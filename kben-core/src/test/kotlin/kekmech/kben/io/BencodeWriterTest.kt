package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BencodeWriterTest {

    @Test
    fun `write strings`() {
        Mocks.StringPrimitives.RAW.zip(Mocks.StringPrimitives.IR).forEach { (rawString, irString) ->
            assertEquals(
                rawString,
                BencodeWriter().apply { write(irString) }.toByteArray().let(::String)
            )
        }
    }

    @Test
    fun `write integers`() {
        Mocks.IntegerPrimitives.RAW.zip(Mocks.IntegerPrimitives.IR).forEach { (rawInteger, irInteger) ->
            assertEquals(
                rawInteger,
                BencodeWriter().apply { write(irInteger) }.toByteArray().let(::String)
            )
        }
    }

    @Test
    fun `write list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.RAW,
            BencodeWriter().apply { write(Mocks.ListOfStrings.IR) }.toByteArray().let(::String)
        )
    }

    @Test
    fun `write list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.RAW,
            BencodeWriter().apply { write(Mocks.ListOfIntegers.IR) }.toByteArray().let(::String)
        )
    }

    @Test
    fun `write empty list`() {
        assertEquals(
            "le",
            BencodeWriter().apply { write(BencodeElement.BencodeList(emptyList())) }.toByteArray().let(::String)
        )
    }

    @Test
    fun `write dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.RAW,
            BencodeWriter().apply { write(Mocks.DictionaryWithIntegers.IR) }.toByteArray().let(::String)
        )
    }

    @Test
    fun `write dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.RAW,
            BencodeWriter().apply { write(Mocks.DictionaryWithStrings.IR) }.toByteArray().let(::String)
        )
    }

    @Test
    fun `write empty dictionary`() {
        assertEquals(
            "de",
            BencodeWriter().apply { write(BencodeElement.BencodeDictionary(sortedMapOf())) }.toByteArray().let(::String)
        )
    }
}