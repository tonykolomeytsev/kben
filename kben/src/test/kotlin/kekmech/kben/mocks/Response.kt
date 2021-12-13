package kekmech.kben.mocks

data class BuyResponse(
    val amount: Long,
    val coin: Coin,
    val purchases: List<Purchase>,
)

data class Coin(
    val ticker: String,
    val name: String,
    val logoUrl: String,
)

data class Purchase(
    val id: Int,
    val url: String,
)

val BuyResponseMock =
    BuyResponse(
        amount = 10L,
        coin = Coin(
            ticker = "MEC",
            name = "EnergoCoin",
            logoUrl = "http://1.2.3.4/logo.url"
        ),
        purchases = listOf(
            Purchase(1, "abc"),
            Purchase(2, "def"),
        )
    )