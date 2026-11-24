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
  given Conversion[Command, Future[ProcessResult]]  = 
    (command: Command) => ProcessRun(command).perform()

  // abstraction over the domain props
  sealed trait ProcessProperty                                

  case class StdinValue(text: String) extends ProcessProperty
  case class EnvValue(variables: List[(String, String)]) extends ProcessProperty
  case class WorkdirValue(path: Path) extends ProcessProperty

  object stdin:
    def ==(text: String): StdinValue = StdinValue(text)

  object env:
    def ==(variables: (String, String)*): EnvValue = EnvValue(variables.toList)

  object workdir:
    def ==(path: Path): WorkdirValue = WorkdirValue(path)
        
  private def set(run: ProcessRun, property: ProcessProperty): ProcessRun =
    property match
      case StdinValue(text) =>
        require(run.standardInput.isEmpty, "stdin has already been set") 
        run.copy(standardInput = Some(text))
      case EnvValue(vars) =>
        require(run.environment.isEmpty, "env has already been set") // up to discuss case like `env = (..) and env = (..)`
        run.copy(environment = vars)
      case WorkdirValue(path) =>
        require(run.workingDirectory.isEmpty, "workdir has already been set")
        run.copy(workingDirectory = Some(path))

  case class ProcessBuilder(run: ProcessRun):
    infix def `and`(property: ProcessProperty): ProcessBuilder =
        ProcessBuilder(set(run, property)) 

  extension (command: Command)
    infix def `with`(property: ProcessProperty): ProcessBuilder =
      ProcessBuilder(set(ProcessRun(command), property))     

  given Conversion[ProcessBuilder, Future[ProcessResult]] = (builder: ProcessBuilder) => builder.run.perform() 

  val root: Path = Path(PathAnchor.Root)
  val home: Path = Path(PathAnchor.Home)
  val tmp:  Path = Path(PathAnchor.Temp)
  val here: Path = Path(PathAnchor.Current)

  extension (path: Path)
    def /(element: String): Path = path.append(element)
    
  val ~ : Path = home
  extension (element: String)
    def unary_~ : Path = home / element

  val `.` : Path = here
  val `..` : Path = Path(PathAnchor.Current, List(".."))

end ProcessDSL

@main def exercise2() =

  // DO NOT touch this
  import CommandDSL.{given, *}
  import ProcessDSL.{given, *}
  import AsyncContext.{given, *}

  // Exercise 2, uncomment below
  val java = cmd("java")
  val git = cmd("git")

  val run1: into[Future[ProcessResult]] = java(-"version")

  println(env == ("A" -> "B", "C" -> "D") )     
  println(env == (("A", "B"), ("C", "D")))     // tuple can be represented as both (.. -> ..) and (.., ..)

  val run2 = git("hash-object", --("stdin")) `with` stdin == "hello\n"

  val run3 = git("var", "GIT_AUTHOR_IDENT") `with` env == (
    "GIT_AUTHOR_NAME" -> "Neo",
    "GIT_AUTHOR_EMAIL" -> "neo@nebuchadnezzar.org"
  )

  val run4 =
    git("rev-parse", --("show-toplevel")) `with` workdir == tmp and env == (
      "LC_ALL" -> "C"
    )

  val run5 = git("teleport")

  val run6 = cmd("gti")("status")

  val run7 =
    git("status", -"s") `with` workdir == root / "no" / "such" / "directory"

  // Do not touch this, just uncomment below.
  executeInSequence(
    run1,
    run2,
    run3,
    run4,
    run5,
    run6,
    run7
  )

  // . ~ and .. usage examples
  executeInSequence(
    git("status", -"s") `with` workdir == ~,
    cmd("ls")() `with` workdir == ~"Documents",
    git("status", -"s") `with` workdir == `.`,
    cmd("ls")() `with` workdir == `..` / "assignment-03"
  )
end exercise2
