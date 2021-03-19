package br.com.zup.edu.pix.validacoes

import javax.validation.Constraint
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-[1-5][a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$",
    flags = [Pattern.Flag.CASE_INSENSITIVE],
    message = "UUId Inválido ({validatedValue})"
)
@Target(FIELD, CONSTRUCTOR, PROPERTY, VALUE_PARAMETER)
annotation class ValidUUID (
    val message: String = "",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = [],
)