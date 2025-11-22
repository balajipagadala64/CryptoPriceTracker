package balaji.project.cryptopricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val sampleAssets = listOf(
                CryptoAsset("btc", "Bitcoin", "BTC", 68500.50, 2.34, Color(0xFFF7931A)),
                CryptoAsset("eth", "Ethereum", "ETH", 3950.25, -1.12, Color(0xFF627EEA)),
                CryptoAsset("bnb", "Binance Coin", "BNB", 605.10, 0.88, Color(0xFFF3BA2F)),
                CryptoAsset("sol", "Solana", "SOL", 145.75, 5.01, Color(0xFF9945FF)),
                CryptoAsset("ada", "Cardano", "ADA", 0.52, -0.45, Color(0xFF0033AD)),
                CryptoAsset("doge", "Dogecoin", "DOGE", 0.15, 3.10, Color(0xFFC2A633)),
            )

            CryptoTrackerHomeScreen(assets = sampleAssets)

        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sampleAssets = listOf(
        CryptoAsset("btc", "Bitcoin", "BTC", 68500.50, 2.34, Color(0xFFF7931A)),
        CryptoAsset("eth", "Ethereum", "ETH", 3950.25, -1.12, Color(0xFF627EEA)),
        CryptoAsset("bnb", "Binance Coin", "BNB", 605.10, 0.88, Color(0xFFF3BA2F)),
        CryptoAsset("sol", "Solana", "SOL", 145.75, 5.01, Color(0xFF9945FF)),
        CryptoAsset("ada", "Cardano", "ADA", 0.52, -0.45, Color(0xFF0033AD)),
        CryptoAsset("doge", "Dogecoin", "DOGE", 0.15, 3.10, Color(0xFFC2A633)),
    )

    MaterialTheme {
        CryptoTrackerHomeScreen(assets = sampleAssets)
    }
}

