package kekmech.kben

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

sealed class TypeHolder {

    data class Simple(val type: KClass<*>) : TypeHolder() {

        override fun toString(): String = "*${type.simpleName}"
    }

    data class Parameterized(val type: KClass<*>, val parameterTypes: List<TypeHolder>) : TypeHolder() {

        override fun toString(): String = "*${type.simpleName}${
            parameterTypes.joinToString(", ", "<", ">")
        }"
    }

    companion object {

        fun ofBencodeList(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Iterable::class,
                parameterTypes = listOf(Simple(valueKClass))
            )

        fun ofBencodeDictionary(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Map::class,
                parameterTypes = listOf(Simple(String::class), Simple(valueKClass))
            )

        fun from(parameter: KParameter): TypeHolder {
            val type = parameter.type.javaType
            return type.parameterTypes()
        }

        private fun Type.parameterTypes(): TypeHolder {
            return when (this) {
                is ParameterizedType ->
                    Parameterized(
                        type = (rawType as Class<*>).kotlin,
                        parameterTypes = actualTypeArguments.map { it.parameterTypes() }
                    )
                is WildcardType -> upperBounds[0].parameterTypes()
                else ->
                    Simple(type = (this as? Class<*>)?.kotlin ?: throw cantInferTypeError(this))
            }
        }

        private fun cantInferTypeError(type: Type): Throwable =
            IllegalStateException("Can't infer type $type")
    }
}