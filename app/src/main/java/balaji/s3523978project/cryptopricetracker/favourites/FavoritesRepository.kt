package balaji.s3523978project.cryptopricetracker.favourites


class FavoriteDetailsRepository(private val dao: FavoriteCoinDetailsDao) {

    fun getFavorites() = dao.getAllFavorites()

    suspend fun addFavorite(details: FavoriteCoinDetails) = dao.addFavorite(details)

    suspend fun removeFavorite(details: FavoriteCoinDetails) = dao.removeFavorite(details)

    suspend fun isFavorite(id: String) = dao.isFavorite(id)

    suspend fun getFavorite(id: String) = dao.getFavoriteById(id)
}

