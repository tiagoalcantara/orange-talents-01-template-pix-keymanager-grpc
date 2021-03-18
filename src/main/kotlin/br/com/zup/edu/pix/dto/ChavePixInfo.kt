package br.com.zup.edu.pix.dto

import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.model.Chave
import br.com.zup.edu.pix.model.Conta
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val idPix: UUID? = null,
    val idCliente: UUID? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: Conta,
    val criadaEm: LocalDateTime,
) {
    companion object {
        fun of(chave: Chave): ChavePixInfo {
            return ChavePixInfo(
                idPix = chave.id,
                idCliente = chave.idCliente,
                tipo = chave.tipoChave,
                chave = chave.chave,
                tipoConta = chave.tipoConta,
                conta = chave.conta,
                criadaEm = chave.criadaEm,
            )
        }
    }
}
