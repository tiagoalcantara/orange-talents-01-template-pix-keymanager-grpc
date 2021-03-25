package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.BuscarChaveRequest
import br.com.zup.edu.ListarChaveRequest
import br.com.zup.edu.PixKeyManagerServiceGrpc
import br.com.zup.edu.RemoverChaveRequest
import br.com.zup.edu.integracao.bcb.BancoCentralClient
import br.com.zup.edu.integracao.bcb.DeletePixKeyRequest
import br.com.zup.edu.integracao.bcb.KeyType
import br.com.zup.edu.integracao.bcb.PixKeyDetailsResponse
import br.com.zup.edu.integracao.itau.ItauClient
import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.model.Chave
import br.com.zup.edu.pix.repository.ChaveRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ChavesGrpcServerTest(
    @Inject private val repository: ChaveRepository,
    @Inject private val grpcClient: PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub,
    @InjectMocks private val itauClient: ItauClient,
    @InjectMocks private val bancoCentralClient: BancoCentralClient,
) {
    companion object {
        val ID_CLIENTE = UUID.randomUUID()
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

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

    /*
    * CADASTRAR
    * */
    @Test
    fun `deve cadastrar uma chave com sucesso`() {
        `when`(
            itauClient.buscarContaPorTipo(
                idCliente = ID_CLIENTE.toString(),
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.ok(buildContaResponse()))

        `when`(bancoCentralClient.cadastrar(buildCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(buildCreatePixKeyResponse()))

        val response = grpcClient.cadastrar(buildCadastrarChaveRequest(idCliente = ID_CLIENTE.toString()))

        with(response) {
            assertFalse(pixId.isNullOrBlank())
        }
    }

    @Test
    fun `nao deve cadastrar chave que ja existe`() {
        repository.save(buildChave(tipoChave = TipoChave.EMAIL, chave = "teste@teste.com"))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(buildCadastrarChaveRequest(idCliente = ID_CLIENTE.toString()))
        }

        with(exception) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
        }
    }

    @Test
    fun `nao deve cadastrar chave com formato incorreto`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                buildCadastrarChaveRequest(
                    chave = "123",
                    tipoChave = br.com.zup.edu.TipoChave.EMAIL,
                    idCliente = ID_CLIENTE.toString()
                )
            )
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve cadastrar chave com cpf diferente do cadastrado no erp`() {
        `when`(
            itauClient.buscarContaPorTipo(
                idCliente = ID_CLIENTE.toString(),
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.ok(buildContaResponse()))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                buildCadastrarChaveRequest(
                    chave = "10987654321",
                    tipoChave = br.com.zup.edu.TipoChave.CPF,
                    idCliente = ID_CLIENTE.toString()
                )
            )
        }

        with(exception) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("o cpf difere do cadastro do titular", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar chave se o erp nao encontrar o cliente`() {
        `when`(
            itauClient.buscarContaPorTipo(
                idCliente = ID_CLIENTE.toString(),
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(buildCadastrarChaveRequest(idCliente = ID_CLIENTE.toString()))
        }

        with(exception) {
            assertEquals(Status.FAILED_PRECONDITION.code, this.status.code)
        }
    }

    @Test
    fun `nao deve cadastrar chave se nao conseguir cadastrar no bcb`() {
        `when`(
            itauClient.buscarContaPorTipo(
                idCliente = ID_CLIENTE.toString(),
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.ok(buildContaResponse()))

        `when`(bancoCentralClient.cadastrar(buildCreatePixKeyRequest()))
            .thenReturn(HttpResponse.badRequest())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(buildCadastrarChaveRequest(idCliente = ID_CLIENTE.toString()))
        }

        with(exception) {
            assertEquals(Status.FAILED_PRECONDITION.code, exception.status.code)
            assertEquals("falha ao cadastrar a chave no banco central", status.description)
        }
    }

    /*
    * LISTAR
    * */
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
            MatcherAssert.assertThat(this, Matchers.hasSize(2))
            MatcherAssert.assertThat(
                this.map { Pair(it.tipoChave, it.chave) }.toList(),
                Matchers.containsInAnyOrder(
                    Pair(br.com.zup.edu.TipoChave.ALEATORIA, "aleatoria2"),
                    Pair(br.com.zup.edu.TipoChave.EMAIL, "tiago.campos@zup.com.br"),
                )
            )
        }
    }

    @Test
    fun `nao deve listar chaves com id do cliente invalido`() {
        val idClienteInvalido = "teste"

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.listar(
                ListarChaveRequest.newBuilder()
                    .setIdCliente(idClienteInvalido)
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertTrue(this.status.description?.contains("UUID inválido") ?: false)
        }
    }

    @Test
    fun `deve retornar uma lista vazia para id que nao tem chaves`() {
        val idClienteNaoCadastrado = UUID.randomUUID().toString()

        val resposta = grpcClient.listar(
            ListarChaveRequest.newBuilder()
                .setIdCliente(idClienteNaoCadastrado)
                .build()
        )

        assertTrue(resposta.chavesList.isEmpty())
    }

    /*
    * REMOVER
    * */
    @Test
    fun `deve remover uma chave`() {
        val chave: Chave = repository.findAll().first()

        `when`(bancoCentralClient.remover(request = DeletePixKeyRequest(key = chave.chave), key = chave.chave))
            .thenReturn(HttpResponse.ok())

        grpcClient.remover(
            RemoverChaveRequest.newBuilder()
                .setIdCliente(chave.idCliente.toString())
                .setIdChave(chave.id.toString())
                .build()
        )

        assertEquals(2, repository.findAll().size)
    }

    @Test
    fun `nao deve remover uma chave se nao conseguir remover do bcb`() {
        val chave: Chave = repository.findAll().first()

        `when`(bancoCentralClient.remover(request = DeletePixKeyRequest(key = chave.chave), key = chave.chave))
            .thenReturn(HttpResponse.status(HttpStatus.FORBIDDEN))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoverChaveRequest.newBuilder()
                    .setIdCliente(chave.idCliente.toString())
                    .setIdChave(chave.id.toString())
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("erro ao remover a chave do banco central", status.description)
        }
    }

    @Test
    fun `nao deve remover uma chave que nao pertence ao cliente`() {
        val chave: Chave = repository.findAll().first()

        `when`(bancoCentralClient.remover(request = DeletePixKeyRequest(key = chave.chave), key = chave.chave))
            .thenReturn(HttpResponse.status(HttpStatus.FORBIDDEN))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoverChaveRequest.newBuilder()
                    .setIdCliente(UUID.randomUUID().toString())
                    .setIdChave(chave.id.toString())
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("chave não existe ou não pertence ao cliente", status.description)
        }
    }

    /*
    * BUSCAR
    * */

    @Test
    fun `deve buscar chave internamente por pixId`() {
        val chave: Chave = repository.findAll().first()

        val resultado = grpcClient.buscar(
            BuscarChaveRequest.newBuilder()
                .setPixId(
                    BuscarChaveRequest.BuscaPorPixId.newBuilder()
                        .setIdCliente(chave.idCliente.toString())
                        .setIdChave(chave.id.toString())
                        .build()
                )
                .build()
        )

        assertEquals(chave.chave, resultado.chave.chave)
    }

    @Test
    fun `deve buscar chave internamente por valor da chave`() {
        val chave: Chave = repository.findAll().first()

        val resultado = grpcClient.buscar(
            BuscarChaveRequest.newBuilder()
                .setChave(chave.chave)
                .build()
        )

        assertEquals(chave.chave, resultado.chave.chave)
    }

    @Test
    fun `deve buscar chave no bcb se nao encontrar internamente`(){
        `when`(bancoCentralClient.buscarPorChave("teste@teste.com"))
            .thenReturn(HttpResponse.ok(PixKeyDetailsResponse(
                keyType = KeyType.EMAIL,
                key = "teste@teste.com",
                bankAccount = buildBankAccount(),
                owner = buildOwner(),
                createdAt = LocalDateTime.now(),
            )))

        val resultado = grpcClient.buscar(
            BuscarChaveRequest.newBuilder()
                .setChave("teste@teste.com")
                .build()
        )

        assertEquals("teste@teste.com", resultado.chave.chave)
    }

    @Test
    fun `deve informar caso nao encontra a chave`() {
        `when`(bancoCentralClient.buscarPorChave("teste@teste.com"))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.buscar(
                BuscarChaveRequest.newBuilder()
                    .setChave("teste@teste.com")
                    .build()
            )
        }

        with(exception){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("chave não encontrada", status.description)
        }
    }

    /*
    * CONFIGURAÇÃO
    * */
    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeyManagerServiceGrpc.PixKeyManagerServiceBlockingStub {
            return PixKeyManagerServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient = mock(ItauClient::class.java)

    @MockBean(BancoCentralClient::class)
    fun bancoCentralClient(): BancoCentralClient = mock(BancoCentralClient::class.java)
}