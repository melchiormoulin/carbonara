val VERSION = "0.0.2-SNAPSHOT"

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

  // Publishing
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    "criteo-oss",
    sys.env.getOrElse("SONATYPE_PASSWORD", "")
  ),
  publishTo := Some(
    if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
    else                  Opts.resolver.sonatypeStaging
  ),
  useGpg := false,
  pgpPassphrase := sys.env.get("SONATYPE_PASSWORD").map(_.toArray),
  pgpSecretRing := file((ThisBuild / baseDirectory).value + "/.travis/secring.gpg"),
  pgpPublicRing := file((ThisBuild / baseDirectory).value + "/.travis/pubring.gpg"),
  pomExtra in Global := {
    <url>https://github.com/criteo/carbonara</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/criteo/carbonara.git</connection>
      <developerConnection>scm:git:git@github.com:criteo/carbonara.git</developerConnection>
      <url>github.com/criteo/carbonara</url>
    </scm>
    <developers>
      <developer>
        <name>Criteo SRE Observability team</name>
        <email>sre-observability@criteo.com</email>
        <url>https://github.com/criteo</url>
        <organization>Criteo</organization>
        <organizationUrl>http://www.criteo.com</organizationUrl>
      </developer>
    </developers>
  }
)

val versions = new {
  val prometheus = "0.6.0"
  val scalatest = "3.0.5"
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
    name := "carbonara-relay",
    commonSettings,
    logging,
    prometheus,
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.0.0-RC2"
    ),
    testLibraries,
    mainClass in(Compile, run) := Some("carbonara.relay.Run")
  )

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    publishArtifact := false,
  )
  .aggregate(relay)
