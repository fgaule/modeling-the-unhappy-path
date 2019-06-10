package org.fgaule.platform.travelagent

interface Failure {
    val message: String
}

interface NotFound
interface SideEffectFailure : Failure {
    val cause: Throwable
}

class FailureException(val failure: Failure) : RuntimeException(failure.message)