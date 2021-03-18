package br.com.zup.edu.pix.utils

import br.com.zup.edu.BuscarChaveRequest
import br.com.zup.edu.BuscarChaveRequest.BuscaCase.CHAVE
import br.com.zup.edu.BuscarChaveRequest.BuscaCase.PIXID
import br.com.zup.edu.CadastrarChaveRequest
import br.com.zup.edu.TipoChave.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.edu.TipoConta.TIPO_CONTA_DESCONHECIDO
import br.com.zup.edu.pix.dto.CadastrarChaveDTO
import br.com.zup.edu.pix.dto.Filtro
import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun CadastrarChaveRequest.toDTO(): CadastrarChaveDTO {
    return CadastrarChaveDTO(
        idCliente = idCliente,
        tipoChave = if (tipoChave != TIPO_CHAVE_DESCONHECIDO) TipoChave.valueOf(tipoChave.name) else null,
        tipoConta = if (tipoConta != TIPO_CONTA_DESCONHECIDO) TipoConta.valueOf(tipoConta.name) else null,
        chave = chave
    )
}

fun BuscarChaveRequest.toFiltro(validator: Validator): Filtro {
    val filtro = when(buscaCase) {
        PIXID -> pixId.let { Filtro.BuscaPorPixId(it.idCliente, it.idChave) }
        CHAVE -> Filtro.BuscaPorChave(chave)
        else -> Filtro.Invalido
    }

    val erros = validator.validate(filtro)
    if(erros.isNotEmpty()) throw ConstraintViolationException(erros)

    return filtro
}