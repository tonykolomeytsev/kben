package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.reflect.Bencode
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

class AnyTypeAdapter<T : Any>(
    private val kClass: KClass<T>,
) : TypeAdapter<T>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): T {
        val dictionary = value as BencodeElement.BencodeDictionary
        val targetConstructor = kClass.primaryConstructor!!
        val constructorArguments = targetConstructor.parameters.associateWith { property ->
            val propertyKClass = (property.type.javaType as Class<*>).kotlin
            val propertyName = property.annotatedName
            val propertyValue = dictionary.entries[propertyName]
                ?.let { context.fromBencode(it, propertyKClass) }
            if (!property.type.isMarkedNullable && propertyValue == null) {
                error("Null value to non null property")
            }
            propertyValue
        }
        return targetConstructor.callBy(constructorArguments)
    }

    override fun toBencode(value: T, context: SerializationContext): BencodeElement {
        val dictionary = sortedMapOf<String, BencodeElement>()
        kClass.declaredMemberProperties.forEach { property ->
            val propertyName = property.annotatedName
            val propertyValue = context.toBencode(property.get(value)!!)
            dictionary[propertyName] = propertyValue
        }
        return BencodeElement.BencodeDictionary(dictionary)
    }

    private val KParameter.annotatedName
        get() =
            (annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name

    private val KProperty<*>.annotatedName
        get() =
            (annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name
}