# Kben

[![](https://jitpack.io/v/tonykolomeytsev/kben.svg)](https://jitpack.io/#tonykolomeytsev/kben)
![](https://github.com/tonykolomeytsev/kben/actions/workflows/pull_requests.yml/badge.svg?branch=master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Kben - is a simple Bencode library for Kotlin. Library makes it easy to serialize data class instances to bencode and deserialize bencode back to objects.

**Bencode** is the encoding used by the peer-to-peer file sharing system BitTorrent for storing and transmitting loosely structured data.

## Features

* Provide simple `toBencode()` and `fromBencode()` methods to convert Kotlin objects to Bencode and vice-versa.
* Allow custom representations for objects (custom `TypeAdapter<T>`).
* Support arbitrarily complex objects (with deep inheritance hierarchies and extensive use of generic types).
* Provide converter for Retrofit.

## Download

Library is distributed through JitPack.

**Add repository in the root project build.gradle:**

```groovy
allprojects {
    repositories {
        maven { url("https://jitpack.io") }
    }
}
```

**Add required dependencies:**

- Core - the library. Bencode serializer/deserializer for Kotlin.

  `implementation("com.github.tonykolomeytsev.kben:kben-core:0.1.5")`

- Retrofit Converter.

  `implementation("com.github.tonykolomeytsev.kben:kben-retrofit-converter:0.1.5")`

- Ktor ContentConverter.

  `implementation("com.github.tonykolomeytsev.kben:kben-ktor-converter:0.1.5")`

Note that the project is using Kotlin 1.6.0 and Kotlin Reflect API dependency.

## Examples

### Converting objects to bencode

```kotlin
val kben = Kben()
kben.toBencode(1) // i1e
kben.toBencode("kben") // 4:kben

kben.toBencode(
    listOf(
        "kben", 
        "are", 
        "awesome",
    )
) // l4:kben3:are7:awesomee

data class Movie(val name: String, val year: Int)
// ...
kben.toBencode(
    Movie(
        name = "The Matrix Revolutions", 
        year = 2003,
    )
) // d4:name22:The Matrix Revolutions4:yeari2003ee
```

### Converting bencode to objects

```kotlin
val kben = Kben()
kben.fromBencode<Int>("i1e") // 1
kben.fromBencode<String>("4:kben") // "kben"

kben.fromBencode(
    "l4:kben3:are7:awesomee", 
    TypeHolder.ofList(String::class)
) // listOf("kben", "are", "awesome")

data class Movie(val name: String, val year: Int)
// ...
kben.fromBencode<Movie>(
    "d4:name22:The Matrix Revolutions4:yeari2003ee"
) // Movie(name = "The Matrix Revolutions", year = 2003)
```

As you can see, to deserialize objects of class with type parameters, you need to provide `TypeHolder` to `fromBencode()` function (just like in Gson library). `TypeHolder` is a simplified type representation for the Kben deserializer.  `TypeHolder` is generated automatically for all classes that do not have type parameters (even if its properties still have type parameters).

**Example:**  `TypeHolder` could not be generated for `List<String>`, but easily generated automatically for `data class Wrapper(val items: List<String>)`.

If you don't want to deserialize bencode to a specific type, you can use the intermediate representation Kben datatypes (inherited from `BencodeElement`), or just use type `Any`:
- `BencodeByteString` is a `byte string` from the bencode spec.
- `BencodeInteger` is an `integer`.
- `BencodeList` is a `list`.
- `BencodeDictionary` is a `dictionary` from the bencode spec.

```kotlin
val kben = Kben()

assertEquals(
    BencodeList(elements = listOf("hello", "world")),
    kben.fromBencode<BencodeElement>("l5:hello5:worlde")
)

assertEquals(
    listOf("hello", "world"),
    kben.fromBencode<Any>("l5:hello5:worlde")
)
```

### Using custom type adapters

**ZonedDateTimeTypeAdapter.kt**:

```kotlin
class ZonedDateTimeTypeAdapter : TypeAdapter<ZonedDateTime>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder,
    ): ZonedDateTime {
        check(value is BencodeElement.BencodeByteString)
        return ZonedDateTime
            .parse(value.asString, DateTimeFormatter.ISO_DATE_TIME)
    }

    override fun toBencode(
        value: ZonedDateTime, 
        context: SerializationContext,
    ): BencodeElement {
        return BencodeElement.BencodeByteString(
            DateTimeFormatter.ISO_DATE_TIME.format(value)
        )
    }
}
```

**Usage**:

```kotlin
val kben = Kben(
    typeAdapter = mapOf(
        ZonedDateTime::class to ZonedDateTimeTypeAdapter(),
    )
)

data class Message(val content: String, val timestamp: ZonedDateTime)
// ...
val message = Message(
    content = "Hi!",
    timestamp = ZonedDateTime.now()
)
val bencodedMessage = kben.toBencode(message)
// d7:content3:Hi!9:timestamp47:2021-12-19T03:33:02.243313+03:00[Europe/Moscow]e

val decodedMessage = kben.fromBencode<Message>(bencodedMessage)
assertEquals(message, decodedMessage)
```

### Ignoring properties on serialization

Use `@kotlin.jvm.Transient` annotation for properties that should not be involved in serialization and deserialization.
```kotlin
data class User(
    val name: String,
    @Transient
    val isOnline: Boolean = false,
)
// ...
kben.toBencode(User("John", isOnline = true))
// d4:name4:Johne
```

### Default values for enum classes

You can add `@DefaultValue` annotation to one of the enum values so that it is returned in case of an enum deserialization error.
```kotlin
enum class UserStatus { ONLINE, OFFLINE, @DefaultValue UNKNOWN }
// ...
kben.fromBencode<UserStatus>("4:IDLE") // returns UserStatus.UNKNOWN
```

### Change property serialization name

Use the `@Bencode(name: String)` annotation on class properties to change their name when serializing and deserializing.
```kotlin
data class Book(
    @Bencode("issue date")
    val issueDate: LocalDate,
)
//...
val encodedBook = kben.toBencode(Book(LocalDate.now())) 
// d10:issue date10:2021-12-19e
```

### License

Distributed under the MIT license. See `LICENSE` for more information.