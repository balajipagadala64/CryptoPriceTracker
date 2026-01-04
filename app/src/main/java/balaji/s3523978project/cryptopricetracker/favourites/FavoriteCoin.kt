package balaji.s3523978project.cryptopricetracker.favourites


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_coin_details")
data class FavoriteCoinDetails(

    @PrimaryKey val id: String,

    val name: String,
    val symbol: String,
    val image: String,

    val selectedCurrency: String,

    val currentPrice: Double,
    val high24h: Double,
    val low24h: Double,
    val marketCap: Double,

    val chartData: String,

    val savedAt: Long
)

