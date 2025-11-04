package balaji.project.cryptopricetracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import balaji.project.cryptopricetracker.ui.theme.CryptoPriceTrackerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoPriceTrackerTheme {
                LoadingScreenCheck(::isUserLoggedIn)
            }
        }
    }

    private fun isUserLoggedIn(value: Int) {

        when (value) {
            2 -> {
                gotoSignInActivity(this)
            }

        }
    }
}

@Composable
fun LoadingScreenCheck(isUserLoggedIn: (value: Int) -> Unit) {
    var splashValue by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
//        splashValue = false
    }

    if (splashValue) {
        LoadingScreen()
    } else {
        isUserLoggedIn.invoke(2)
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.purple_500)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.weight(1f))


            Image(
                painter = painterResource(id = R.drawable.icon_cryptocurrency),
                contentDescription = "Crypto Currency App",
            )

            Text(
                text = "Crypto Currency App",
                color = colorResource(id = R.color.black),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.weight(1f))


        }
    }

}


@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}

fun gotoSignInActivity(context: Activity) {
//    context.startActivity(Intent(context, SignInActivity::class.java))
//    context.finish()
}