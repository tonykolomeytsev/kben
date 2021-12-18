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

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder,
    ): T {
        val dict = (value as BencodeElement.BencodeDictionary).entries
        val properties = typeHolder.type.declaredMemberProperties.associateBy { it.name }
        return typeHolder
            .type
            .primaryConstructorParameters
            .associateWith { parameter ->
                val name = properties[parameter.name]?.annotatedName
                    ?: throw propertyNotFoundError(
                        className = typeHolder.type.qualifiedName!!,
                        parameterName = parameter.name!!,
                    )
                // parameter value
                context.fromBencode<Any>(
                    bencodeElement = dict[name]
                        ?: throw missedDictKeyError(
                            className = typeHolder.type.qualifiedName.orEmpty(),
                            parameterName = parameter.name.orEmpty(),
                            annotatedParameterName = name
                        ),
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
        val properties = value::class.declaredMemberProperties.associateBy { it.name }
        @Suppress("UNCHECKED_CAST")
        value::class.primaryConstructorParameters.forEach { parameter ->
            val parameterCorrespondingProperty =
                (properties[parameter.name] as? KProperty1<T, Any>)
                    ?: throw propertyNotFoundError(
                        className = value::class.qualifiedName!!,
                        parameterName = parameter.name!!,
                    )
            val obtainedValue = context.toBencode(parameterCorrespondingProperty.get(value))
            dictionary[parameterCorrespondingProperty.annotatedName] = obtainedValue
        }
        return BencodeElement.BencodeDictionary(dictionary)
    }

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

    private fun missedDictKeyError(
        className: String,
        parameterName: String,
        annotatedParameterName: String,
    ): Throwable =
        IllegalStateException(
            "Parameter $parameterName was specified for class ${className}, but no value " +
                "with key '$annotatedParameterName' was found for it in the data being serialized."
        )

    private fun propertyNotFoundError(
        className: String,
        parameterName: String,
    ): Throwable =
        IllegalStateException(
            "A property named '$parameterName' that matches the $parameterName constructor " +
                "parameter of the class $className was not found."
        )
}
