package br.com.zup.edu.compartilhado.grpc.handlers

import io.grpc.Status
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

// Não quero que esse seja injetado na lista, por isso não será um bean
class DefaultExceptionHandler: ExceptionHandler<Exception> {

    override fun handle(e: Exception): ExceptionHandler.StatusWithDetails {
        val status = when(e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            else -> Status.UNKNOWN
        }

        return ExceptionHandler.StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }
}