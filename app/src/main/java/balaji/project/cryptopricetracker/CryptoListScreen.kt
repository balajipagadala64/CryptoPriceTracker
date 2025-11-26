package balaji.project.cryptopricetracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ----------------------------
// Retrofit API Interface
// ----------------------------
interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getTopCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<CryptoItem>
}

// ----------------------------
// Data Class for API Response
// ----------------------------
data class CryptoItem(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double
)

// ----------------------------
// Retrofit Helper
// ----------------------------
object RetrofitHelper {
    val api: CoinGeckoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApi::class.java)
    }
}

// ----------------------------
// Main Activity
// ----------------------------
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            CryptoListScreen()
//        }
//    }
//}

// ----------------------------
// Composable UI Screen
// ----------------------------
@Composable
fun CryptoListScreen() {

    var cryptoList by remember { mutableStateOf<List<CryptoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Fetch crypto on first launch
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            cryptoList = RetrofitHelper.api.getTopCoins()
        } catch (e: Exception) {
            error = e.message.toString()
        }
        isLoading = false
    }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Top Cryptocurrencies") }
//            )
//        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error.isNotEmpty() -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                            .padding(10.dp)
                    ) {
                        items(cryptoList) { coin ->
                            CryptoRow(coin)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------
// Single Row UI
// ----------------------------
@Composable
fun CryptoRow(coin: CryptoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = coin.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coin.symbol.uppercase(),
                    color = Color.Gray
                )
            }

            Text(
                text = "$${coin.current_price}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF008000)
            )
        }
    }
}
