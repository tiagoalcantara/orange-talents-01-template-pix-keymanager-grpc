package br.com.zup.edu.integracao.bcb

import br.com.zup.edu.pix.Instituicoes
import br.com.zup.edu.pix.dto.ChavePixInfo
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.model.Conta
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
) {
    fun toModel(): ChavePixInfo{
        return ChavePixInfo(
            tipo = keyType.tipoChave!!,
            chave = key,
            tipoConta = when(this.bankAccount.accountType){
                BankAccount.AccountType.CACC -> TipoConta.CONTA_CORRENTE
                BankAccount.AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = Conta(
                titularNome = owner.name,
                titularCpf = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                instituicao = Instituicoes.nome(bankAccount.participant)
            ),
            criadaEm = createdAt,
        )
    }
}
