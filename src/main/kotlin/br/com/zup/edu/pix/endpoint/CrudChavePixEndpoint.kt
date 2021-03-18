package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.HandleExceptions
import br.com.zup.edu.integracao.bcb.BancoCentralClient
import br.com.zup.edu.pix.repository.ChaveRepository
import br.com.zup.edu.pix.service.CadastrarChaveService
import br.com.zup.edu.pix.service.RemoverChaveService
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
class ChavesGrpcServer(
    @Inject private val cadastrarChaveService: CadastrarChaveService,
    @Inject private val removerChaveService: RemoverChaveService,
) : PixKeyManagerServiceGrpc.PixKeyManagerServiceImplBase() {
    private val logger = LoggerFactory.getLogger(ChavesGrpcServer::class.java)

    override fun cadastrar(request: CadastrarChaveRequest, responseObserver: StreamObserver<CadastrarChaveResponse>
    ) {
        val chave = cadastrarChaveService.salvar(request.toDTO())
        responseObserver.onNext(CadastrarChaveResponse.newBuilder().setPixId(chave.id.toString()).build())
        responseObserver.onCompleted()
    }

    override fun remover(request: RemoverChaveRequest, responseObserver: StreamObserver<Empty>) {
        removerChaveService.remover(request.idCliente, request.idChave)
        responseObserver.onNext(Empty.newBuilder().build())
        responseObserver.onCompleted()
    }
}