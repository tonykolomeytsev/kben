package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.BencodeByteString

class EnumTypeAdapter<T : Any> : TypeAdapter<Enum<*>>() {

    @Suppress("UNCHECKED_CAST")
    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): Enum<*> {
        typeHolder as TypeHolder.Simple // enum classes cannot have type parameters
        val serializedOptionName = (value as BencodeByteString).asString
        val options = typeHolder.type.java.enumConstants as Array<Enum<*>>
        return options.firstOrNull { it.name == serializedOptionName }
            ?: throw optionError(
                enumClassName = typeHolder.type.qualifiedName!!,
                optionName = serializedOptionName,
            )
    }

    override fun toBencode(value: Enum<*>, context: SerializationContext): BencodeElement =
        BencodeByteString(value.name)

    private fun optionError(enumClassName: String, optionName: String): Throwable =
        IllegalStateException("Option with name '$optionName' not found in enum class $enumClassName")
}
