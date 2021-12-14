package kekmech.kben.domain

import kekmech.kben.domain.dto.BencodeElement.BencodeDictionary
import kekmech.kben.domain.dto.BencodeElement.BencodeList
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SerializationContextTest {

    @Test
    fun `serialize integers`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        Mocks.IntegerPrimitives.LIST.zip(Mocks.IntegerPrimitives.IR).forEach { (integer, irInteger) ->
            assertEquals(
                irInteger,
                context.toBencode(integer)
            )
        }
    }

    @Test
    fun `serialize strings`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        Mocks.StringPrimitives.LIST.zip(Mocks.StringPrimitives.IR).forEach { (string, irString) ->
            assertEquals(
                irString,
                context.toBencode(string)
            )
        }
    }

    @Test
    fun `serialize list of integers`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.ListOfIntegers.IR,
            context.toBencode(Mocks.IntegerPrimitives.LIST)
        )
    }

    @Test
    fun `serialize list of strings`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.ListOfStrings.IR,
            context.toBencode(Mocks.StringPrimitives.LIST)
        )
    }

    @Test
    fun `serialize empty list`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            BencodeList(emptyList()),
            context.toBencode(emptyList<Any>())
        )
    }

    @Test
    fun `serialize dictionary with integer values`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.DictionaryWithIntegers.IR,
            context.toBencode(Mocks.DictionaryWithIntegers.DICTIONARY)
        )
    }

    @Test
    fun `serialize dictionary with string values`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.DictionaryWithStrings.IR,
            context.toBencode(Mocks.DictionaryWithStrings.DICTIONARY)
        )
    }

    @Test
    fun `serialize empty dictionary`() {
        val context = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            BencodeDictionary(sortedMapOf()),
            context.toBencode(mapOf<String, String>())
        )
    }
}