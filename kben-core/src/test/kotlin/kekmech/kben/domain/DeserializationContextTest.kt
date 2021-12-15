package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kekmech.kben.domain.dto.BencodeElement.BencodeDictionary
import kekmech.kben.domain.dto.BencodeElement.BencodeList
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DeserializationContextTest {

    @Test
    fun `deserialize integers`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        Mocks.IntegerPrimitives.IR.zip(Mocks.IntegerPrimitives.LIST).forEach { (irInteger, integer) ->
            assertEquals(
                integer,
                context.fromBencode(irInteger, TypeHolder.Simple(Long::class))
            )
        }
    }

    @Test
    fun `deserialize strings`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        Mocks.StringPrimitives.IR.zip(Mocks.StringPrimitives.LIST).forEach { (irString, string) ->
            assertEquals(
                string,
                context.fromBencode(irString, TypeHolder.Simple(String::class))
            )
        }
    }

    @Test
    fun `deserialize list of integers`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.IntegerPrimitives.LIST,
            context.fromBencode(Mocks.ListOfIntegers.IR, TypeHolder.ofList(Long::class))
        )
    }

    @Test
    fun `deserialize list of strings`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.StringPrimitives.LIST,
            context.fromBencode(Mocks.ListOfStrings.IR, TypeHolder.ofList(String::class))
        )
    }

    @Test
    fun `deserialize empty list`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            emptyList<String>(),
            context.fromBencode(BencodeList(emptyList()), TypeHolder.ofList(String::class))
        )
    }

    @Test
    fun `deserialize dictionary with integer values`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.DictionaryWithIntegers.DICTIONARY,
            context.fromBencode(Mocks.DictionaryWithIntegers.IR, TypeHolder.ofMap(Long::class))
        )
    }

    @Test
    fun `deserialize dictionary with string values`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.DictionaryWithStrings.DICTIONARY,
            context.fromBencode(Mocks.DictionaryWithStrings.IR, TypeHolder.ofMap(String::class))
        )
    }

    @Test
    fun `deserialize empty dictionary`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            mapOf<String, String>(),
            context.fromBencode(BencodeDictionary(sortedMapOf()), TypeHolder.ofMap(String::class))
        )
    }

    @Test
    fun `deserialize set of integers`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.SetOfIntegers.SET,
            context.fromBencode(Mocks.SetOfIntegers.IR, TypeHolder.ofSet(Long::class))
        )
    }

    @Test
    fun `deserialize set of strings`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        assertEquals(
            Mocks.SetOfStrings.SET,
            context.fromBencode(Mocks.SetOfStrings.IR, TypeHolder.ofSet(String::class))
        )
    }
}