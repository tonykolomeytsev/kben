package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement.BencodeDictionary
import kekmech.kben.domain.dto.BencodeElement.BencodeList
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BencodeReaderTest {

    @Test
    fun `read strings`() {
        Mocks.StringPrimitives.RAW.zip(Mocks.StringPrimitives.IR).forEach { (rawString, irString) ->
            assertEquals(
                irString,
                BencodeReader(rawString.byteInputStream()).read()
            )
        }
    }

    @Test
    fun `read integers`() {
        Mocks.IntegerPrimitives.RAW.zip(Mocks.IntegerPrimitives.IR).forEach { (rawInteger, irInteger) ->
            assertEquals(
                irInteger,
                BencodeReader(rawInteger.byteInputStream()).read()
            )
        }
    }

    @Test
    fun `read list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.IR,
            BencodeReader(Mocks.ListOfStrings.RAW.byteInputStream()).read()
        )
    }

    @Test
    fun `read list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.IR,
            BencodeReader(Mocks.ListOfIntegers.RAW.byteInputStream()).read()
        )
    }

    @Test
    fun `read empty list`() {
        assertEquals(
            BencodeList(emptyList()),
            BencodeReader("le".byteInputStream()).read()
        )
    }

    @Test
    fun `read dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.IR,
            BencodeReader(Mocks.DictionaryWithIntegers.RAW.byteInputStream()).read()
        )
    }

    @Test
    fun `read dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.IR,
            BencodeReader(Mocks.DictionaryWithStrings.RAW.byteInputStream()).read()
        )
    }

    @Test
    fun `read empty dictionary`() {
        assertEquals(
            BencodeDictionary(sortedMapOf()),
            BencodeReader("de".byteInputStream()).read()
        )
    }
}