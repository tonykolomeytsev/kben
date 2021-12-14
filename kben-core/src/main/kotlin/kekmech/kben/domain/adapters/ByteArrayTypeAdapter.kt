package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class ByteArrayTypeAdapter : TypeAdapter<ByteArray>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder
    ): ByteArray {
        return (value as BencodeElement.BencodeByteArray).content
    }

    override fun toBencode(value: ByteArray, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeByteArray(value)
    }
}