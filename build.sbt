import SPSettings._

lazy val projectName = "sp-domain"
lazy val projectVersion = "0.9.11"

lazy val buildSettings = Seq(
  name         := projectName,
  description  := "The domain and logic to work with it",
  version      := projectVersion,
  libraryDependencies ++= domainDependencies.value,
  scmInfo := Some(ScmInfo(
    PublishingSettings.githubSP(projectName),
    PublishingSettings.githubscm(projectName)
    )
  )
)

lazy val root = project.in(file("."))
  .aggregate(spdomain_jvm, spdomain_js)
  .settings(defaultBuildSettings)
  .settings(buildSettings)
  .settings(
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file("")
    )


lazy val spdomain = (crossProject.crossType(CrossType.Pure) in file("."))
  .settings(defaultBuildSettings)
  .settings(buildSettings)
  .jvmSettings(
    libraryDependencies += "org.joda" % "joda-convert" % "1.8.2"
  )
  .jsSettings(jsSettings)

lazy val spdomain_jvm = spdomain.jvm
lazy val spdomain_js = spdomain.js
