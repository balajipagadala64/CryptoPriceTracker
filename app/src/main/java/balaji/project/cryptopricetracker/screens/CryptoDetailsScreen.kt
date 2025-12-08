package balaji.project.cryptopricetracker.screens

// Compose & UI

// Coroutines

// MPAndroidChart
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import balaji.project.cryptopricetracker.favourites.AppDatabase
import balaji.project.cryptopricetracker.favourites.FavoriteCoinDetails
import balaji.project.cryptopricetracker.favourites.FavoriteDetailsRepository
import balaji.project.cryptopricetracker.favourites.FavoriteDetailsViewModel
import balaji.project.cryptopricetracker.favourites.FavoritesViewModelFactory
import coil.compose.AsyncImage
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


data class MarketChartResponse(val prices: List<List<Double>>)

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

// SharedPreferences keys for favorites
private const val PREFS_NAME = "crypto_prefs"
private const val PREF_FAV_KEY = "fav_coins"

private fun getSharedPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private fun getFavoriteSet(context: Context): MutableSet<String> {
    return getSharedPrefs(context).getStringSet(PREF_FAV_KEY, emptySet())?.toMutableSet()
        ?: mutableSetOf()
}

private fun toggleFavorite(context: Context, coinId: String) {
    val prefs = getSharedPrefs(context)
    val set = getFavoriteSet(context)
    if (set.contains(coinId)) set.remove(coinId) else set.add(coinId)
    prefs.edit().putStringSet(PREF_FAV_KEY, set).apply()
}

// 20 popular currencies
private val SUPPORTED_CURRENCIES = listOf(
    "usd", "inr", "gbp", "eur", "jpy",
    "aud", "cad", "chf", "cny", "sgd",
    "hkd", "sek", "nok", "rub", "zar",
    "nzd", "thb", "krw", "mxn", "brl"
)

// Utility: map some currency codes to symbols
fun currencySymbol(code: String): String = when (code.lowercase()) {
    "usd" -> "$"
    "inr" -> "₹"
    "gbp" -> "£"
    "eur" -> "€"
    "jpy" -> "¥"
    "aud" -> "A$"
    "cad" -> "C$"
    "chf" -> "CHF "
    "cny" -> "¥"
    "sgd" -> "S$"
    "hkd" -> "HK$"
    "sek" -> "kr "
    "nok" -> "kr "
    "rub" -> "₽"
    "zar" -> "R "
    "nzd" -> "NZ$"
    "thb" -> "฿"
    "krw" -> "₩"
    "mxn" -> "Mex$"
    "brl" -> "R$"
    else -> ""
}

// Format big numbers (market cap)
private fun formatLargeNumber(value: Double): String {
    val abs = kotlin.math.abs(value)
    return when {
        abs >= 1_000_000_000 -> String.format("%.2fB", value / 1_000_000_000)
        abs >= 1_000_000 -> String.format("%.2fM", value / 1_000_000)
        abs >= 1_000 -> String.format("%.2fK", value / 1_000)
        else -> String.format("%.2f", value)
    }
}

@Composable
fun CryptoDetailsTopBar(onBack: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Back Arrow
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBack() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = "Crypto Details",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


// Main enhanced details screen (final merged)
@Composable
fun CryptoDetailsScreenEnhanced(
    coinId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // UI states
    var details by remember { mutableStateOf<CoinDetailResponse?>(null) }
    var chartPrices by remember { mutableStateOf<List<Double>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // range and currency
    var rangeDays by remember { mutableStateOf(7) } // 1,7,30,365
    var selectedCurrency by remember { mutableStateOf("usd") }

    // favorites
//    var isFav by remember { mutableStateOf(getFavoriteSet(ctx).contains(coinId)) }

    // compare
    var compareMode by remember { mutableStateOf(false) }
    var compareQuery by remember { mutableStateOf("") }
    var compareSelection by remember { mutableStateOf<CryptoItem?>(null) }
    var topCoins by remember { mutableStateOf<List<CryptoItem>>(emptyList()) }
    var compareChartPrices by remember { mutableStateOf<List<Double>>(emptyList()) }
    var comparingDetails by remember { mutableStateOf<CoinDetailResponse?>(null) }


    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = FavoriteDetailsRepository(db.favoriteCoinDetailsDao())
    val viewModel: FavoriteDetailsViewModel = viewModel(
        factory = FavoritesViewModelFactory(repo)
    )

    var isFav by remember { mutableStateOf(false) }

    LaunchedEffect(coinId) {
        isFav = viewModel.isFavorite(coinId)
    }

    // Fetch details & chart whenever coinId, rangeDays or selectedCurrency changes
    LaunchedEffect(coinId, rangeDays, selectedCurrency) {
        loading = true
        error = ""
        try {
            // coin details (market_data includes many currencies map)
            details = RetrofitHelper.api.getCoinDetails(coinId)

            // chart in selected currency
            val chart = RetrofitHelper.api.getMarketChart(
                coinId,
                currency = selectedCurrency,
                days = rangeDays
            )
            chartPrices = chart.prices.map { it[1] }

            // top coins for autocomplete/suggestions
            topCoins = RetrofitHelper.api.getTopCoins(perPage = 250)

            // if compare selection exists, fetch compare chart in same currency
            compareSelection?.let { sel ->
                comparingDetails = RetrofitHelper.api.getCoinDetails(sel.id)
                val cchart = RetrofitHelper.api.getMarketChart(
                    sel.id,
                    currency = selectedCurrency,
                    days = rangeDays
                )
                compareChartPrices = cchart.prices.map { it[1] }
            }
        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
        }
        loading = false
    }

    // if compare selection changes, refetch its chart for current range & currency
    LaunchedEffect(compareSelection, rangeDays, selectedCurrency) {
        compareSelection?.let {
            try {
                comparingDetails = RetrofitHelper.api.getCoinDetails(it.id)
                val cchart = RetrofitHelper.api.getMarketChart(
                    it.id,
                    currency = selectedCurrency,
                    days = rangeDays
                )
                compareChartPrices = cchart.prices.map { p -> p[1] }
            } catch (_: Exception) {
            }
        }
    }

    // UI states while loading/error
    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
        }
        return
    }

    val coin = details ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CryptoDetailsTopBar(onBack = {
            onBack()
        })

        // Price + 24h high/low (in selected currency)
        val price = coin.market_data.current_price[selectedCurrency] ?: 0.0
        val high = coin.market_data.high_24h[selectedCurrency] ?: 0.0
        val low = coin.market_data.low_24h[selectedCurrency] ?: 0.0

        val marketCap = coin.market_data.market_cap[selectedCurrency] ?: 0.0



        // Header
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = coin.image.large,
                contentDescription = coin.name,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coin.name,
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = coin.symbol.uppercase(), color = Color.Gray)
            }

            IconButton(onClick = {
                if (isFav) {
                    viewModel.deleteFavorite(
                        FavoriteCoinDetails(
                            id = coin.id,
                            name = coin.name,
                            symbol = coin.symbol,
                            image = coin.image.large,
                            selectedCurrency = selectedCurrency,
                            currentPrice = price,
                            high24h = high,
                            low24h = low,
                            marketCap = marketCap,
                            chartData = chartPrices.joinToString(","),
                            savedAt = System.currentTimeMillis()
                        )
                    )
                    isFav = false
                } else {
                    viewModel.saveFavorite(
                        FavoriteCoinDetails(
                            id = coin.id,
                            name = coin.name,
                            symbol = coin.symbol,
                            image = coin.image.large,
                            selectedCurrency = selectedCurrency,
                            currentPrice = price,
                            high24h = high,
                            low24h = low,
                            marketCap = marketCap,
                            chartData = chartPrices.joinToString(","),
                            savedAt = System.currentTimeMillis()
                        )
                    )
                    isFav = true
                }
            }) {

            Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFav) Color.Red else Color.Gray
                )
            }

        }

        Spacer(modifier = Modifier.height(12.dp))


        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = currencySymbol(selectedCurrency) + String.format("%.2f", price),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    "24h High: " + currencySymbol(selectedCurrency) + String.format("%.2f", high),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "24h Low: " + currencySymbol(selectedCurrency) + String.format("%.2f", low),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Range toggles (1D / 7D / 30D / 1Y)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val ranges = listOf(1 to "1D", 7 to "7D", 30 to "30D", 365 to "1Y")
            ranges.forEach { (days, label) ->
                val selected = days == rangeDays
                Button(
                    onClick = { rangeDays = days },
                    colors = if (selected) ButtonDefaults.buttonColors(
                        containerColor = Color(
                            0xFF197BFF
                        )
                    ) else ButtonDefaults.buttonColors(),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = label, color = if (selected) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Currency selector (placed above chart per your choice)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Currency: ", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(selectedCurrency.uppercase())
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    SUPPORTED_CURRENCIES.forEach { currency ->
                        DropdownMenuItem(text = { Text(currency.uppercase()) }, onClick = {
                            selectedCurrency = currency
                            expanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            // Show market cap in selected currency
            Text(
                "Market Cap: ${currencySymbol(selectedCurrency)}${formatLargeNumber(marketCap)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            if (chartPrices.isNotEmpty()) {
                PriceHistoryChartCompose(
                    prices = chartPrices,
                    comparePrices = if (compareMode && compareChartPrices.isNotEmpty()) compareChartPrices else null,
                    mainColor = Color(0xFF197BFF),
                    compareColor = Color(0xFFf39c12)
                )
            } else {
                Text(
                    "No chart data",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats cards
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Market Cap", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        currencySymbol(selectedCurrency) + formatLargeNumber(
                            coin.market_data.market_cap[selectedCurrency] ?: 0.0
                        ), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("24h High / Low", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        currencySymbol(selectedCurrency) + String.format("%.2f", high),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        currencySymbol(selectedCurrency) + String.format("%.2f", low),
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Compare controls
//        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            Text(text = "Compare", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
//            Switch(checked = compareMode, onCheckedChange = { compareMode = it })
//
//            if (compareMode) {
//                Spacer(modifier = Modifier.width(8.dp))
//                OutlinedTextField(
//                    value = compareQuery,
//                    onValueChange = { compareQuery = it },
//                    modifier = Modifier.weight(1f),
//                    placeholder = { Text("Type coin id/name (eg. ethereum)") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                    keyboardActions = KeyboardActions(onDone = { /* hide keyboard */ })
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(onClick = {
//                    // try to resolve compareQuery to an id using topCoins (simple exact/fuzzy match)
//                    val candidate = topCoins.firstOrNull { c ->
//                        c.id.equals(compareQuery, true) || c.name.equals(compareQuery, true) || c.symbol.equals(compareQuery, true)
//                    }
//                    candidate?.let {
//                        compareSelection = it
//                        // fetch compare data
//                        scope.launch {
//                            try {
//                                comparingDetails = RetrofitHelper.api.getCoinDetails(it.id)
//                                val cchart = RetrofitHelper.api.getMarketChart(it.id, currency = selectedCurrency, days = rangeDays)
//                                compareChartPrices = cchart.prices.map { p -> p[1] }
//                            } catch (_: Exception) {}
//                        }
//                    }
//                }) {
//                    Text("Add")
//                }
//            }
//        }

        // If compare selected, show brief info
        compareSelection?.let { sel ->
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = sel.image,
                    contentDescription = sel.name,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(sel.name)
                Spacer(modifier = Modifier.width(8.dp))
                Text(sel.symbol.uppercase(), color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Extra details (IDs)
        Text("More details", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        Text("ID: ${coin.id}")
        Text("Symbol: ${coin.symbol}")
    }
}

// Chart composable using MPAndroidChart inside AndroidView
@Composable
fun PriceHistoryChartCompose(
    prices: List<Double>,
    comparePrices: List<Double>? = null,
    mainColor: Color,
    compareColor: Color? = null
) {
    val entries = prices.mapIndexed { idx, v -> Entry(idx.toFloat(), v.toFloat()) }
    val compareEntries = comparePrices?.mapIndexed { idx, v -> Entry(idx.toFloat(), v.toFloat()) }

    AndroidView(
        factory = { ctx ->
        LineChart(ctx).apply {
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            description = Description().apply { text = "" }
            setNoDataText("No data")
            isDragEnabled = true
            setScaleEnabled(true)
        }
    }, update = { chart ->
        val ds = LineDataSet(entries, "Price").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
            setDrawCircles(false)
            lineWidth = 2f
            color = mainColor.toArgb()
            setDrawFilled(true)
            fillAlpha = 160
            // gradient fill drawable
            val startColor = mainColor.copy(alpha = 0.9f).toArgb()
            val endColor = Color.White.copy(alpha = 0.05f).toArgb()
            val gradient = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startColor, endColor)
            )
            setFillDrawable(gradient)
        }

        val dataSets = mutableListOf<ILineDataSet>(ds)

        compareEntries?.let { ce ->
            val ds2 = LineDataSet(ce, "Compare").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(false)
                setDrawCircles(false)
                lineWidth = 2f
                color = compareColor?.toArgb() ?: Color.Gray.toArgb()
                setDrawFilled(false)
            }
            dataSets.add(ds2)
        }

        chart.data = LineData(dataSets)
        chart.animateX(700)
        chart.invalidate()
    }, modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CryptoDetailsPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Preview (run on device to test network & chart)")
    }
}
