/*
 * Copyright 2016 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

val algebirdVersion = "0.13.0"
val breezeVersion = "0.13.1"
val commonsMathVersion = "3.6.1"
val hadoopVersion = "2.8.0"
val scalacheckVersion = "1.13.5"
val scalatestVersion = "3.0.1"
val scaldingVersion = "0.17.0"
val scioVersion = "0.3.1"
val sparkVersion = "2.1.1"

val commonSettings = Seq(
  organization := "com.spotify",
  name := "featran",
  description := "Feature Transformers",
  scalaVersion := "2.11.11",
  scalacOptions ++= Seq("-target:jvm-1.7", "-deprecation", "-feature", "-unchecked"),
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked"),

  // Release settings
  releaseCrossBuild             := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle             := true,
  publishArtifact in Test       := false,
  sonatypeProfileName           := "com.spotify",
  pomExtra                      := {
    <url>https://github.com/spotify/ratatool</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com/spotify/ratatool.git</url>
      <connection>scm:git:git@github.com:spotify/ratatool.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sinisa_lyh</id>
        <name>Neville Li</name>
        <url>https://twitter.com/sinisa_lyh</url>
      </developer>
      <developer>
        <id>rwhitcomb</id>
        <name>Richard Whitcomb</name>
        <url>https://twitter.com/rwhitcomb</url>
      </developer>
    </developers>
  }
)

val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val root: Project = Project(
  "root",
  file(".")
).settings(
  commonSettings ++ noPublishSettings
).aggregate(
  core,
  scalding,
  scio,
  spark
)

lazy val core: Project = Project(
  "core",
  file("core")
).settings(
  moduleName := "featran-core",
  commonSettings,
  description := "Feature Transformers",
  libraryDependencies ++= Seq(
    "com.twitter" %% "algebird-core" % algebirdVersion,
    "org.apache.commons" % "commons-math3" % commonsMathVersion,
    "org.scalanlp" %% "breeze" % breezeVersion,
    "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test"
  )
)

lazy val scalding: Project = Project(
  "scalding",
  file("scalding")
).settings(
  moduleName := "featran-scalding",
  commonSettings,
  description := "Feature Transformers - Scalding",
  resolvers += "Concurrent Maven Repo" at "http://conjars.org/repo",
  libraryDependencies ++= Seq(
    "com.twitter" %% "scalding-core" % scaldingVersion % "provided",
    "org.apache.hadoop" % "hadoop-client" % hadoopVersion % "provided",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  )
).dependsOn(core)

lazy val scio: Project = Project(
  "scio",
  file("scio")
).settings(
  moduleName := "featran-scio",
  commonSettings,
  description := "Feature Transformers - Scio",
  libraryDependencies ++= Seq(
    "com.spotify" %% "scio-core" % scioVersion,
    "com.spotify" %% "scio-test" % scioVersion % "test"
  )
).dependsOn(core)

lazy val spark: Project = Project(
  "spark",
  file("spark")
).settings(
  moduleName := "featran-spark",
  commonSettings,
  description := "Feature Transformers - Spark",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  )
).dependsOn(core)
