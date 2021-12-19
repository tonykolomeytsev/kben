package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.annotations.Bencode
import kekmech.kben.annotations.DefaultValue
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.BencodeByteString

internal class EnumTypeAdapter : TypeAdapter<Enum<*>>() {

    @Suppress("UNCHECKED_CAST")
    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): Enum<*> {
        typeHolder as TypeHolder.Simple // enum classes cannot have type parameters
        val serializedOptionName = (value as BencodeByteString).asString
        val options = typeHolder.type.java.enumConstants as Array<Enum<*>>
        return options.firstOrNull { it.annotatedName == serializedOptionName }
            ?: options.firstOrNull { it.isDefaultOption }
            ?: throw optionError(
                enumClassName = typeHolder.type.qualifiedName!!,
                optionName = serializedOptionName,
            )
    }

    override fun toBencode(value: Enum<*>, context: SerializationContext): BencodeElement =
        BencodeByteString(value.annotatedName)

    private val Enum<*>.annotatedName: String
        get() = (javaClass.getField(name).annotations.firstOrNull { it is Bencode } as? Bencode)?.name ?: name

    private val Enum<*>.isDefaultOption: Boolean
        get() = javaClass.getField(name).annotations.any { it is DefaultValue }

    private fun optionError(enumClassName: String, optionName: String): Throwable =
        IllegalStateException("Option with name '$optionName' not found in enum class $enumClassName")
}
