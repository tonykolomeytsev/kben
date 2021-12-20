# Kben Ktor Converter

A `ContentConverter` which uses Kben for serialization to and from Bencode.

A default `Kben` instance will be created or one can be configured and passed to the `KbenBencodeConverter` to further control the serialization.

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

**Add dependency:**

`implementation("com.github.tonykolomeytsev.kben:kben-ktor-converter:latest-version")`

## Usage

```kotlin
import io.ktor.gson.*

install(ContentNegotiation) {
    kben()
}
```

Inside the `kben` block, you can access the `KbenBuilder`, for example:

```kotlin
install(ContentNegotiation) {
    kben {
        registerTypeAdapter(ZonedDateTime::class, ZonedDateTimeTypeAdapter())
    }
}
```