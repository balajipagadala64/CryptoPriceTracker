package balaji.project.cryptopricetracker.favourites


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteDetailsViewModel(private val repo: FavoriteDetailsRepository) : ViewModel() {

    val favorites = repo.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun saveFavorite(coin: FavoriteCoinDetails) {
        viewModelScope.launch { repo.addFavorite(coin) }
    }

    fun deleteFavorite(coin: FavoriteCoinDetails) {
        viewModelScope.launch { repo.removeFavorite(coin) }
    }

    suspend fun isFavorite(id: String) = repo.isFavorite(id)
}



class FavoritesViewModelFactory(private val repo: FavoriteDetailsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteDetailsViewModel(repo) as T
    }
}
