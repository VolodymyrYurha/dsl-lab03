package ch.usi.si.maise.edsl.assignment_03

import scala.Conversion.into
import scala.concurrent.Future
import model.ProcessModel.*
import model.AssertionModel.*
import model.AssertionExecutor

trait ProcessAssertionDSL extends AssertionExecutor:

  // Do not touch this import
  import model.AsyncContext.*

  // Write your DSL below

end ProcessAssertionDSL

class Exercise3_Example extends ProcessAssertionDSL:

  // Do not touch this
  import CommandDSL.{given, *}
  import ProcessDSL.{given, *}
  import model.AsyncContext.{given, *}

  // Exercise 3: exit/fail assertions
  // uncomment below

  // val java = cmd("java")
  // val git = cmd("git")

  // "java -version" should "exit with 0" += eventually (
  //   java(-"version")
  // ) should exit `with` exitStatus == 0

  // "java --version" should "print on the standard output only" += eventually (
  //   java(--("version"))
  // ) should exit `with` exitStatus == 0 & stderr == ""

  // "hashing hello" should "print the id of the blob" += eventually (
  //   git("hash-object", --("stdin")) `with` stdin == "hello\n"
  // ) should exit `with` stdout == "ce013625030ba8dba906f756967f9e9ca394464a"

  // "the author identity" should "come from the environment" += eventually (
  //   git("var", "GIT_AUTHOR_IDENT") `with` env == (
  //     "GIT_AUTHOR_NAME" -> "Neo",
  //     "GIT_AUTHOR_EMAIL" -> "neo@nebuchadnezzar.org"
  //   )
  // ) should exit `with` stdout containing "Neo <neo@nebuchadnezzar.org>"

  // "git outside a repository" should "exit with 128 or 129" += eventually (
  //   git("rev-parse", --("show-toplevel")) `with` workdir == tmp and env == (
  //     "LC_ALL" -> "C"
  //   )
  // ) should exit `with` exitStatus == 128 | exitStatus == 129 & (stderr containing "not a git repository")

  // "an unknown git command" should "exit with an error and complain" += eventually (
  //   git("teleport")
  // ) should exit `with` exitStatus > 0 & exitStatus < 128 & (stderr containing "is not a git command")

  // "a program that does not exist" should "fail" += eventually (
  //   cmd("gti")("status")
  // ) should fail

  // "a command in a directory that does not exist" should "fail" += eventually (
  //   git("status", -"s") `with` workdir == root / "no" / "such" / "directory"
  // ) should fail

  // do ** NOT ** remove this
  run()
end Exercise3_Example

@main def exercise3 = Exercise3_Example()
