package balaji.project.cryptopricetracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

// Data class to represent a Cryptocurrency Asset
data class CryptoAsset(
    val id: String,
    val name: String,
    val symbol: String,
    val price: Double,
    val change24h: Double, // Percentage change
    val iconPlaceholderColor: Color // Placeholder for asset icon
)

/**
 * Main Composable for the Crypto Price Tracker Home Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoTrackerHomeScreen(
    assets: List<CryptoAsset>,
    onAssetClick: (CryptoAsset) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Crypto Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to Settings */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (assets.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No assets tracked. Add some crypto to get started!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // List of Crypto Assets
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Market Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(assets, key = { it.id }) { asset ->
                    CryptoItem(asset = asset, onClick = { onAssetClick(asset) })
                }
            }
        }
    }
}

/**
 * Composable for displaying a single cryptocurrency item in the list.
 */
@Composable
fun CryptoItem(asset: CryptoAsset, onClick: () -> Unit) {
    val changeColor = if (asset.change24h >= 0) {
        Color(0xFF388E3C) // Dark Green for positive change
    } else {
        Color(0xFFD32F2F) // Dark Red for negative change
    }

    val priceFormatter = remember { DecimalFormat("$#,##0.00") }
    val changeFormatter = remember { DecimalFormat("0.00'%'") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Icon and Name/Symbol Column
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for Crypto Icon (Replace with actual image loading in production)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(asset.iconPlaceholderColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.symbol.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = asset.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Price and Change Column (Aligned Right)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = priceFormatter.format(asset.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Simple indicator for up/down trend
                    Text(
                        text = (if (asset.change24h >= 0) "+" else "") + changeFormatter.format(asset.change24h),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = changeColor
                    )
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun CryptoTrackerHomeScreenPreview() {
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