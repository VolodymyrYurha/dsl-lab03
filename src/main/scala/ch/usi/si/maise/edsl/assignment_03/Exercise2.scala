package ch.usi.si.maise.edsl.assignment_03

import model.*
import model.CommandModel.*
import model.ProcessModel.*
import model.AsyncContext
import scala.Conversion.into
import scala.concurrent.Future

object ProcessDSL:

  // ** Do not touch this **
  import CommandDSL.*
  import AsyncContext.{given, *}

  // ** Implement the DSL below **

end ProcessDSL

@main def exercise2() =

  // DO NOT touch this
  import CommandDSL.{given, *}
  import ProcessDSL.{given, *}
  import AsyncContext.{given, *}

  // Exercise 2, uncomment below

  // val java = cmd("java")
  // val git = cmd("git")

  // val run1: into[Future[ProcessResult]] = java(-"version")

  // val run2 = git("hash-object", --("stdin")) `with` stdin == "hello\n"

  // val run3 = git("var", "GIT_AUTHOR_IDENT") `with` env == (
  //   "GIT_AUTHOR_NAME" -> "Neo",
  //   "GIT_AUTHOR_EMAIL" -> "neo@nebuchadnezzar.org"
  // )

  // val run4 =
  //   git("rev-parse", --("show-toplevel")) `with` workdir == tmp and env == (
  //     "LC_ALL" -> "C"
  //   )

  // val run5 = git("teleport")

  // val run6 = cmd("gti")("status")

  // val run7 =
  //   git("status", -"s") `with` workdir == root / "no" / "such" / "directory"

  // Do not touch this, just uncomment below.
  // executeInSequence(
  //   run1,
  //   run2,
  //   run3,
  //   run4,
  //   run5,
  //   run6,
  //   run7
  // )
end exercise2
