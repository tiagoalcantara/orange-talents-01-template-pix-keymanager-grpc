package br.com.zup.edu.compartilhado.grpc.handlers

import br.com.zup.edu.pix.exception.ObjetoNaoEncontradoException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ObjetoNaoEncontradoExceptionHandler : ExceptionHandler<ObjetoNaoEncontradoException> {
    override fun handle(e: ObjetoNaoEncontradoException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ObjetoNaoEncontradoException
    }
}