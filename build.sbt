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

val versions = new {
  val prometheus = "0.6.0"
  val scalatest = "3.0.5"
  val circe = "0.10.0"
}

lazy val logging = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
)

lazy val prometheus = Seq(
  libraryDependencies ++= Seq(
    "simpleclient",
    "simpleclient_hotspot", // JVM metrics
    "simpleclient_httpserver" // HTTP server to expose metrics
  ).map("io.prometheus" % _ % versions.prometheus)
)

lazy val testLibraries = Seq(
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic",
    "org.scalatest" %% "scalatest"
  ).map(_ % versions.scalatest % "test")
)

lazy val relay = (project in file("relay"))
  .settings(
    commonSettings,
    logging,
    prometheus,
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.0.0-RC2",
      //For configuration
      "org.apache.commons" % "commons-configuration2" % "2.4",
      "commons-beanutils" % "commons-beanutils" % "1.9.3",
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % versions.circe),
    testLibraries,
    mainClass in(Compile, run) := Some("carbonara.relay.Run")
  )

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    publishArtifact := false,
  )
  .aggregate(relay)