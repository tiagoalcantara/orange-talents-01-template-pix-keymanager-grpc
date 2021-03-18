package br.com.zup.edu.compartilhado.grpc.handlers

import io.grpc.Status
import io.micronaut.context.MessageSource
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ValidationExceptionHandler(
    @Inject private val messageSource: MessageSource
) : ExceptionHandler<ConstraintViolationException> {
    override fun handle(e: ConstraintViolationException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }
}