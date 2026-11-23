package ch.usi.si.maise.edsl.assignment_03.model

/** Just a context variable (given) to enable the async execution of
  * processes.
  */
object AsyncContext:

  given ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

end AsyncContext
