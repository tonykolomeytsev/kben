package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.reflect.Bencode
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

class AnyTypeAdapter<T : Any>: TypeAdapter<T>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): T {
        val dict = (value as BencodeElement.BencodeDictionary).entries
        return when (typeHolder) {
            is TypeHolder.Simple -> typeHolder
                .type
                .primaryConstructorParameters
                .associateWith { parameter ->
                    val name = parameter.annotatedName
                    // parameter value
                    context.fromBencode<Any>(dict[name]!!, TypeHolder.from(parameter))
                }
                .let { typeHolder.type.primaryConstructor!!.callBy(it) as T }
            is TypeHolder.Parameterized -> TODO("Not implemented")
        }
    }

    override fun toBencode(value: T, context: SerializationContext): BencodeElement {
        val dictionary = sortedMapOf<String, BencodeElement>()
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

    private val KClass<*>.primaryConstructorParameters get() =
        primaryConstructor!!.parameters
}
