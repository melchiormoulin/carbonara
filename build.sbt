val VERSION = "0.0.1"

lazy val commonSettings = Seq(
  organization := "com.criteo.carbonara",
  version := VERSION,
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq(scalaVersion.value),
  scalacOptions ++= {
    Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint",
      "-Xlint:-inaccessible",
      "-Yno-adapted-args",
      "-Ypartial-unification",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Ywarn-unused-import",
      "-Ywarn-macros:after"
    )
  },

  // Tests
  fork in Test := true,
)