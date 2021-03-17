package br.com.zup.edu.pix.service

import br.com.zup.edu.pix.exception.ObjetoNaoEncontradoException
import br.com.zup.edu.pix.repository.ChaveRepository
import br.com.zup.edu.pix.validacoes.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class RemoverChaveService(
    @Inject val chaveRepository: ChaveRepository
){

    @Transactional
    fun remover(
        @NotBlank @ValidUUID("idCliente em formato inválido") idCliente: String?,
        @NotBlank @ValidUUID("idChave em formato inválido") idChave: String?
    ){
        val uuidChave = UUID.fromString(idChave)
        val uuidCliente = UUID.fromString(idCliente)

        val chave = chaveRepository.findByIdAndIdCliente(uuidChave, uuidCliente)
            .orElseThrow{ throw ObjetoNaoEncontradoException("chave não existe ou não pertence ao cliente.") }



        chaveRepository.deleteById(uuidChave)
    }
}