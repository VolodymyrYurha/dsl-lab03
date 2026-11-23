val scala3Version = "3.9.0"

// Warns until the developers section below is filled in.
val checkDevelopers = taskKey[Unit]("Checks that the developers section is filled in")

lazy val root = project
  .in(file("."))
  .settings(
    organization := "ch.usi.si.maise.edsl",
    // Replace with your data: one Developer(...) per group member.
    developers := List(Developer("firstname.lastname", "FirstName LastName", "firstanme.lastname@usi.ch", url("https://yourwebsite.com"))),
    name := "assignment-03-template",
    version := "2026.1.0",
    scalaVersion := scala3Version,
    scalacOptions += "-feature",
    scalacOptions += "-deprecation",
    fork := true,
    checkDevelopers := Def.uncached {
      val placeholders = Set("firstname.lastname", "FirstName LastName", "firstanme.lastname@usi.ch")
      val devs = developers.value
      val notFilled = devs.isEmpty || devs.exists: dev =>
        Seq(dev.id, dev.name, dev.email).exists: field =>
          field.trim.isEmpty || placeholders.contains(field)
      if (notFilled)
        streams.value.log.warn("Fill in the developers section of build.sbt with your data (one Developer per group member).")
    },
    Compile / compile := Def.uncached((Compile / compile).dependsOn(checkDevelopers).value)
  )
