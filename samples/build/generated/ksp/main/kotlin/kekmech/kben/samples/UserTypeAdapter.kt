package kekmech.kben.samples

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

public class UserTypeAdapter : TypeAdapter<User>() {
  public override fun fromBencode(`value`: BencodeElement, context: DeserializationContext): User {
    val dict = (`value` as BencodeElement.BencodeDictionary).entries
    return User(
      name = context.fromBencode(dict["name"]!!),
      uid = context.fromBencode(dict["uid"]!!),
    )
  }

  public override fun toBencode(`value`: User, context: SerializationContext): BencodeElement {
    error("Not implemented")
  }
}
