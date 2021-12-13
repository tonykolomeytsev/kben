package kekmech.kben.domain

import kekmech.kben.domain.adapters.*
import kekmech.kben.domain.adapters.BencodeIntegerTypeAdapter
import kekmech.kben.domain.adapters.ByteArrayTypeAdapter
import kekmech.kben.domain.adapters.IntTypeAdapter
import kekmech.kben.domain.adapters.LongTypeAdapter
import kekmech.kben.domain.adapters.StringTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kotlin.reflect.KClass

object StandardTypeAdaptersFactory {

    fun createTypeAdapters(): Map<KClass<*>, TypeAdapter<*>> =
        linkedMapOf(
            Int::class to IntTypeAdapter(),
            Long::class to LongTypeAdapter(),
            String::class to StringTypeAdapter(),
            ByteArray::class to ByteArrayTypeAdapter(),
            BencodeElement.BencodeInteger::class to BencodeIntegerTypeAdapter(),
            BencodeElement.BencodeByteArray::class to BencodeByteArrayTypeAdapter(),
            BencodeElement.BencodeList::class to BencodeListTypeAdapter(),
            BencodeElement.BencodeDictionary::class to BencodeDictionaryTypeAdapter(),
        )
}

internal class IterableTypeAdapter : TypeAdapter<Iterable<*>>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Iterable<*> =
        (value as BencodeElement.BencodeList).elements.map { context.fromBencode<BencodeElement>(it) }

    override fun toBencode(value: Iterable<*>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeList(value.map { context.toBencode(it!!) })
    }
}

internal class MapTypeAdapter : TypeAdapter<Map<*, *>>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Map<*, *> =
        (value as BencodeElement.BencodeDictionary).entries
            .mapValues { (_, value) -> context.fromBencode<BencodeElement>(value) }
            .toSortedMap()

    override fun toBencode(value: Map<*, *>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeDictionary(
            value
                .mapKeys { (key, _) -> key as String }
                .mapValues { (_, value) -> context.toBencode(value!!) }
                .toSortedMap()
        )
    }
}

