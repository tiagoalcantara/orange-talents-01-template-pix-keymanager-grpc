package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.HandleExceptions
import br.com.zup.edu.integracao.bcb.BancoCentralClient
import br.com.zup.edu.pix.repository.ChaveRepository
import br.com.zup.edu.pix.service.CadastrarChaveService
import br.com.zup.edu.pix.service.RemoverChaveService
import br.com.zup.edu.pix.utils.BuscarChaveResponseConverter
import br.com.zup.edu.pix.utils.toDTO
import br.com.zup.edu.pix.utils.toFiltro
import com.google.protobuf.Descriptors
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@HandleExceptions
@Suppress("unused")
class BuscarChavePixEndpoint(
    @Inject private val validator: Validator,
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val bancoCentralClient: BancoCentralClient,
) : PixKeyManagerServiceGrpc.PixKeyManagerServiceImplBase() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun buscar(request: BuscarChaveRequest, responseObserver: StreamObserver<BuscarChaveResponse>) {
        val filtro = request.toFiltro(validator)
        val resultado = filtro.filtrar(chaveRepository, bancoCentralClient)

        responseObserver.onNext(BuscarChaveResponseConverter().converter(resultado))
        responseObserver.onCompleted()
    }
}