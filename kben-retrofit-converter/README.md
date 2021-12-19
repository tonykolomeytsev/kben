# Kben Retrofit Converter

A `Converter` which uses Kben for serialization to and from Bencode.

A default `Kben` instance will be created or one can be configured and passed to the `KbenConverterFactory` to further control the serialization.

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

  `implementation("com.github.tonykolomeytsev.kben:kben-retrofit-converter:latest-version")`

## Usage

```kotlin
val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(KbenConverterFactory.create())
      .build()
```