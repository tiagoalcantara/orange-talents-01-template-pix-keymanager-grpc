package br.com.zup.edu.integracao.bcb

import br.com.zup.edu.pix.model.Conta

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.ITAU_UNIBANCO_ISPB,
)