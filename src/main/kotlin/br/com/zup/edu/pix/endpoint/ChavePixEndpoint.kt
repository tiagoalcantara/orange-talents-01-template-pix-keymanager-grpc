package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.HandleExceptions
import br.com.zup.edu.pix.service.CadastrarChaveService
import br.com.zup.edu.pix.service.RemoverChaveService
import br.com.zup.edu.pix.utils.toDTO
import com.google.protobuf.Descriptors
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@HandleExceptions
@Suppress("unused")
class ChavesGrpcServer(
    @Inject val cadastrarChaveService: CadastrarChaveService,
    @Inject val removerChaveService: RemoverChaveService
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