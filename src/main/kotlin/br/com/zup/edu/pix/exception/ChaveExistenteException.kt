package br.com.zup.edu.pix.exception

class ChaveExistenteException(
    message: String = "chave já está cadastrada"
) : RuntimeException(message) {
}