package br.com.zup.edu.pix.utils

import br.com.zup.edu.CadastrarChaveRequest
import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.dto.CadastrarChaveDTO
import br.com.zup.edu.TipoChave.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.edu.TipoConta.TIPO_CONTA_DESCONHECIDO

fun CadastrarChaveRequest.toDTO(): CadastrarChaveDTO {
    return CadastrarChaveDTO(
        idCliente = idCliente,
        tipoChave = if (tipoChave != TIPO_CHAVE_DESCONHECIDO) TipoChave.valueOf(tipoChave.name) else null,
        tipoConta = if (tipoConta != TIPO_CONTA_DESCONHECIDO) TipoConta.valueOf(tipoConta.name) else null,
        chave = chave
    )
}