ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
      name := "scala-loci-sse",
      scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-Ymacro-annotations"),
      libraryDependencies ++= Seq(
          //    "io.github.scala-loci" %% "scala-loci-language" % "0.5.0" % "compile-internal",
          "io.github.scala-loci" %% "scala-loci-language" % "0.5.0" % "provided",
          "io.github.scala-loci" %% "scala-loci-language-runtime" % "0.5.0",
          "io.github.scala-loci" %% "scala-loci-language-transmitter-rescala" % "0.5.0",
          "io.github.scala-loci" %% "scala-loci-serializer-upickle" % "0.5.0",
          "io.github.scala-loci" %% "scala-loci-communicator-tcp" % "0.5.0"
      )
  )
