package br.com.zup.edu.pix.cadastrar

import br.com.zup.edu.integracao.itau.ItauClient
import br.com.zup.edu.pix.Chave
import br.com.zup.edu.pix.ChaveRepository
import br.com.zup.edu.pix.TipoChave
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class CadastrarChaveService(
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val itauClient: ItauClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun salvar(@Valid chaveDTO: CadastrarChaveDTO): Chave {
        // Verificar se a chave já existe
        if(chaveRepository.existsByChave(chaveDTO.chave)){
            throw ChaveExistenteException()
        }

        // Buscar dados da conta
        val conta =
            itauClient.buscarContaPorTipo(chaveDTO.idCliente!!, chaveDTO.tipoConta!!.name).body() ?:
            throw IllegalStateException("cliente não cadastrado no Itaú")

        // Verificar se o CPF informado é o do titular da conta
        if(chaveDTO.tipoChave == TipoChave.CPF && conta.titular.cpf != chaveDTO.chave){
            throw IllegalStateException("o cpf difere do cadastro do titular")
        }

        logger.info("Chave recebida e validada: $chaveDTO")
        return chaveRepository.save(chaveDTO.toModel())
    }
}
