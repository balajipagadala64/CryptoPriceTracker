package balaji.s3523978project.cryptopricetracker.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import balaji.s3523978project.cryptopricetracker.ui.theme.Yellow
import coil.compose.AsyncImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val source: NewsSource?
)

data class NewsSource(
    val name: String?
)

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getCryptoNews(
        @Query("category") category: String = "business",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String
    ): NewsApiResponse
}


object NewsApiHelper {
    private const val BASE_URL = "https://newsapi.org/v2/"

    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoNewsScreen() {

    val TAG = "CryptoNewsScreen"

    var news by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    val apiKey = "ecb1729e2f2c4920bb7d2a45c67f4c2d"

    LaunchedEffect(Unit) {
        try {
            Log.d(TAG, "Fetching crypto news...")

            val response = NewsApiHelper.api.getCryptoNews(apiKey = apiKey)

            Log.d(TAG, "API Status: ${response.status}")
            Log.d(TAG, "Total Results: ${response.totalResults}")

            news = response.articles

            Log.d(TAG, "Received ${news.size} articles")

            news.forEachIndexed { index, article ->
                Log.d(TAG, "[$index] Title: ${article.title}")
                Log.d(TAG, "[$index] Source: ${article.source?.name}")
                Log.d(TAG, "[$index] URL: ${article.url}")
                Log.d(TAG, "[$index] Image: ${article.urlToImage}")
            }

        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
            Log.e(TAG, "Error fetching news: ${e.message}", e)
        }

        loading = false
        Log.d(TAG, "Loading finished")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Business News",
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

        when {
            loading -> {
                Log.d(TAG, "Loading UI shown")
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error.isNotEmpty() -> {
                Log.e(TAG, "Showing error UI: $error")
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            }

            else -> {
                Log.d(TAG, "Displaying news list UI")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(news) { article ->
                        NewsArticleCard(article)
                    }
                }
            }
        }
    }
}


@Composable
fun NewsArticleCard(article: NewsArticle) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                article.url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {

            if (article.urlToImage != null) {
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(article.title ?: "", fontSize = 18.sp)

            Spacer(Modifier.height(4.dp))

            Text(article.source?.name ?: "Unknown", color = Color.Gray)

            Spacer(Modifier.height(4.dp))

            Text(article.publishedAt ?: "", color = Color.Gray, fontSize = 12.sp)
        }
    }
}



