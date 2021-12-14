package kekmech.kben

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

sealed class TypeHolder {

    data class Simple(val type: KClass<*>) : TypeHolder()

    data class Parameterized(val type: KClass<*>, val parameterTypes: List<KClass<*>>) : TypeHolder()

    companion object {

        fun ofBencodeList(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Iterable::class,
                parameterTypes = listOf(valueKClass)
            )

        fun ofBencodeDictionary(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Map::class,
                parameterTypes = listOf(String::class, valueKClass)
            )

        fun from(parameter: KParameter): TypeHolder =
            when (val type = parameter.type.javaType) {
                is ParameterizedType ->
                    Parameterized(
                        type = (type.rawType as Class<*>).kotlin,
                        parameterTypes = type.actualTypeArguments.map { (it as Class<*>).kotlin },
                    )
                else ->
                    Simple(
                        type = (type as Class<*>).kotlin
                    )
            }
    }
}