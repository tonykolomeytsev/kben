package kekmech.kben.io

import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

internal class BencodeOutputStreamTest {

    private fun write(bencodeElement: BencodeElement): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        BencodeOutputStream(byteArrayOutputStream).write(bencodeElement)
        return byteArrayOutputStream.toByteArray().let(::String)
    }

    @Test
    fun `write strings`() {
        Mocks.StringPrimitives.RAW.zip(Mocks.StringPrimitives.IR).forEach { (rawString, irString) ->
            assertEquals(
                rawString,
                write(irString)
            )
        }
    }

    @Test
    fun `write empty string`() {
        assertEquals(
            "0:",
            write(BencodeByteString(""))
        )
    }

    @Test
    fun `write integers`() {
        Mocks.IntegerPrimitives.RAW.zip(Mocks.IntegerPrimitives.IR).forEach { (rawInteger, irInteger) ->
            assertEquals(
                rawInteger,
                write(irInteger)
            )
        }
    }

    @Test
    fun `write list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.RAW,
            write(Mocks.ListOfStrings.IR)
        )
    }

    @Test
    fun `write list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.RAW,
            write(Mocks.ListOfIntegers.IR)
        )
    }

    @Test
    fun `write empty list`() {
        assertEquals(
            "le",
            write(BencodeList(emptyList()))
        )
    }

    @Test
    fun `write dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.RAW,
            write(Mocks.DictionaryWithIntegers.IR)
        )
    }

    @Test
    fun `write dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.RAW,
            write(Mocks.DictionaryWithStrings.IR)
        )
    }

    @Test
    fun `write empty dictionary`() {
        assertEquals(
            "de",
            write(BencodeDictionary(sortedMapOf()))
        )
    }
}