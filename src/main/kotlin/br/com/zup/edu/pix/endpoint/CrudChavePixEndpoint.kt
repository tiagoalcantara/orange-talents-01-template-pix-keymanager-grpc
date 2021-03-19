package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.grpc.HandleExceptions
import br.com.zup.edu.integracao.bcb.BancoCentralClient
import br.com.zup.edu.pix.repository.ChaveRepository
import br.com.zup.edu.pix.service.CadastrarChaveService
import br.com.zup.edu.pix.service.ListarChaveService
import br.com.zup.edu.pix.service.RemoverChaveService
import br.com.zup.edu.pix.utils.BuscarChaveResponseConverter
import br.com.zup.edu.pix.utils.toDTO
import br.com.zup.edu.pix.utils.toFiltro
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@HandleExceptions
@Suppress("unused")
class ChavesGrpcServer(
    @Inject private val cadastrarChaveService: CadastrarChaveService,
    @Inject private val removerChaveService: RemoverChaveService,
    @Inject private val validator: Validator,
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val bancoCentralClient: BancoCentralClient,
    @Inject private val listarChaveService: ListarChaveService,
) : PixKeyManagerServiceGrpc.PixKeyManagerServiceImplBase() {
    private val logger = LoggerFactory.getLogger(ChavesGrpcServer::class.java)

    override fun cadastrar(
        request: CadastrarChaveRequest, responseObserver: StreamObserver<CadastrarChaveResponse>
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

    override fun buscar(request: BuscarChaveRequest, responseObserver: StreamObserver<BuscarChaveResponse>) {
        val filtro = request.toFiltro(validator)
        val resultado = filtro.filtrar(chaveRepository, bancoCentralClient)

        responseObserver.onNext(BuscarChaveResponseConverter().converter(resultado))
        responseObserver.onCompleted()
    }

    override fun listar(request: ListarChaveRequest, responseObserver: StreamObserver<ListarChaveResponse>) {
        val chaves = listarChaveService.listar(request.idCliente)

        val chavesGrpc = chaves.map { chave ->
            ListarChaveResponse.ListagemInfo.newBuilder()
                .setIdPix(chave.id.toString())
                .setTipoChave(TipoChave.valueOf(chave.tipoChave.name))
                .setTipoConta(TipoConta.valueOf(chave.tipoConta.name))
                .setChave(chave.chave)
                .setCriadaEm(chave.criadaEm.let {
                    val instantes = it.atZone(ZoneId.of("UTC")).toInstant()

                    Timestamp.newBuilder()
                        .setSeconds(instantes.epochSecond)
                        .setNanos(instantes.nano)
                        .build()
                })
                .build()
        }

        responseObserver.onNext(
            ListarChaveResponse.newBuilder()
                .setIdCliente(request.idCliente)
                .addAllChaves(chavesGrpc)
                .build()
        )
        responseObserver.onCompleted()
    }
}