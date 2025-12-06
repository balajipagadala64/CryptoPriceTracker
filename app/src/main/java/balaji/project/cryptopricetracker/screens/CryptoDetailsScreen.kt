package balaji.project.cryptopricetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


data class MarketChartResponse(
    val prices: List<List<Double>>
)

data class CoinDetailResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val image: ImageData,
    val market_data: MarketData
)

data class ImageData(val large: String)
data class MarketData(
    val current_price: Map<String, Double>,
    val market_cap: Map<String, Double>,
    val high_24h: Map<String, Double>,
    val low_24h: Map<String, Double>
)

@Composable
fun CryptoDetailsScreen(coinId: String) {

    var details by remember { mutableStateOf<CoinDetailResponse?>(null) }
    var chartData by remember { mutableStateOf<List<Double>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        try {
            loading = true
            details = RetrofitHelper.api.getCoinDetails(coinId)
            val history = RetrofitHelper.api.getMarketChart(coinId, days = 7)
            chartData = history.prices.map { it[1] }
        } catch (e: Exception) {
            error = e.message ?: "Error"
        }
        loading = false
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error.isNotEmpty()) {
        Text("Error: $error", color = Color.Red)
        return
    }

    val coin = details!!

    Column(Modifier.padding(16.dp)) {

        // IMAGE + NAME
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = coin.image.large,
                contentDescription = coin.name,
                modifier = Modifier.size(60.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(coin.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(coin.symbol.uppercase(), color = Color.Gray)
            }
        }

        Spacer(Modifier.height(20.dp))

        // CURRENT PRICE
        Text(
            text = "$" + String.format("%.2f", coin.market_data.current_price["usd"]),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        // PRICE HISTORY CHART
        PriceHistoryChart(chartData)
    }
}

