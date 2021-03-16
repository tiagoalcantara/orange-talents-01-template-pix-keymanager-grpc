package br.com.zup.edu.integracao.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091/")
interface ItauClient {

    @Get("/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscarContaPorTipo(@PathVariable idCliente: String, @QueryValue tipo: String): HttpResponse<ContaResponse>
}