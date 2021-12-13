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





