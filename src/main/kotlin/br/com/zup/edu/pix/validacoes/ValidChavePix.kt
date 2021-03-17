package br.com.zup.edu.pix.validacoes

import br.com.zup.edu.pix.dto.CadastrarChaveDTO
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePix(
    val message: String = "chave PIX inválida para o tipo informado",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = [],
)

@Singleton
class ChavePixValidator: ConstraintValidator<ChavePix, CadastrarChaveDTO> {
    override fun isValid(
        value: CadastrarChaveDTO?,
        annotationMetadata: AnnotationValue<ChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoChave == null)
            return false

        return value.tipoChave.valida(value.chave)
    }
}


