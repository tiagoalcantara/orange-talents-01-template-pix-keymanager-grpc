package br.com.zup.edu.pix.service

import br.com.zup.edu.ListarChaveResponse
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.exception.ObjetoNaoEncontradoException
import br.com.zup.edu.pix.model.Chave
import br.com.zup.edu.pix.repository.ChaveRepository
import br.com.zup.edu.pix.validacoes.ValidUUID
import com.google.protobuf.Timestamp
import io.micronaut.validation.Validated
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class ListarChaveService(
    @Inject private val chaveRepository: ChaveRepository,
) {
    fun listar(
        @NotBlank @ValidUUID idCliente: String
    ): List<Chave> {
        return chaveRepository.findByIdCliente(UUID.fromString(idCliente))
    }
}
