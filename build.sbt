ThisBuild / scalaVersion        := "3.2.2"
ThisBuild / organization        := "com.vzxplnhqr"
ThisBuild / homepage            := Some(url("https://github.com/VzxPLnHqr/fliptxo"))
ThisBuild / licenses            += License.Apache2
ThisBuild / developers          := List(tlGitHubDev("VzxPLnHqr", "VzxPLnHqr"))

ThisBuild / version             := "0.0.1"
//ThisBuild / tlSonatypeUseLegacyHost := false

Global / onChangedBuildSource := ReloadOnSourceChanges

//lazy val root = tlCrossRootProject.aggregate(scoin)

lazy val fliptxo = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("."))
  .settings(
    name := "fliptxo",
    description := "Demo of non-interactive utxo cycling (flipping)",
    libraryDependencies ++= Seq(
      "com.fiatjaf" %%% "scoin" % "0.7.0-SNAPSHOT",
      "com.armanbilge" %%% "calico" % "0.2.0-RC2",

      "com.lihaoyi" %%% "utest" % "0.8.0" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    crossScalaVersions := List("3.2.0"),
  )
  .jsConfigure { _.enablePlugins(NpmDependenciesPlugin) }
  .jsSettings(
    scalaVersion := "3.2.0",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    scalaJSUseMainModuleInitializer := true

  )
  .nativeSettings(
    scalaVersion := "3.2.0",
    nativeConfig ~= { _.withEmbedResources(true) }
  )