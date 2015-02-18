name := "aam-apis-hands-on"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "commons-httpclient" % "commons-httpclient" % "3.1"
)     

play.Project.playJavaSettings
