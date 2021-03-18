package br.com.zup.edu.pix.utils

import br.com.zup.edu.BuscarChaveResponse
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.dto.ChavePixInfo
import com.google.protobuf.Timestamp
import java.time.ZoneId

class BuscarChaveResponseConverter {
    fun converter(chaveInfo: ChavePixInfo): BuscarChaveResponse {
        return BuscarChaveResponse.newBuilder()
            .setIdCliente(chaveInfo.idCliente?.toString() ?: "")
            .setIdPix(chaveInfo.idPix?.toString() ?: "")
            .setChave(BuscarChaveResponse.ChaveInfo.newBuilder()
                .setTipo(TipoChave.valueOf(chaveInfo.tipo.name))
                .setChave(chaveInfo.chave)
                .setConta(BuscarChaveResponse.ChaveInfo.ContaInfo.newBuilder()
                    .setTipo(TipoConta.valueOf(chaveInfo.tipoConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setTitularNome(chaveInfo.conta.titularNome)
                    .setTitularCpf(chaveInfo.conta.titularCpf)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setConta(chaveInfo.conta.numero)
                    .build()
                )
                .setCriadaEm(chaveInfo.criadaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
            )
            .build()
    }
}