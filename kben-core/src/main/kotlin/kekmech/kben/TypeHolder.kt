package kekmech.kben

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

/**
 * A simplified representation of the Kotlin class.
 *
 * This class helps in deserializing Bencode into objects.
 * Instances of [TypeHolder] are limited to the [TypeHolder.Simple]
 * and [TypeHolder.Parameterized] classes.
 *
 * Sometimes you will need to create [TypeHolder] instances yourself
 * to deserialize Bencode. You can use class constructors for this,
 * or functions from the [TypeHolder] companion object:
 * - [TypeHolder.of]
 * - [TypeHolder.ofList]
 * - [TypeHolder.ofMap]
 * - [TypeHolder.ofSet]
 */
sealed class TypeHolder {

    /**
     * A simplified representation of the Kotlin class **without type parameters**.
     *
     * Instances of [TypeHolder.Simple] when calling `kben.fromBencode(...)`
     * can be generated automatically by the Kben library.
     *
     * Example:
     * ```kotlin
     * val kben = Kben()
     * val value1 = kben.fromKben<String>("7:bencode") // will return "bencode"
     * val value2: String =
     *     kben.fromKben("7:bencode", Simple(String::class)) // the same
     *
     * Simple(List::class) // Error, `List<T>` is a class with type parameters.
     * ```
     */
    data class Simple(val type: KClass<*>) : TypeHolder() {

        override fun toString(): String = "*${type.simpleName}"
    }

    /**
     * A simplified representation of the Kotlin class **with type parameters**.
     *
     * Instances of [TypeHolder.Parameterized] when calling `kben.fromBencode(...)`
     * must always be specified.
     *
     * Example:
     *```kotlin
     * val kben = Kben()
     * val value1: List<Int> = // listOf(42, 0)
     *     kben.fromBencode("li42ei0ee", TypeHolder.ofList(Int::class))
     * val value2: Map<String, Int> = // mapOf("hello" to 42, "world" to -1)
     *     kben.fromBencode("d5:helloi42e5:worldi-1ee", TypeHolder.ofMap(Int::class))
     * ```
     */
    data class Parameterized(val type: KClass<*>, val parameterTypes: List<TypeHolder>) : TypeHolder() {

        override fun toString(): String = "*${type.simpleName}${
            parameterTypes.joinToString(", ", "<", ">")
        }"
    }

    companion object {

        /**
         * Creates [TypeHolder] for List.
         *
         * @param valueKClass is a List type parameter class.
         */
        fun ofList(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Iterable::class,
                parameterTypes = listOf(Simple(valueKClass)),
            )

        /**
         * Creates [TypeHolder] for Map.
         *
         * Note that the keys in dictionaries in Bencode are always valid UTF-8 strings.
         *
         * @param valueKClass is a Map value type parameter class.
         */
        fun ofMap(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Map::class,
                parameterTypes = listOf(Simple(String::class), Simple(valueKClass)),
            )

        /**
         * Creates [TypeHolder] for Set.
         *
         * @param valueKClass is a Set type parameter class.
         */
        fun ofSet(valueKClass: KClass<*>): TypeHolder =
            Parameterized(
                type = Set::class,
                parameterTypes = listOf(Simple(valueKClass))
            )

        /**
         * Creates [TypeHolder] for type of KParemeter/KProperty.
         */
        fun of(type: Type): TypeHolder = type.parameterTypes()

        internal fun of(parameter: KParameter): TypeHolder {
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