import Dependencies._


lazy val donatr = (project in file("."))
  .aggregate(donatrCore, vertxServer, donatrUi)
  .settings(
    inThisBuild(List(
      organization := "de.fnordeingang",
      scalaVersion := "2.12.1",
      version := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq("-deprecation", "-feature"),
      resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
      test in assembly := {}
    )),
    herokuAppName in Compile := "donatr",
    herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value),
    mainClass in assembly := Some("donatr.DonatrVertxServer"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
      case "codegen.json" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ).dependsOn(donatrCore, vertxServer, donatrUi)

lazy val donatrCore = (project in file("./donatr-core")).
  settings(
    resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
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
      "io.circe" %%% "circe-core" % "0.7.0",
      "io.circe" %%% "circe-generic" % "0.7.0",
      "io.circe" %%% "circe-parser" % "0.7.0"
    )
  ).enablePlugins(ScalaJSPlugin)

lazy val vertxServer = (project in file("./vertx-server")).
  settings(
    resolvers += "Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots/",
    mainClass in assembly := Some("donatr.DonatrVertxServer"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
      case "codegen.json" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    libraryDependencies ++= Seq(
      Dependencies.vertxLangScala.exclude("io.vertx", "vertx-codegen"),
      Dependencies.vertxCodegen,
      Dependencies.vertxWeb.exclude("io.vertx", "vertx-codegen"),
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    )
  ).dependsOn(donatrCore)

lazy val donatrUi = (project in file("./donatr-ui")).
  settings(
    jsEnv := PhantomJSEnv().value,
    libraryDependencies ++= Seq(
      "in.nvilla" %%% "monadic-html" % "latest.integration",
      "com.github.japgolly.scalacss" %%% "core" % "0.5.1",
      //"org.webjars.npm" %%% "spark-md5" % "3.0.0",
      scalaTest % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
    ),
    jsDependencies += "org.webjars.npm" % "spark-md5" % "2.0.2" / "spark-md5.js",
    emitSourceMaps := true,
    artifactPath in (Compile, fastOptJS) :=
      ((crossTarget in (Compile, fastOptJS)).value /
        ((moduleName in fastOptJS).value + "-opt.js")),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  ).enablePlugins(ScalaJSPlugin)
