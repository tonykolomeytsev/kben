package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BencodeInputStreamTest {

    @Test
    fun `read strings`() {
        Mocks.StringPrimitives.RAW.zip(Mocks.StringPrimitives.IR).forEach { (rawString, irString) ->
            assertEquals(
                irString,
                BencodeInputStream(rawString.byteInputStream()).readBencodeElement()
            )
        }
    }

    @Test
    fun `read empty string`() {
        assertEquals(
            BencodeByteString(""),
            BencodeInputStream("0:".byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read integers`() {
        Mocks.IntegerPrimitives.RAW.zip(Mocks.IntegerPrimitives.IR).forEach { (rawInteger, irInteger) ->
            assertEquals(
                irInteger,
                BencodeInputStream(rawInteger.byteInputStream()).readBencodeElement()
            )
        }
    }

    @Test
    fun `read list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.IR,
            BencodeInputStream(Mocks.ListOfStrings.RAW.byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.IR,
            BencodeInputStream(Mocks.ListOfIntegers.RAW.byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read empty list`() {
        assertEquals(
            BencodeList(emptyList()),
            BencodeInputStream("le".byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.IR,
            BencodeInputStream(Mocks.DictionaryWithIntegers.RAW.byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.IR,
            BencodeInputStream(Mocks.DictionaryWithStrings.RAW.byteInputStream()).readBencodeElement()
        )
    }

    @Test
    fun `read empty dictionary`() {
        assertEquals(
            BencodeDictionary(sortedMapOf()),
            BencodeInputStream("de".byteInputStream()).readBencodeElement()
        )
    }
}