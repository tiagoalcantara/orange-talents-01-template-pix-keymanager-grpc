package br.com.zup.edu.pix.cadastrar

class ChaveExistenteException(
    message: String = "chave já está cadastrada"
) : RuntimeException(message) {
}