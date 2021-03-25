package br.com.zup.edu.pix.endpoint

import br.com.zup.edu.CadastrarChaveRequest
import br.com.zup.edu.integracao.bcb.*
import br.com.zup.edu.integracao.itau.ContaResponse
import br.com.zup.edu.integracao.itau.InstituicaoResponse
import br.com.zup.edu.integracao.itau.TitularResponse
import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import br.com.zup.edu.pix.model.Chave
import br.com.zup.edu.pix.model.Conta
import java.time.LocalDateTime
import java.util.*

internal fun buildCadastrarChaveRequest(
    chave: String = "teste@teste.com",
    tipoChave: br.com.zup.edu.TipoChave = br.com.zup.edu.TipoChave.EMAIL,
    idCliente: String,
) = CadastrarChaveRequest.newBuilder()
    .setChave(chave)
    .setTipoChave(tipoChave)
    .setTipoConta(br.com.zup.edu.TipoConta.CONTA_CORRENTE)
    .setIdCliente(idCliente)
    .build()

internal fun buildCreatePixKeyRequest() = CreatePixKeyRequest(
    keyType = KeyType.EMAIL,
    key = "teste@teste.com",
    owner = buildOwner(),
    bankAccount = buildBankAccount(),
)

internal fun buildCreatePixKeyResponse() = CreatePixKeyResponse(
    keyType = KeyType.EMAIL,
    key = "teste@teste.com",
    bankAccount = buildBankAccount(),
    owner = buildOwner(),
    createdAt = LocalDateTime.now(),
)

internal fun buildBankAccount(): BankAccount {
    return BankAccount(
        participant = Conta.ITAU_UNIBANCO_ISPB,
        branch = "1111",
        accountNumber = "222222",
        accountType = BankAccount.AccountType.CACC,
    )
}

internal fun buildOwner(): Owner {
    return Owner(
        type = Owner.OwnerType.NATURAL_PERSON,
        name = "Tiago Campos",
        taxIdNumber = "12345678910",
    )
}

internal fun buildContaResponse(): ContaResponse {
    return ContaResponse(
        tipo = TipoConta.CONTA_CORRENTE.name,
        instituicao = InstituicaoResponse("UNIBANCO ITAU SA", Conta.ITAU_UNIBANCO_ISPB),
        agencia = "1111",
        numero = "222222",
        titular = TitularResponse("Tiago Campos", "12345678910")
    )
}

internal fun buildChave(
    tipoChave: TipoChave,
    chave: String = UUID.randomUUID().toString(),
    idCliente: UUID = UUID.randomUUID()
): Chave {
    return Chave(
        tipoChave = tipoChave,
        chave = chave,
        idCliente = idCliente,
        conta = Conta(
            instituicao = "ITAU",
            titularNome = "Tiago Campos",
            titularCpf = "12345678910",
            agencia = "1234",
            numero = "123456",
        ),
        tipoConta = TipoConta.CONTA_CORRENTE
    )
}