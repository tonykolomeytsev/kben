package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.annotations.Bencode
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import java.lang.reflect.Modifier
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

internal class AnyTypeAdapter<T : Any> : TypeAdapter<T>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder,
    ): T {
        val dictionary = (value as BencodeElement.BencodeDictionary).entries
        val properties = typeHolder.type.declaredMemberProperties.associateBy { it.name }
        val constructorArguments = mutableMapOf<KParameter, Any>()
        @Suppress("UNCHECKED_CAST")
        return typeHolder
            .type
            .primaryConstructorParameters
            .onEach { parameter ->
                val relatedProperty = (properties[parameter.name] as? KProperty1<T, Any>)
                    ?: throw propertyNotFoundError(
                        className = typeHolder.type.qualifiedName!!,
                        parameterName = parameter.name!!,
                    )
                if (!relatedProperty.isTransient) {
                    val name = relatedProperty.annotatedName
                    constructorArguments[parameter] =
                        context.fromBencode(
                            bencodeElement = dictionary[name]
                                ?: throw missedDictKeyError(
                                    className = typeHolder.type.qualifiedName.orEmpty(),
                                    parameterName = parameter.name.orEmpty(),
                                    annotatedParameterName = name
                                ),
                            typeHolder = when (typeHolder) {
                                is TypeHolder.Simple -> TypeHolder.of(parameter)
                                is TypeHolder.Parameterized ->
                                    createTypeHolder(parameter, typeHolder)
                            },
                        )
                }
            }
            .let {
                typeHolder.type.primaryConstructor!!.callBy(constructorArguments) as T
            }
    }

    private fun createTypeHolder(
        parameter: KParameter,
        typeHolder: TypeHolder.Parameterized,
    ): TypeHolder {
        return if (parameter.type.isErasedType) {
            val parameterTypeName = parameter.type.javaType.typeName
            val index = typeHolder.type
                .typeParameters
                .indexOfFirst { typeParameter -> typeParameter.name == parameterTypeName }
            typeHolder.parameterTypes.getOrNull(index)
                ?: throw IllegalStateException(
                    "Can't infer type $parameterTypeName of parameter ${parameter.name}."
                )
        } else {
            TypeHolder.of(parameter)
        }
    }

    override fun toBencode(value: T, context: SerializationContext): BencodeElement {
        val dictionary = sortedMapOf<String, BencodeElement>()
        val properties = value::class.declaredMemberProperties.associateBy { it.name }
        @Suppress("UNCHECKED_CAST")
        return value::class
            .primaryConstructorParameters
            .onEach { parameter ->
                val relatedProperty = (properties[parameter.name] as? KProperty1<T, Any>)
                    ?: throw propertyNotFoundError(
                        className = value::class.qualifiedName!!,
                        parameterName = parameter.name!!,
                    )
                if (!relatedProperty.isTransient) {
                    val obtainedValue = context.toBencode(relatedProperty.get(value))
                    dictionary[relatedProperty.annotatedName] = obtainedValue
                }
            }
            .let {
                BencodeElement.BencodeDictionary(dictionary)
            }
    }

    private val KProperty<*>.annotatedName
        get() = (annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name

    private val KProperty<*>.isTransient
        get() = Modifier.isTransient(javaField?.modifiers ?: 0)

    private val KClass<*>.primaryConstructorParameters
        get() = primaryConstructor!!.parameters

    private val KType.isErasedType
        get() = javaType !is Class<*>

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
