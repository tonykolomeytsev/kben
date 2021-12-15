# Kben

Kben - is a simple Bencode library for Kotlin. Library makes it easy to serialize data class instances to bencode and deserialize bencode back to objects.

### Features

* Provide simple `toBencode()` and `fromBencode()` methods to convert Kotlin objects to Bencode and vice-versa.
* Allow custom representations for objects.
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

  `implementation("com.github.tonykolomeytsev.kben:kben-core:{latest-version}")`

- Retrofit Converter

  `implementation("com.github.tonykolomeytsev.kben:kben-retrofit-converter:{latest-version}")`

### Usage sample

Converting objects  to bencode:

```kotlin
data class User(val name: String, val password: String)
...
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
kben.fromBencode(
    "l4:kben3:are7:awesomee", 
    TypeHolder.ofList(String::class)) // listOf("kben", "are", "awesome")
kben.fromBencode<User>(
    "d4:name4John8:password8:p@55w0rDe") // User("John", "p@55w0rD")
```

### What has not been completed yet? 

- Error handling.
- Support deserialization for objects of classes with `Any` parameter types.