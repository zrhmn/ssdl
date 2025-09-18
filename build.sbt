import sbt.*

ThisBuild / organization := "io.github.zrhmn"
ThisBuild / homepage     := Some(url("https://github.com/zrhmn/ssdl"))
ThisBuild / licenses     := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / developers := List(
  Developer(
    "zrhmn",
    "SSDL Authors",
    "ssdl@example.com",
    url("https://github.com/zrhmn"),
  ),
)

// Sonatype publishing settings
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"

lazy val root = project
    .in(file("."))
    .settings(
      scalaVersion := "3.7.3",
      name         := "ssdl",
      description  := "Scala domain-specific language for describing complex systems.",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core"     % "2.13.0",
        "io.circe"      %% "circe-core"    % "0.14.6",
        "io.circe"      %% "circe-generic" % "0.14.6",
        "io.circe"      %% "circe-parser"  % "0.14.6",
        "org.typelevel" %% "squants"       % "1.8.3",
        "org.scalameta" %% "munit"         % "1.0.4" % Test,
      ),
    )
    .enablePlugins(DynVerPlugin, BuildInfoPlugin)
    .settings(
      buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, libraryDependencies),
      buildInfoPackage := "io.github.zrhmn.sdl",
      buildInfoOptions ++= Seq(BuildInfoOption.BuildTime, BuildInfoOption.ConstantValue),
    )
