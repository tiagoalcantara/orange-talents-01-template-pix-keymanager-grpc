package br.com.zup.edu.pix

import br.com.zup.edu.CadastrarChaveRequest
import br.com.zup.edu.CadastrarChaveResponse
import br.com.zup.edu.PixKeyManagerServiceGrpc
import br.com.zup.edu.TipoChave.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.edu.TipoConta.TIPO_CONTA_DESCONHECIDO
import br.com.zup.edu.compartilhado.grpc.HandleExceptions
import br.com.zup.edu.pix.cadastrar.CadastrarChaveDTO
import br.com.zup.edu.pix.cadastrar.CadastrarChaveService
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@HandleExceptions
@Suppress("unused")
class ChavesGrpcServer(
    val service: CadastrarChaveService
) : PixKeyManagerServiceGrpc.PixKeyManagerServiceImplBase() {
    private val logger = LoggerFactory.getLogger(ChavesGrpcServer::class.java)

    override fun cadastrar(
        request: CadastrarChaveRequest,
        responseObserver: StreamObserver<CadastrarChaveResponse>
    ) {
        val chavePixDTO = request.toDTO()
        val chave = service.salvar(chavePixDTO)

        responseObserver.onNext(CadastrarChaveResponse.newBuilder().setPixId(chave.id.toString()).build())
        responseObserver.onCompleted()
    }
}

fun CadastrarChaveRequest.toDTO(): CadastrarChaveDTO {
    return CadastrarChaveDTO(
        idCliente = idCliente,
        tipoChave = if (tipoChave != TIPO_CHAVE_DESCONHECIDO) TipoChave.valueOf(tipoChave.name) else null,
        tipoConta = if (tipoConta != TIPO_CONTA_DESCONHECIDO) TipoConta.valueOf(tipoConta.name) else null,
        chave = chave
    )
}