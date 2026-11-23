package ch.usi.si.maise.edsl.assignment_03.model

import scala.concurrent.Await
import scala.Conversion.into
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.duration.Duration
import scala.util.Success
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import CommandModel.Command

/** A simple model for the execution of commands as processes of the operating
  * system.
  */
object ProcessModel:

  // a given value that allows async computations on futures.
  import AsyncContext.ec

  /** The maximum time a process is allowed to run, in seconds.
    */
  val timeoutInSeconds = 20

  /** The run of a command.
    *
    * @param command
    *   the command to run.
    * @param environment
    *   the environment variables to set for the process, as pairs of strings.
    *   They are added to the ones of the current program.
    * @param workingDirectory
    *   the directory where the process runs. If missing, it is the one of the
    *   current program.
    * @param standardInput
    *   the text that the process reads from its standard input. If missing,
    *   the process reads an empty input.
    */
  case class ProcessRun(
      command: Command,
      environment: List[(String, String)] = List(),
      workingDirectory: Option[Path] = None,
      standardInput: Option[String] = None
  ):
    /** Starts the process.
      *
      * The future fails if the process cannot be started (for example because
      * the executable or the working directory do not exist), or if it does
      * not terminate in time. A process that terminates with an exit status
      * different from 0 is not a failure.
      *
      * @return
      *   a future of the result of the process.
      */
    def perform(): Future[ProcessResult] =
      Future:
        val builder = java.lang.ProcessBuilder(command.words*)
        environment.foreach: (name, value) =>
          builder.environment().put(name, value)
        workingDirectory.foreach: path =>
          builder.directory(path.toFile)
        blocking:
          builder.start()
      .flatMap: process =>
        // input and outputs are handled concurrently, so that the process
        // never blocks on a full pipe.
        Future:
          blocking:
            val input = process.getOutputStream()
            try input.write(standardInput.getOrElse("").getBytes(UTF_8))
            finally input.close()
        val futureOutput = Future:
          blocking:
            String(process.getInputStream().readAllBytes(), UTF_8)
        val futureError = Future:
          blocking:
            String(process.getErrorStream().readAllBytes(), UTF_8)
        val futureExitStatus = Future:
          blocking:
            if process.waitFor(timeoutInSeconds, TimeUnit.SECONDS) then
              process.exitValue()
            else
              process.destroyForcibly()
              throw TimeoutException(
                s"$command did not terminate in $timeoutInSeconds seconds"
              )
        futureExitStatus.flatMap: exitStatus =>
          futureOutput.flatMap: output =>
            futureError.map: error =>
              ProcessResult(exitStatus, output, error)
    end perform
  end ProcessRun

  /** The result of a terminated process.
    *
    * @param exitStatus
    *   the exit status: by convention, 0 means success.
    * @param standardOutput
    *   everything the process has written on its standard output.
    * @param standardError
    *   everything the process has written on its standard error.
    */
  class ProcessResult(
      val exitStatus: Int,
      val standardOutput: String,
      val standardError: String
  ):
    override def toString =
      s"Process exited with status ${exitStatus}, ${standardOutput.length} chars on stdout and ${standardError.length} chars on stderr"
  end ProcessResult

  /** Waits for some futures, and then prints their outcome in sequence.
    *
    * @param seq
    *   a sequence of futures.
    */
  def executeInSequence[T](seq: into[Future[T]]*): Unit =
    val transformedSeq = seq map:
       _.transform(t => Success(t))
    val results = Await.result(Future.sequence(transformedSeq), Duration.Inf)
    results.foreach(println)
  end executeInSequence

end ProcessModel
