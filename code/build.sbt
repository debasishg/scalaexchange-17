val catsVersion = "1.0.0-MF"
val catsEffectVersion = "0.4"
val configVersion = "1.3.1"

val catsCore = "org.typelevel" % "cats-core_2.12" % catsVersion
val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

val macroParadise = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
val kindProjector = compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
val resetAllAttrs = "org.scalamacros" %% "resetallattrs" % "1.0.0-M1"

val typesafeConfig = "com.typesafe" % "config" % configVersion

val specs2Version = "3.8.9" // use the version used by discipline
val specs2Core  = "org.specs2" %% "specs2-core" % specs2Version
val specs2Scalacheck = "org.specs2" %% "specs2-scalacheck" % specs2Version
val scalacheck = "org.scalacheck" %% "scalacheck" % "1.12.4"

lazy val commonSettings = Seq(
  version := "0.0.1",
  resolvers ++= Seq(
      Resolver.mavenLocal
    , Resolver.sonatypeRepo("releases")
    , Resolver.sonatypeRepo("snapshots")
  ),
  scalaVersion := "2.12.3",
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  libraryDependencies ++= Seq(
      catsCore, catsEffect,
      specs2Core % Test, specs2Scalacheck % Test, scalacheck % Test,
      macroParadise, kindProjector,
      typesafeConfig
    )
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "scalaex",
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-language:higherKinds",
      "-language:postfixOps",
      "-deprecation"
    )
  )
