package ch.usi.si.maise.edsl.assignment_03.model

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** This trait is used to execute a set of assertions.
  *
  * Insert the assertions to execute in the namedAssertions var, and then call
  * run().
  */
trait AssertionExecutor:

  import AssertionModel.{given, *}

  protected var namedAssertions: List[AssertionWithDescription] = List()

  import AsyncContext.{given, *}

  /** Checks the assertions, waits for all of them, and prints the results.
    */
  def run() =
    val assertionFutures = namedAssertions.map(_.run())
    val results = Await.result(Future.sequence(assertionFutures), Duration.Inf)
    results.foreach(println)
  end run

end AssertionExecutor
