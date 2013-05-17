import com.jsuereth.sbtsite.SiteKeys

name := "scaldi"

organization := "org.angelsmasterpiece.scaldi"

version := "0.2-SNAPSHOT"

crossScalaVersions := Seq("2.10.0")

scalaVersion := "2.10.0"

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

seq(site.settings:_*)

seq(ghpages.settings:_*)

git.remoteRepo := "git://github.com/OlegIlyenko/scaldi.git"

site.addMappingsToSiteDir(mappings in packageDoc in Compile, "api")

SiteKeys.siteMappings <<=
  (SiteKeys.siteMappings, PamfletKeys.write, PamfletKeys.output) map { (mappings, _, dir) =>
    mappings ++ (dir ** "*.*" x relativeTo(dir))
  }

pomExtra := <xml:group>
  <inceptionYear>2011</inceptionYear>
  <name>Scaldi - Scala Dependency Injection Framework</name>
  <licenses>
    <license>
      <name>Apache License, ASL Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>OlegIlyenko</id>
      <name>Oleg Ilyenko</name>
    </developer>
  </developers>
  <issueManagement>
    <system>GitHub</system>
    <url>http://github.com/OlegIlyenko/scaldi/issues</url>
  </issueManagement>
  <scm>
    <connection>scm:git://github.com/OlegIlyenko/scaldi.git</connection>
    <url>http://github.com/OlegIlyenko/scaldi/tree/master</url>
  </scm>
</xml:group>
