import ReleaseTransformations.*
import Dependencies.*

ThisBuild / organization := "com.madgag"

ThisBuild / scalaVersion := "2.13.16"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps", "-release:11")

ThisBuild / licenses := Seq(License.GPL3_or_later)

ThisBuild / resolvers ++= jgitVersionOverride.map(_ => Resolver.mavenLocal).toSeq

ThisBuild / libraryDependencies += scalatest % Test

ThisBuild / Test/ testOptions += Tests.Argument(
  TestFrameworks.ScalaTest,
  "-u", s"test-results/scala-${scalaVersion.value}"
)

lazy val root = Project(id = "bfg-parent", base = file(".")).aggregate (bfg, `bfg-test`, `bfg-library`).settings(
  publish / skip := true,
  releaseCrossBuild := true, // true if you cross-build the project for multiple Scala versions
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion
  )
)

lazy val commonAssemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    // Discard everything under META-INF (including module-info.class files)
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

lazy val `bfg-test` = project.settings(commonAssemblySettings: _*)

lazy val `bfg-library` = project
  .dependsOn(`bfg-test` % Test)
  .settings(commonAssemblySettings: _*)

lazy val bfg = project
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(`bfg-library`, `bfg-test` % Test)
  .settings(commonAssemblySettings: _*)

lazy val `bfg-benchmark` = project.settings(commonAssemblySettings: _*)

