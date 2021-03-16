package br.com.zup.edu.compartilhado.grpc

import br.com.zup.edu.compartilhado.grpc.handlers.DefaultExceptionHandler
import br.com.zup.edu.compartilhado.grpc.handlers.ExceptionHandler
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

/*
* Explicação do fluxo para ajudar a entender depois
* 1. Anotação @HandleExceptions indica que o endpoint vai usar um interceptador para os métodos.
* 2. O interceptor usa um try catch para tentar seguir o fluxo, e se não conseguir trata a exceção.
*    Ele passa a exceção para o resolver.
* 3. O resolver busca um handler adequado ou usa o default e devolve para o interceptor.
* 4. O interceptor pega o handler e passa a exceção.
* 5. O handler devolve um objeto com o Status e Metadados da resposta de erro.
* 6. O interceptor pega o objeto e usa o contexto para devolve-lo para o responseObserver.
* */

@Singleton
class ExceptionHandlerResolver(
    @Inject private val handlers: List<ExceptionHandler<Exception>>,
) {
    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>): this(handlers){
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception):
            ExceptionHandler<Exception> {
        val foundHandlers = handlers.filter { h -> h.supports(e)}
        if(foundHandlers.size > 1)
            throw IllegalStateException("Mais de um handler tratando a mesma exceção (${e.javaClass.name}): $foundHandlers")

        return foundHandlers.firstOrNull() ?: defaultHandler
    }
}
