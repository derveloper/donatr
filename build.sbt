import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "de.fnordeingang",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq("-deprecation")
    )),
    name := "DonatrServer",
    resolvers += "twttr" at "https://maven.twttr.com/",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % "0.12.0",
      "com.github.finagle" %% "finch-circe" % "0.12.0",
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "com.twitter" %% "twitter-server" % "1.26.0",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.h2database" % "h2" % "1.4.193",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    ),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    )
  )
