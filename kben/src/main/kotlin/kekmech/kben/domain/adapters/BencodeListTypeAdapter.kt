package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class BencodeListTypeAdapter : TypeAdapter<BencodeElement.BencodeList>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): BencodeElement.BencodeList {
        return value as BencodeElement.BencodeList
    }

    override fun toBencode(value: BencodeElement.BencodeList, context: SerializationContext): BencodeElement {
        return value
    }
}