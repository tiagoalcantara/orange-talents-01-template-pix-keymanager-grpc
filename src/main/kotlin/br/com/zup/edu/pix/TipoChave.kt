package br.com.zup.edu.pix

import io.micronaut.validation.validator.constraints.EmailValidator

enum class TipoChave {
    CPF {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false

            return chave.matches("^[0-9]{11}\$".toRegex())
        }
    },
    CELULAR {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false

            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false

            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    ALEATORIA {
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }
    };

    abstract fun valida(chave: String?): Boolean
}
