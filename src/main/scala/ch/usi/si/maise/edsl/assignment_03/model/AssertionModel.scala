package ch.usi.si.maise.edsl.assignment_03.model

import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import AsyncContext.{given, *}
import ProcessModel.ProcessResult

/** A model for assertions on processes.
  */
object AssertionModel:

  /** An assertion description.
    *
    * @param subject
    *   the assertion subject.
    * @param expectedBehavior
    *   the expected behavior
    */
  case class AssertionDescription(subject: String, expectedBehavior: String):

    def rep: String =
      s"$subject should $expectedBehavior"

  /** An assertion with a description.
    *
    * @param description
    *   a description for the assertion.
    * @param assertion
    *   the assertion itself.
    */
  case class AssertionWithDescription(
      description: AssertionDescription,
      assertion: ExecutableAssertion
  ):
    def run() = assertion.run(description)
  end AssertionWithDescription

  /** An executable assertion.
    */
  trait ExecutableAssertion:
    def run(description: AssertionDescription): Future[AssertionResult]
  end ExecutableAssertion

  /** An assertion result, with a description of what happened during its check.
    */
  sealed trait AssertionResult:
    val message: String
  end AssertionResult

  /** A successful assertion.
    */
  case class AssertionSuccess(message: String) extends AssertionResult

  /** A failed assertion.
    */
  case class AssertionFailure(message: String) extends AssertionResult

  /** An assertion on the failure of a process, i.e., a process that cannot be
    * started or that does not terminate in time.
    *
    * @param f
    *   a function returning a future of a process result.
    */
  class ProcessWillFailAssertion(f: () => Future[ProcessResult])
      extends ExecutableAssertion:
    def run(description: AssertionDescription): Future[AssertionResult] =
      f().transform: tryResult =>
        Success(tryResult match
          case Success(result) =>
            AssertionFailure(
              s"${description.rep}: process did not fail (exited with status ${result.exitStatus})"
            )
          case Failure(f) =>
            AssertionSuccess(
              s"${description.rep}: correctly failed with error ${f.getMessage()}"
            )
        )
  end ProcessWillFailAssertion

  /** An assertion using a predicate on the result of a process.
    *
    * @param f
    *   a function returning a future of a process result.
    * @param resultPredicate
    *   the predicate that the result must satisfy.
    */
  case class ProcessExitsWithResultPredicateAssertion(
      f: () => Future[ProcessResult],
      resultPredicate: ResultPredicate
  ) extends ExecutableAssertion:
    def run(description: AssertionDescription): Future[AssertionResult] =
      f().transform: tryResult =>
        Success(tryResult match
          case Success(result) =>
            if (resultPredicate.fun(result))
              AssertionSuccess(
                s"${description.rep}: result matched ${resultPredicate.description}"
              )
            else
              AssertionFailure(
                s"${description.rep}: result did not match ${resultPredicate.description} (${resultPredicate.failureMessage(result)})"
              )
          case Failure(f) => AssertionFailure(s"${description.rep}: ${f}")
        )
    end run
  end ProcessExitsWithResultPredicateAssertion

  /** A predicate on the result of a process, composed of a function and a
    * description.
    */
  trait ResultPredicate:

    val fun: ProcessResult => Boolean
    val description: String

    def failureMessage(r: ProcessResult): String

    /** Composes two predicates with a logical and.
      */
    def &&(other: ResultPredicate): ResultPredicate =
      AndPredicate(this, other)

    /** Composes two predicates with a logical or.
      */
    def ||(other: ResultPredicate): ResultPredicate =
      OrPredicate(this, other)

  end ResultPredicate

  private case class AndPredicate(a: ResultPredicate, b: ResultPredicate)
      extends ResultPredicate:
    val fun = (r: ProcessResult) => a.fun(r) && b.fun(r)
    val description = s"(${a.description} and ${b.description})"
    def failureMessage(r: ProcessResult): String =
      s"${a.failureMessage(r)}, ${b.failureMessage(r)}"
  end AndPredicate

  private case class OrPredicate(a: ResultPredicate, b: ResultPredicate)
      extends ResultPredicate:
    val fun = (r: ProcessResult) => a.fun(r) || b.fun(r)
    val description = s"(${a.description} or ${b.description})"
    def failureMessage(r: ProcessResult): String =
      s"${a.failureMessage(r)}, ${b.failureMessage(r)}"
  end OrPredicate

  /** The ways in which two exit statuses can be compared.
    */
  enum Comparison(val symbol: String, val fun: (Int, Int) => Boolean):
    case EqualTo extends Comparison("==", (a, b) => a == b)
    case LessThan extends Comparison("<", (a, b) => a < b)
    case LessThanOrEqualTo extends Comparison("<=", (a, b) => a <= b)
    case GreaterThan extends Comparison(">", (a, b) => a > b)
    case GreaterThanOrEqualTo extends Comparison(">=", (a, b) => a >= b)

  /** A predicate which compares the exit status of a result with a given one.
    * For example, with GreaterThan and 2 it matches the exit statuses greater
    * than 2.
    */
  case class ResultHasExitStatusPredicate(
      comparison: Comparison,
      exitStatus: Int
  ) extends ResultPredicate:
    val fun = (r: ProcessResult) => comparison.fun(r.exitStatus, exitStatus)
    val description = s"exitStatus ${comparison.symbol} ${exitStatus}"
    def failureMessage(r: ProcessResult): String =
      s"exitStatus was ${r.exitStatus}"
  end ResultHasExitStatusPredicate

  /** A predicate which matches the text written on the standard output.
    */
  case class ResultHasStandardOutputPredicate(matcher: TextMatcher)
      extends ResultPredicate:
    val fun = (r: ProcessResult) => matcher.fun(r.standardOutput)
    val description = s"stdout ${matcher.description}"
    def failureMessage(r: ProcessResult): String =
      s"stdout was ${quote(r.standardOutput)}"
  end ResultHasStandardOutputPredicate

  /** A predicate which matches the text written on the standard error.
    */
  case class ResultHasStandardErrorPredicate(matcher: TextMatcher)
      extends ResultPredicate:
    val fun = (r: ProcessResult) => matcher.fun(r.standardError)
    val description = s"stderr ${matcher.description}"
    def failureMessage(r: ProcessResult): String =
      s"stderr was ${quote(r.standardError)}"
  end ResultHasStandardErrorPredicate

  /** A condition on a text, used to check what a process has written.
    */
  sealed trait TextMatcher:
    val fun: String => Boolean
    val description: String

  /** Matches a text equal to the given one. Since programs normally terminate
    * their output with a new line, white space at the end of both texts is
    * ignored.
    */
  case class TextEqualsTo(expected: String) extends TextMatcher:
    val fun = (text: String) => text.stripTrailing() == expected.stripTrailing()
    val description = s"is ${quote(expected)}"

  /** Matches a text containing the given fragment.
    */
  case class TextContains(fragment: String) extends TextMatcher:
    val fun = (text: String) => text.contains(fragment)
    val description = s"contains ${quote(fragment)}"

  /** A quoted, single-line, and possibly shortened representation of a text.
    */
  private def quote(text: String): String =
    val singleLine = text.stripTrailing().replace("\r", "").replace("\n", "\\n")
    if singleLine.length <= 80 then s"\"$singleLine\""
    else s"\"${singleLine.take(80)}...\""

end AssertionModel
