package ch.usi.si.maise.edsl.assignment_03.model

/** A simple model for commands, as they are typed on a command line: the name
  * of an executable program followed by its arguments.
  */
object CommandModel:

  /** An argument of a command.
    *
    * It is marked with the into soft modifier, because it can be the target
    * of a given conversion: a plain value can be used as an argument.
    */
  into sealed trait Argument:
    /** The words that this argument contributes to the command line.
      */
    def words: List[String]

    override def toString() = words.mkString(" ")
  end Argument

  /** A positional argument, i.e., a plain value like a subcommand or a file
    * name. For example, status in "git status".
    */
  case class Positional(val value: String) extends Argument:
    def words = List(value)

  /** A flag, i.e., a named argument introduced by one or two dashes.
    */
  sealed trait Flag extends Argument:
    val name: String

  /** A flag introduced by a single dash, like -s in "git status -s".
    *
    * @param name
    *   the name of the flag, without the dash.
    */
  case class ShortFlag(val name: String) extends Flag:
    require(name.nonEmpty && !name.startsWith("-"))
    def words = List(s"-$name")

  /** A flag introduced by two dashes, like --oneline in "git log --oneline".
    *
    * @param name
    *   the name of the flag, without the dashes.
    */
  case class LongFlag(val name: String) extends Flag:
    require(name.nonEmpty && !name.startsWith("-"))
    def words = List(s"--$name")

  /** A flag followed by a value, like -n 3 in "git log -n 3".
    *
    * @param flag
    *   the flag that names the option.
    * @param value
    *   the value of the option.
    */
  case class OptionWithValue(val flag: Flag, val value: String)
      extends Argument:
    def words = flag.words :+ value

  /** A command: an executable program and its arguments.
    *
    * @param executable
    *   the name of the program (looked up in the system path), or its path.
    * @param arguments
    *   the arguments, in the order in which they appear in the command line.
    */
  case class Command(
      val executable: String,
      val arguments: List[Argument] = List()
  ):
    require(executable.nonEmpty)

    /** All the words of the command line, starting with the executable.
      */
    def words: List[String] = executable :: arguments.flatMap(_.words)

    override def toString() = words.mkString(" ")
  end Command

end CommandModel
