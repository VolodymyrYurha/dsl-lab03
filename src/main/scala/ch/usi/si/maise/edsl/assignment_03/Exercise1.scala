package ch.usi.si.maise.edsl.assignment_03

import model.*
import model.CommandModel.*

object CommandDSL:

  // Implement the DSL below.

end CommandDSL

@main def exercise1() =
  // DO NOT touch this
  import CommandDSL.{given, *}

  // Uncomment the lines below.

  // // the first word of a command, to be reused.
  // val git = cmd("git")

  // // a command with no arguments, equivalent to: git
  // val noArguments = git()

  // /** A command with a positional argument and a flag, equivalent to:
  //   * git status -s
  //   */
  // val status = git("status", -"s")

  // /** A more complex example, equivalent to:
  //   * git log --oneline -n 3 --author neo
  //   */
  // val log = git(
  //   "log",
  //   --("oneline"),
  //   -"n" := 3,
  //   --("author") := "neo"
  // )

  // /** Positional arguments can be numbers, and options can follow them:
  //   * head -c 16 README.md
  //   */
  // val head = cmd("head")(-"c" := 16, "README.md")

  // /** The executable does not need to be named first: java -version */
  // val javaVersion = cmd("java")(-"version")

  // println(noArguments)
  // println(status)
  // println(log)
  // println(head)
  // println(javaVersion)

end exercise1
