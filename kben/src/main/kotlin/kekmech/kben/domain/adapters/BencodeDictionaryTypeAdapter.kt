package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class BencodeDictionaryTypeAdapter : TypeAdapter<BencodeElement.BencodeDictionary>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): BencodeElement.BencodeDictionary {
        return value as BencodeElement.BencodeDictionary
    }

    override fun toBencode(value: BencodeElement.BencodeDictionary, context: SerializationContext): BencodeElement {
        return value
    }
}