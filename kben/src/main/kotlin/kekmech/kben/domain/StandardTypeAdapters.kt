package kekmech.kben.domain

import kekmech.kben.domain.dto.BencodeElement

internal val StandardTypeAdapters = mapOf(
    Int::class to IntTypeAdapter(),
    Long::class to LongTypeAdapter(),
    String::class to StringTypeAdapter(),
    ByteArray::class to ByteArrayTypeAdapter(),
)

private class IntTypeAdapter : TypeAdapter<Int>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Int {
        return (value as BencodeElement.BencodeInteger).integer.toInt()
    }

    override fun toBencode(value: Int, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeInteger(value.toLong())
    }
}

private class LongTypeAdapter : TypeAdapter<Long>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Long {
        return (value as BencodeElement.BencodeInteger).integer
    }

    override fun toBencode(value: Long, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeInteger(value)
    }
}

private class StringTypeAdapter : TypeAdapter<String>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): String {
        return (value as BencodeElement.BencodeByteArray).asString
    }

    override fun toBencode(value: String, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeByteArray(value)
    }
}

private class ByteArrayTypeAdapter : TypeAdapter<ByteArray>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): ByteArray {
        return (value as BencodeElement.BencodeByteArray).content
    }

    override fun toBencode(value: ByteArray, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeByteArray(value)
    }
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