name := "pong"

version := "1.0"

scalaVersion := "2.11.8"


// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"


// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true