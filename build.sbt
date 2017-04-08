import Dependencies._
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.{fullOptJS, packageMinifiedJSDependencies}

lazy val commonSettings = Seq(
  organization := "de.fnordeingang",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val scalacSettings = Seq(
  scalacOptions := Seq("-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-target:jvm-1.8",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture")
)

lazy val assemblySetting = Seq(
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("META-INF", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
    case "codegen.json" => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

lazy val donatr = (project in file("."))
  .aggregate(donatrCore, donatrUi, http4sServer)
  .settings(
    commonSettings,
    assemblySetting,
    herokuAppName in Compile := "donatr",
    herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value),
    mainClass in assembly := Some("donatr.DonatrHttp4sServer")
  ).dependsOn(donatrCore, http4sServer)

lazy val donatrCore = (project in file("./donatr-core")).
  settings(
    commonSettings,
    scalacSettings,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "com.typesafe.slick" %% "slick" % "3.2.0-M2",
      "org.slf4j" % "slf4j-api" % "1.7.22",
      "ch.qos.logback" % "logback-classic" % "1.1.10",
      "com.h2database" % "h2" % "1.4.193",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test,
      "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test
    )
  )

/*lazy val donatrMigration = (project in file("./donatr-migration")).
  settings(
    resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
    commonSettings,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "org.slf4j" % "slf4j-api" % "1.7.22",
      Dependencies.vertxLangScala.exclude("io.vertx", "vertx-codegen"),
      Dependencies.vertxCodegen,
      Dependencies.vertxWeb.exclude("io.vertx", "vertx-codegen"),
      "ch.qos.logback" % "logback-classic" % "1.1.10"
    )
  ).dependsOn(donatrCore)*/

lazy val http4sServer = (project in file("./http4s-server")).
  settings(
    mainClass in assembly := Some("donatr.DonatrHttp4sServer"),
    commonSettings,
    assemblySetting,
    scalacSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % "0.15.4a",
      "org.http4s" %% "http4s-dsl" % "0.15.4a",
      "org.http4s" %% "http4s-circe" % "0.15.4a",
      "io.circe" %% "circe-generic" % "0.7.0",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    )
  ).dependsOn(donatrCore).dependsOn(donatrUi)

lazy val donatrUi = (project in file("./donatr-ui")).
  settings(
    commonSettings,
    assemblySetting,
    libraryDependencies ++= Seq(
      "in.nvilla" %%% "monadic-html" % "0.3.0",
      scalaTest % Test,
      "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test"
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "spark-md5" % "2.0.2" / "spark-md5.js"
    ),
    emitSourceMaps := true,
    artifactPath in(Compile, fastOptJS) :=
      ((crossTarget in(Compile, fastOptJS)).value / "classes" / "webroot" /
        ((moduleName in fastOptJS).value + "-opt.js")),
    artifactPath in(Compile, fullOptJS) :=
      ((crossTarget in(Compile, fullOptJS)).value / "classes" / "webroot" /
        ((moduleName in fullOptJS).value + "-opt.js")),
    artifactPath in(Compile, packageMinifiedJSDependencies) :=
      ((crossTarget in(Compile, packageMinifiedJSDependencies)).value / "classes" / "webroot" /
        ((moduleName in packageMinifiedJSDependencies).value + "-jsdeps.js")),
    artifactPath in(Compile, packageJSDependencies) :=
      ((crossTarget in(Compile, packageJSDependencies)).value / "classes" / "webroot" /
        ((moduleName in packageJSDependencies).value + "-jsdeps.js"))
  ).enablePlugins(ScalaJSPlugin)
