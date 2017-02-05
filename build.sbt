import Dependencies._


lazy val donatr = (project in file("."))
  .aggregate(donatrCore, vertxServer)
  .settings(
    inThisBuild(List(
      organization := "de.fnordeingang",
      scalaVersion := "2.12.1",
      version := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq("-deprecation"),
      resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/"
    ))
  ).dependsOn(donatrCore, vertxServer)

lazy val donatrCore = (project in file("./donatr-core")).
  settings(
    resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "com.typesafe.slick" %% "slick" % "3.2.0-M2",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.h2database" % "h2" % "1.4.193",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    )
  )

/*lazy val finchServer = (project in file("./finch-server")).
  settings(
    name += "DonatrServer",
    resolvers += "twttr" at "https://maven.twttr.com/",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % "0.12.0",
      "com.github.finagle" %% "finch-circe" % "0.12.0",
      "com.github.finagle" %% "finch-sse" % "0.12.0",
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "com.twitter" %% "twitter-server" % "1.26.0",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    ),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    )
  ).dependsOn(donatrCore)*/

lazy val vertxServer = (project in file("./vertx-server")).
  settings(
    resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
    libraryDependencies ++= Seq(
      Dependencies.vertxLangScala,
      Dependencies.vertxCodegen,
      Dependencies.vertxWeb,
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    )
  ).dependsOn(donatrCore)
