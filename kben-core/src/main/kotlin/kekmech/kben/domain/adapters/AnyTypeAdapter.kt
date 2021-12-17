package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.annotations.Bencode
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

internal class AnyTypeAdapter<T : Any> : TypeAdapter<T>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): T {
        val dict = (value as BencodeElement.BencodeDictionary).entries
        return typeHolder
            .type
            .primaryConstructorParameters
            .associateWith { parameter ->
                val name = parameter.annotatedName
                // parameter value
                context.fromBencode<Any>(
                    bencodeElement = dict[name]!!,
                    typeHolder = TypeHolder.of(parameter)
                )
            }
            .let {
                @Suppress("UNCHECKED_CAST")
                typeHolder.type.primaryConstructor!!.callBy(it) as T
            }
    }

    override fun toBencode(value: T, context: SerializationContext): BencodeElement {
        val dictionary = sortedMapOf<String, BencodeElement>()
        @Suppress("UNCHECKED_CAST")
        value::class.primaryConstructorParameters.forEach { parameter ->
            val name = parameter.annotatedName
            val parameterCorrespondingProperty =
                value::class.declaredMemberProperties.first { it.annotatedName == name } as KProperty1<T, Any>
            val obtainedValue = context.toBencode(parameterCorrespondingProperty.get(value))
            dictionary[name] = obtainedValue
        }
        return BencodeElement.BencodeDictionary(dictionary)
    }

    private val KParameter.annotatedName
        get() =
            (annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name

    private val KProperty<*>.annotatedName
        get() =
            (annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name

    private val KClass<*>.primaryConstructorParameters
        get() =
            primaryConstructor!!.parameters

    private val TypeHolder.type
        get() =
            when (this) {
                is TypeHolder.Simple -> type
                is TypeHolder.Parameterized -> type
            }
}
