package kekmech.kben.domain

import kekmech.kben.domain.dto.BencodeElement.BencodeDictionary
import kekmech.kben.domain.dto.BencodeElement.BencodeList
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SerializationContextTest {

    private val context get() = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters(), emptyMap())

    @Test
    fun `serialize integers`() {
        Mocks.IntegerPrimitives.LIST.zip(Mocks.IntegerPrimitives.IR).forEach { (integer, irInteger) ->
            assertEquals(
                irInteger,
                context.toBencode(integer)
            )
        }
    }

    @Test
    fun `serialize strings`() {
        Mocks.StringPrimitives.LIST.zip(Mocks.StringPrimitives.IR).forEach { (string, irString) ->
            assertEquals(
                irString,
                context.toBencode(string)
            )
        }
    }

    @Test
    fun `serialize list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.IR,
            context.toBencode(Mocks.IntegerPrimitives.LIST)
        )
    }

    @Test
    fun `serialize list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.IR,
            context.toBencode(Mocks.StringPrimitives.LIST)
        )
    }

    @Test
    fun `serialize empty list`() {
        assertEquals(
            BencodeList(emptyList()),
            context.toBencode(emptyList<Any>())
        )
    }

    @Test
    fun `serialize dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.IR,
            context.toBencode(Mocks.DictionaryWithIntegers.DICTIONARY)
        )
    }

    @Test
    fun `serialize dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.IR,
            context.toBencode(Mocks.DictionaryWithStrings.DICTIONARY)
        )
    }

    @Test
    fun `serialize empty dictionary`() {
        assertEquals(
            BencodeDictionary(sortedMapOf()),
            context.toBencode(mapOf<String, String>())
        )
    }

    @Test
    fun `serialize set of integers`() {
        assertEquals(
            Mocks.SetOfIntegers.IR,
            context.toBencode(Mocks.SetOfIntegers.SET)
        )
    }

    @Test
    fun `serialize set of strings`() {
        assertEquals(
            Mocks.SetOfStrings.IR,
            context.toBencode(Mocks.SetOfStrings.SET)
        )
    }
}