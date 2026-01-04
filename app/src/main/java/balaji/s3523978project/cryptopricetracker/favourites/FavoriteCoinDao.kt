package balaji.s3523978project.cryptopricetracker.favourites


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteCoinDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(coin: FavoriteCoinDetails)

    @Delete
    suspend fun removeFavorite(coin: FavoriteCoinDetails)

    @Query("SELECT * FROM favorite_coin_details")
    fun getAllFavorites(): kotlinx.coroutines.flow.Flow<List<FavoriteCoinDetails>>

    @Query("SELECT * FROM favorite_coin_details WHERE id = :id")
    suspend fun getFavoriteById(id: String): FavoriteCoinDetails?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_coin_details WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
}

