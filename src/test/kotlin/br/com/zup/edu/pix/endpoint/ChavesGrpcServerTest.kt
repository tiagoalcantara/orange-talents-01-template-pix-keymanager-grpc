package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.ListarChaveRequest
import br.com.zup.edu.PixKeyManagerServiceGrpc
import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.model.Chave
import br.com.zup.edu.pix.model.Conta
import br.com.zup.edu.pix.repository.ChaveRepository
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/*
* DICAS (do repo do Ponte)
* Necessario desabilitar o controle transacional (transactional=false) pois o gRPC Server
* roda numa thread separada, caso contrário não será possível preparar cenário dentro do método @Test
* */
@MicronautTest
internal class ChavesGrpcServerTest(
    private val repository: ChaveRepository,
    private val grpcClient: PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub,
) {
    companion object {
        val ID_CLIENTE = UUID.randomUUID()
    }

    // Por padrão roda numa transação isolada
    @BeforeEach
    fun setup() {
        repository.save(buildChave(tipoChave = TipoChave.ALEATORIA, chave = "aleatoria"))
        repository.save(buildChave(tipoChave = TipoChave.ALEATORIA, chave = "aleatoria2", idCliente = ID_CLIENTE))
        repository.save(
            buildChave(
                tipoChave = TipoChave.EMAIL,
                chave = "tiago.campos@zup.com.br",
                idCliente = ID_CLIENTE
            )
        )
    }

    // Por padrão roda numa transação isolada
    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar todas as chaves do cliente`() {
        // Cenário
        val idCliente = ID_CLIENTE.toString()

        // Ação
        val response = grpcClient.listar(
            ListarChaveRequest.newBuilder()
                .setIdCliente(idCliente)
                .build()
        )

        // Validação
        with(response.chavesList) {
            MatcherAssert.assertThat(this, hasSize(2))
            MatcherAssert.assertThat(
                this.map { Pair(it.tipoChave, it.chave) }.toList(),
                containsInAnyOrder(
                    Pair(br.com.zup.edu.TipoChave.ALEATORIA, "aleatoria2"),
                    Pair(br.com.zup.edu.TipoChave.EMAIL, "tiago.campos@zup.com.br"),
                )
            )
        }
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub {
            return PixKeyManagerServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun buildChave(
        tipoChave: TipoChave,
        chave: String = UUID.randomUUID().toString(),
        idCliente: UUID = UUID.randomUUID()
    ): Chave {
        return Chave(
            tipoChave = tipoChave,
            chave = chave,
            idCliente = idCliente,
            conta = Conta(
                instituicao = "ITAU",
                titularNome = "Tiago Campos",
                titularCpf = "12345678910",
                agencia = "1234",
                numero = "123456",
            ),
            tipoConta = TipoConta.CONTA_CORRENTE
        )
    }
}