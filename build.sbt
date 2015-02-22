name := "wLibrary_j"

version := "1.0"

//scalacOptions += "-target:jvm-1.7"

// append several options to the list of options passed to the Java compiler
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

//scalaVersion := "2.11.5"

// disable scala-library dependency
autoScalaLibrary := false

// disable using the Scala version in output paths and artifacts
crossPaths := false

// fork a new JVM for 'run' and 'test:run'
//fork := true

// fork a new JVM for 'test:run', but not 'run'
//fork in Test := true

// add a JVM option to use when forking a JVM for 'run'
//javaOptions += "-Xmx2G"

// only use a single thread for building
//parallelExecution := false

// Execute tests in the current project serially
//   Tests from other projects may still run concurrently.
//parallelExecution in Test := false

// set the location of the JDK to use for compiling Java code.
// if 'fork' is true, this is used for 'run' as well
//javaHome := Some(file("/usr/lib/jvm/sun-jdk-1.6"))

// Use Scala from a directory on the filesystem instead of retrieving from a repository
//scalaHome := Some(file("/home/user/scala/trunk/"))

libraryDependencies += "junit" % "junit" % "3.8.1"

unmanagedBase := file("/Volumes/Develop/lib/")

EclipseKeys.withSource := true

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

scalaSource in Compile := baseDirectory.value / "src"

javaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test-src"

javaSource in Test := baseDirectory.value / "test-src"

lazy val wLibrary_j = project.in(file("."))

