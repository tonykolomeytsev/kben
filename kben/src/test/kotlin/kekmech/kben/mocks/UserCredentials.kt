package kekmech.kben.mocks

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

data class UserCredentials(
    val username: String,
    val password: String,
)

class UserCredentialsTypeAdapter : TypeAdapter<UserCredentials>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): UserCredentials {
        value as BencodeElement.BencodeDictionary
        return UserCredentials(
            username = context.fromBencode(value.entries["username"]!!, String::class),
            password = context.fromBencode(value.entries["password"]!!, String::class)
        )
    }

    override fun toBencode(value: UserCredentials, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeDictionary(
            sortedMapOf(
                "username" to context.toBencode(value.username),
                "password" to context.toBencode(value.password),
            )
        )
    }
}