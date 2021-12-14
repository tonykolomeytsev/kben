package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kekmech.kben.domain.dto.BencodeElement

abstract class TypeAdapter<T : Any> {

    open fun toBencode(value: T, context: SerializationContext): BencodeElement {
        throw NotImplementedError("Serialization for class $this not implemented")
    }

    open fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): T {
        throw NotImplementedError("Serialization for class $this not implemented")
    }
}