package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*

internal class AnyTargetTypeAdapter : TypeAdapter<Any>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): Any =
        when (value) {
            is BencodeInteger ->
                context.fromBencode(value, TypeHolder.Simple(Long::class))
            is BencodeByteString ->
                if (value.isValidUTF8String) {
                    context.fromBencode(value, TypeHolder.Simple(String::class))
                } else {
                    context.fromBencode(value, TypeHolder.Simple(ByteArray::class))
                }
            is BencodeList ->
                context.fromBencode(value, TypeHolder.ofList(Any::class))
            is BencodeDictionary ->
                context.fromBencode(value, TypeHolder.ofMap(Any::class))
        }

    override fun toBencode(value: Any, context: SerializationContext): BencodeElement {
        // theoretically will never throws,
        // because it is no cases need to serialize raw instance of Any
        throw UnsupportedOperationException("Can't serialize value: $value")
    }
}