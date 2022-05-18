name := """mads-todolist-2017"""

version := "1.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies += javaJpa
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.2.5.Final"
libraryDependencies +=  "mysql" % "mysql-connector-java" % "5.1.18"
libraryDependencies += "org.dbunit" % "dbunit" % "2.4.9"

libraryDependencies += cache
libraryDependencies += javaWs % "test"

libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % "test"
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % "test"
// libraryDependencies += "org.powermock" % "powermock-module-junit4" % "1.7.3" % "test"
// libraryDependencies += "org.powermock" % "powermock-api-mockito2" % "1.7.3" % "test"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.0"
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")
