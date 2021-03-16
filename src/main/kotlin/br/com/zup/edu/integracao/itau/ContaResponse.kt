package br.com.zup.edu.integracao.itau

data class ContaResponse (
    val titular: TitularResponse
)

data class TitularResponse(val nome: String, val cpf: String)