package br.com.zup.edu.pix.repository

import br.com.zup.edu.pix.model.Chave
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository: JpaRepository<Chave, UUID>{
    fun existsByChave(chave: String?): Boolean
    fun findByIdAndIdCliente(id: UUID, idCliente: UUID): Optional<Chave>
}