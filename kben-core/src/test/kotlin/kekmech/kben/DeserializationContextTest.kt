package kekmech.kben

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.StandardTypeAdaptersFactory
import kekmech.kben.domain.dto.BencodeElement
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.test.assertEquals

class DeserializationContextTest {

    data class User(
        val firstName: String,
        val lastName: String,
    )

    @Test
    fun `object deserialization`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        val userIR = BencodeElement.BencodeDictionary(
            sortedMapOf(
                "firstName" to BencodeElement.BencodeByteArray("Anton"),
                "lastName" to BencodeElement.BencodeByteArray("Kolomeytsev")
            )
        )
        assertEquals(
            User(firstName = "Anton", lastName = "Kolomeytsev"),
            context.fromBencode(userIR, TypeHolder.Simple(User::class))
        )
    }

    @Test
    fun `list of objects deserialization`() {
        val context = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters())
        val user1IR = BencodeElement.BencodeDictionary(
            sortedMapOf(
                "firstName" to BencodeElement.BencodeByteArray("Anton"),
                "lastName" to BencodeElement.BencodeByteArray("Kolomeytsev")
            )
        )
        val user2IR = BencodeElement.BencodeDictionary(
            sortedMapOf(
                "firstName" to BencodeElement.BencodeByteArray("Vasya"),
                "lastName" to BencodeElement.BencodeByteArray("Pupkin")
            )
        )
        val usersList = BencodeElement.BencodeList(listOf(user1IR, user2IR))
        assertEquals(
            listOf(
                User(firstName = "Anton", lastName = "Kolomeytsev"),
                User(firstName = "Vasya", lastName = "Pupkin"),
            ),
            context.fromBencode(usersList, TypeHolder.ofBencodeList(User::class))
        )
    }

    @Test
    fun `runtime test`() {
        val kben = Kben()
        val userSource = User("Anton", "Kolomeytsev")
        val userNew = kben.toBencode(userSource)
            .toString(Charset.defaultCharset())
            .let { kben.fromBencode<User>(it) }
        assertEquals(userSource, userNew)
    }
}