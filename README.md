# Kben

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/tonykolomeytsev/kben?label=version)
![](https://github.com/tonykolomeytsev/kben/actions/workflows/pull_requests.yml/badge.svg?branch=master)

Kben - is a simple Bencode library for Kotlin. Library makes it easy to serialize data class instances to bencode and deserialize bencode back to objects.

**Bencode** is the encoding used by the peer-to-peer file sharing system BitTorrent for storing and transmitting loosely structured data.

### Features

* Provide simple `toBencode()` and `fromBencode()` methods to convert Kotlin objects to Bencode and vice-versa.
* Allow custom representations for objects (custom `TypeAdapter<T>`).
* Support arbitrarily complex objects (with deep inheritance hierarchies and extensive use of generic types).
* Provide converter for Retrofit.

### Download

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

  `implementation("com.github.tonykolomeytsev.kben:kben-core:0.1.3")`

- Retrofit Converter.

  `implementation("com.github.tonykolomeytsev.kben:kben-retrofit-converter:0.1.3")`

### Usage sample

Converting objects  to bencode:

```kotlin
data class User(val name: String, val password: String)
// ...
val kben = Kben()
kben.toBencode(1) // i1e
kben.toBencode("kben") // 4:kben
kben.toBencode(listOf("kben", "are", "awesome")) // l4:kben3:are7:awesomee
kben.tpBencode(User("John", "p@55w0rD")) // d4:name4John8:password8:p@55w0rDe
```

Converting bencode to objects:

```kotlin
kben.fromBencode<Int>("i1e") // 1
kben.fromBencode<String>("4:kben") // "kben"
kben.fromBencode("l4:kben3:are7:awesomee", 
    TypeHolder.ofList(String::class)) // listOf("kben", "are", "awesome")
kben.fromBencode<User>(
    "d4:name4John8:password8:p@55w0rDe") // User("John", "p@55w0rD")
```

As you can see, to deserialize objects of class with type parameters, you need to provide `TypeHolder` to `fromBencode()` function (just like in Gson library). `TypeHolder` is a simplified type representation for the Kben deserializer.  `TypeHolder` is generated automatically for all classes that do not have type parameters (even if its properties still have type parameters).

**Example:**  `TypeHolder` could not be generated for `List<String>`, but easily generated automatically for `data class Wrapper(val items: List<String>)`.

If you don't want to deserialize bencode to a specific type, you can use the raw Kben datatypes (inherited from `BencodeElement`):
- `BencodeByteArray` is a `byte string` from the bencode spec.
- `BencodeInteger` is an `integer`.
- `BencodeList` is a `list`.
- `BencodeDictionary` is a `dictionary` from the bencode spec.

```kotlin
val kben = Kben()
val e = kben.fromBencode<BencodeElement>("l5:hello5:worlde")
check(e == BencodeList(elements = listOf("hello", "world")))
```

### What has not been completed yet? 

- Error handling is not yet complete.
- Deserialization for objects of classes with `Any` type parameters.
