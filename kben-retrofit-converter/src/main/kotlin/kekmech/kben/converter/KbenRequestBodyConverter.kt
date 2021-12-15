package kekmech.kben.converter

import kekmech.kben.Kben
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

internal class KbenRequestBodyConverter<T>(
    private val kben: Kben,
) : Converter<T, RequestBody> {

    private val mediaType = MediaType.get("application/octet-stream")

    override fun convert(value: T): RequestBody =
        RequestBody.create(mediaType, kben.toBencode(value!!))
}