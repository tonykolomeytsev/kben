package kekmech.kben.converter

import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import okhttp3.ResponseBody
import retrofit2.Converter

internal class KbenResponseBodyConverter<T>(
    private val kben: Kben,
    private val typeHolder: TypeHolder,
): Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T =
        kben.fromBencode(value.bytes().inputStream(), typeHolder)
}