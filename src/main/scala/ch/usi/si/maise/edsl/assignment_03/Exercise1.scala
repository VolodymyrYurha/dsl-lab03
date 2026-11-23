package ch.usi.si.maise.edsl.assignment_03

import model.*
import model.CommandModel.*

object CommandDSL:

  // Implement the DSL below.

  case class Executable(name: String):
    def apply(arguments: Argument*): Command =
      Command(name, arguments.toList)

  def cmd(name: String): Executable = Executable(name)

  given Conversion[String, Argument] = 
    (paramStr: String) => Positional(paramStr)

  extension (name: String)
    def unary_- : ShortFlag = ShortFlag(name)

  def --(name: String): LongFlag = LongFlag(name)

  extension (flag: Flag)
    def :=(value: Int): OptionWithValue = OptionWithValue(flag, value.toString()) 
    def :=(value: String): OptionWithValue = OptionWithValue(flag, value)

end CommandDSL

@main def exercise1() =
  // DO NOT touch this
  import CommandDSL.{given, *}

  // Uncomment the lines below.

  // the first word of a command, to be reused.
  val git = cmd("git")

  // a command with no arguments, equivalent to: git
  val noArguments = git()
  println(noArguments)

  /** A command with a positional argument and a flag, equivalent to:
    * git status -s
    */
  // its basically syntax sugar for the git.apply(...) so basically calling a method over the object directly 
  val status = git("status", -"s")
  println(status)

  val status2 = git("status", -"s", --("long_par"))
  println(status2)

  /** A more complex example, equivalent to:
    * git log --oneline -n 3 --author neo
    */
  val log = git(
    "log",
    --("oneline"),
    -"n" := 3,
    --("author") := "neo"
  )
  println(log)

  /** Positional arguments can be numbers, and options can follow them:
    * head -c 16 README.md
    */
  val head = cmd("head")(-"c" := 16, "README.md")
  println(head)

  /** The executable does not need to be named first: java -version */
  val javaVersion = cmd("java")(-"version")
  println(javaVersion)

end exercise1
