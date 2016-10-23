import scala.util.Random
import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text

object ScalaFXHelloWorld extends JFXApp {

  val SCREEN_WIDTH = 800
  val SCREEN_HEIGHT = 600

  val PADDLE_SPEED: Double = 100.0
  val INITIAL_BALL_SPEED = 1000.0

  val PADDLE_SIZE = 200

  val END_SCREEN_TIME = 10000

  stage = new PrimaryStage {
    title = "Pong"
    maxHeight = SCREEN_HEIGHT + 20
    maxWidth = SCREEN_WIDTH + 20
    scene = new Scene(SCREEN_WIDTH, SCREEN_HEIGHT) {
      fill = Color.rgb(38, 38, 38)
      val ball = Circle(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 20)
      ball.fill = Color.Yellow

      val paddle1 = Rectangle(0, SCREEN_HEIGHT / 2, 10, PADDLE_SIZE)
      paddle1.fill = Color.Blue


      val paddle2 = Rectangle(SCREEN_WIDTH - 10, SCREEN_HEIGHT / 2, 10, PADDLE_SIZE)
      paddle2.fill = Color.Red

      // per game variables
      var redScore: Int = 0
      var blueScore: Int = 0


      val backGround = new HBox {
        padding = Insets(150, 80, 50, 120)
        children = Seq(
          new Text {
            text = "Pong"
            style = "-fx-font: normal bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Red, DarkRed))
          },
          new Text {
            text = "FX"
            style = "-fx-font: italic bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(White, DarkGray)
            )
            effect = new DropShadow {
              color = DarkGray
              radius = 15
              spread = 0.25
            }
          }
        )
      }


      val redWins = new HBox {
        padding = Insets(150, 80, 50, 70)
        children = Seq(
          new Text {
            text = "Red Wins"
            style = "-fx-font: normal bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Red, DarkRed))
          },
          new Text {
            text = "!"
            style = "-fx-font: italic bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(White, DarkGray)
            )
            effect = new DropShadow {
              color = DarkGray
              radius = 15
              spread = 0.25
            }
          }
        )
      }

      val blueWins = new HBox {
        padding = Insets(150, 80, 50, 60)
        children = Seq(
          new Text {
            text = "Blue Wins"
            style = "-fx-font: normal bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Blue, DarkBlue))
          },
          new Text {
            text = "!"
            style = "-fx-font: italic bold 100pt sans-serif"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(White, DarkGray)
            )
            effect = new DropShadow {
              color = DarkGray
              radius = 15
              spread = 0.25
            }
          }
        )
      }

      val continueText = new Text {
        text = "Press Space bar to Continue"
        style = "-fx-font: normal bold 12pt sans-serif"
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(White, DarkGray))
      }
      continueText.x = SCREEN_WIDTH / 2 - 100
      continueText.y = SCREEN_HEIGHT - 50

      val redScoreDisplay = new Text {
        text = "0"
        style = "-fx-font: normal bold 100pt sans-serif"
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(Red, DarkRed))
      }
      redScoreDisplay.x = SCREEN_WIDTH - 70
      redScoreDisplay.y = 100

      val blueScoreDisplay = new Text {
        text = "0"
        style = "-fx-font: normal bold 100pt sans-serif"
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(Blue, DarkBlue))
      }
      blueScoreDisplay.x = 0
      blueScoreDisplay.y = 100

      // per match variables
      blueWins.visible = false
      redWins.visible = false
      continueText.visible = false
      backGround.visible = true
      var lastTime = 0L
      var ballDeltaX = 0.8
      var ballDeltaY = 0.2
      ball.centerX = SCREEN_WIDTH / 2
      ball.centerY = SCREEN_HEIGHT / 2
      paddle1.y = SCREEN_HEIGHT / 2
      paddle2.y = SCREEN_HEIGHT / 2
      var ballSpeed = INITIAL_BALL_SPEED
      var finished = false

      content = List(continueText, redScoreDisplay, blueScoreDisplay, redWins, blueWins, backGround, ball, paddle1, paddle2)



      onKeyReleased = (e: KeyEvent) => {
        if (e.code == KeyCode.Up && paddle2.y() > 0) paddle2.y = paddle2.y() - PADDLE_SPEED
        else if (e.code == KeyCode.Down && paddle2.y() < SCREEN_HEIGHT - PADDLE_SIZE) paddle2.y = paddle2.y() + PADDLE_SPEED
        else if (e.code == KeyCode.W && paddle1.y() > 0) paddle1.y = paddle1.y() - PADDLE_SPEED
        else if (e.code == KeyCode.S && paddle1.y() < SCREEN_HEIGHT - PADDLE_SIZE) paddle1.y = paddle1.y() + PADDLE_SPEED
        else if (e.code == KeyCode.Space) {
          restart
        }
      }


      private def restart: Unit = {
        if (finished) {
          finished = false
          blueWins.visible = false
          redWins.visible = false
          continueText.visible = false
          backGround.visible = true
          lastTime = 0L
          ballDeltaX = 0.8
          ballDeltaY = 0.2
          ball.centerX = SCREEN_WIDTH / 2
          ball.centerY = SCREEN_HEIGHT / 2
          paddle1.y = SCREEN_HEIGHT / 2
          paddle2.y = SCREEN_HEIGHT / 2
          ballSpeed = INITIAL_BALL_SPEED
        }
      }

      val ballTimer = AnimationTimer(t => {

        // slightly changes the balls direction
        def wiggle: Unit = {
          val rand = new Random(1)
          var wiggle = rand.nextDouble() / 10
          if (redScore > blueScore) {
            ballDeltaX += wiggle
            ballDeltaY -= wiggle
          } else if (blueScore > redScore) {
            ballDeltaX -= wiggle
            ballDeltaY += wiggle
          }
        }
        if (lastTime > 0) {
          val timeDelta: Double = (t - lastTime) / 1e9

          ballSpeed *= 1.001


          if (ball.getBoundsInParent.intersects(paddle1.boundsInParent())) {
            ballDeltaX = Math.abs(ballDeltaX)
            wiggle

          }

          if (ball.getBoundsInParent.intersects(paddle2.boundsInParent())) {
            ballDeltaX = -Math.abs(ballDeltaX)
            wiggle

          }

          if ( ball.getCenterY < (0 + ball.getRadius) ) {
            ballDeltaY = Math.abs(ballDeltaY)
          } else if (ball.getCenterY > (SCREEN_HEIGHT - ball.getRadius)) {
            ballDeltaY = -Math.abs(ballDeltaY)
          }





          ball.centerX = ball.centerX() + (ballSpeed * ballDeltaX * timeDelta)
          ball.centerY = ball.centerY() + (ballSpeed * ballDeltaY * timeDelta)


          // check for red win
          if (ball.getCenterX < 0 - ball.getRadius && !finished) {
            backGround.visible = false
            redWins.visible = true
            blueWins.visible = false
            continueText.visible = true
            redScore += 1
            redScoreDisplay.text = redScore.toString
            redScoreDisplay.x = SCREEN_WIDTH - (70 * ((redScore / 10).toInt + 1))
            finished = true
          }

          // check for blue win
          else if (ball.getCenterX > SCREEN_WIDTH + ball.getRadius && !finished) {
            backGround.visible = false
            redWins.visible = false
            blueWins.visible = true
            continueText.visible = true
            blueScore += 1
            blueScoreDisplay.text = blueScore.toString
            finished = true
          }
        }
        lastTime = t
      })
      ballTimer.start()

    }


  }

}