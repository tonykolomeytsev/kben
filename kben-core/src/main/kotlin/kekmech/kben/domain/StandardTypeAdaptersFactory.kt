package kekmech.kben.domain

import kekmech.kben.domain.adapters.*
import kekmech.kben.domain.dto.BencodeElement
import kotlin.reflect.KClass

internal object StandardTypeAdaptersFactory {

    fun createTypeAdapters(): Map<KClass<*>, TypeAdapter<*>> =
        linkedMapOf(
            Int::class to IntTypeAdapter(),
            Long::class to LongTypeAdapter(),
            String::class to StringTypeAdapter(),
            ByteArray::class to ByteArrayTypeAdapter(),
            Boolean::class to BooleanTypeAdapter(),
            BencodeElement.BencodeInteger::class to BencodeIntegerTypeAdapter(),
            BencodeElement.BencodeByteString::class to BencodeByteStringTypeAdapter(),
            BencodeElement.BencodeList::class to BencodeListTypeAdapter(),
            BencodeElement.BencodeDictionary::class to BencodeDictionaryTypeAdapter(),
            BencodeElement::class to BencodeElementTypeAdapter(),
            Any::class to AnyTargetTypeAdapter(),
        )
}





