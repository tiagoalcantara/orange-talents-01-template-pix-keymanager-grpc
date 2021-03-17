package br.com.zup.edu.pix.model

import br.com.zup.edu.pix.enums.TipoChave
import br.com.zup.edu.pix.enums.TipoConta
import java.util.*
import javax.persistence.*

@Entity
class Chave(
    @Column(nullable = false, unique = true)
    val chave: String,
    @Column(nullable = false)
    val idCliente: UUID,
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val tipoConta: TipoConta,
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val tipoChave: TipoChave,
) {
    @Id @GeneratedValue
    var id: UUID? = null
        private set
}