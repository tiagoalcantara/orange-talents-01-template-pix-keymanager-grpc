package br.com.zup.edu.compartilhado.grpc

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerInterceptor(
    @Inject private val resolver: ExceptionHandlerResolver
): MethodInterceptor<BindableService, Any?> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        try {
            return context.proceed()
        } catch (e: Exception) {
            logger.error("Tratando exceção (${e.javaClass.name}) na chamada: ${context.targetMethod}")

            val handler = resolver.resolve(e)
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response().onError(status.asRuntimeException())
        }

        return null
    }

    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any?>) {
        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }
}