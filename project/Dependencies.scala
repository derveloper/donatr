import sbt._


object Version {
  final val Scala       = "2.12.1"
  final val ScalaTest   = "3.0.1"
  final val Vertx       = "3.4.0-SNAPSHOT"
}

object Dependencies {
  // val vertxCodegen   = "io.vertx"       %  "vertx-codegen"    % Version.Vertx     % "provided" changing()
  val vertxLangScala = ("io.vertx"       %% "vertx-lang-scala" % Version.Vertx                  changing())
    .exclude("io.vertx", "vertx-codegen")
  val vertxWeb       = ("io.vertx"       %% "vertx-web-scala"  % Version.Vertx                  changing())
    .exclude("io.vertx", "vertx-codegen")
  lazy val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
