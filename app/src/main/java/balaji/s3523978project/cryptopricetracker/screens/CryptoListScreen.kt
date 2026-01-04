package balaji.s3523978project.cryptopricetracker.screens


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import balaji.s3523978project.cryptopricetracker.App
import balaji.s3523978project.cryptopricetracker.ui.theme.Yellow
import coil.compose.AsyncImage
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getTopCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CryptoItem>

    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") community: Boolean = false,
        @Query("developer_data") developer: Boolean = false,
        @Query("sparkline") sparkline: Boolean = true
    ): CoinDetailResponse

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 7
    ): MarketChartResponse
}


data class CryptoItem(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long? = null,
    val price_change_percentage_24h: Double? = null
)

object RetrofitHelper {

    private val cacheSize = (5 * 1024 * 1024) // 5 MB
    private val cache = Cache(App.context.cacheDir, cacheSize.toLong())

    private val okHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=30") // cache for 30 seconds
                .build()
        }
        .build()

    val api: CoinGeckoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApi::class.java)
    }
}



@Preview(showBackground = true)
@Composable
fun CryptoListScreenPreview() {
    CryptoListScreen(navController = NavHostController(LocalContext.current))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListScreen(navController: NavController) {

    var cryptoList by remember { mutableStateOf<List<CryptoItem>>(emptyList()) }
    var filteredList by remember { mutableStateOf<List<CryptoItem>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            cryptoList = RetrofitHelper.api.getTopCoins()
            filteredList = cryptoList  // default
        } catch (e: Exception) {
            error = e.message.toString()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Search Crypto",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Yellow,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {


                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it

                        filteredList = if (searchText.isEmpty()) {
                            cryptoList
                        } else {
                            cryptoList.filter { coin ->
                                coin.name.contains(searchText, ignoreCase = true) ||
                                        coin.symbol.contains(searchText, ignoreCase = true)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Cryptocurrency") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    shape = RoundedCornerShape(25.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()

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
                            items(filteredList) { coin ->
                                CryptoRow(coin) { clickedId ->
                                    navController.navigate("details/$clickedId")
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun CryptoRow(coin: CryptoItem,onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick(coin.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(3f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = coin.image,
                    contentDescription = coin.name,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

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
            }

            Text(
                modifier = Modifier.weight(1f),
                text = "$" + String.format("%.2f", coin.current_price),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF008000)
            )

        }
    }
}

