package balaji.project.cryptopricetracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import balaji.project.cryptopricetracker.R
import balaji.project.cryptopricetracker.favourites.AppDatabase
import balaji.project.cryptopricetracker.favourites.FavoriteDetailsRepository
import balaji.project.cryptopricetracker.favourites.FavoriteDetailsViewModel
import balaji.project.cryptopricetracker.favourites.FavoritesViewModelFactory
import coil.compose.AsyncImage

@Composable
fun FavoriteDetailsScreen(onCoinClick: (String) -> Unit = {}) {

    val ctx = LocalContext.current
    val db = AppDatabase.getDatabase(ctx)
    val repo = FavoriteDetailsRepository(db.favoriteCoinDetailsDao())
    val viewModel: FavoriteDetailsViewModel = viewModel(
        factory = FavoritesViewModelFactory(repo)
    )

    val list by viewModel.favorites.collectAsState()

    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites added")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.first_color))
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {


            // Title
            Text(
                text = "Favourites",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            items(list) { coin ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .clickable { onCoinClick(coin.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = coin.image,
                            contentDescription = coin.name,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(coin.name, fontSize = 18.sp)
                            Text("${coin.symbol.uppercase()} • ${coin.selectedCurrency.uppercase()}")
                            Text(
                                "Price: ${currencySymbol(coin.selectedCurrency)}${coin.currentPrice}",
                                color = Color.Gray
                            )
                        }

                        IconButton(onClick = {
                            viewModel.deleteFavorite(coin)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }
    }
}
