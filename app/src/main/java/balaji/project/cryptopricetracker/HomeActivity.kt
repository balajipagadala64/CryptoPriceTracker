package balaji.project.cryptopricetracker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import balaji.project.cryptopricetracker.screens.CryptoDetailsScreenEnhanced
import balaji.project.cryptopricetracker.screens.CryptoListScreen
import balaji.project.cryptopricetracker.screens.CryptoNewsScreen
import balaji.project.cryptopricetracker.screens.FavoriteDetailsScreen


@Composable
fun ContainerScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = { CustomBottomBar(navController) }
    ) {
        NavigationGraph(navController)
    }

}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object CryptoList : BottomNavItem("cryptolist", "Crypto List", Icons.Filled.CurrencyBitcoin)
    object Favourites : BottomNavItem("favourites", "Favourites", Icons.Filled.FavoriteBorder)
    object News : BottomNavItem("news", "News", Icons.Filled.Newspaper)

    object Profile :
        BottomNavItem("profile", "Update Profile", Icons.Filled.AccountCircle)
}



@Composable
fun Profile() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Update Profile")
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.CryptoList.route
    ) {
        composable(BottomNavItem.CryptoList.route) { CryptoListScreen(navController) }
        composable(BottomNavItem.Favourites.route) { FavoriteDetailsScreen(){
            navController.navigate("details/$it")
        } }
        composable(BottomNavItem.Profile.route) { Profile() }
        composable(BottomNavItem.News.route) { CryptoNewsScreen() }

        composable(
            route = "details/{coinId}"
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId")!!
            CryptoDetailsScreenEnhanced(coinId, onBack = {
                navController.popBackStack()
            })
        }



    }
}

@Composable
fun CustomBottomBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.CryptoList,
        BottomNavItem.Favourites,
        BottomNavItem.News,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    color = Color(0xAA1A1A1A), // Transparent black
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val selected = currentRoute == item.route

                BottomNavItemView(
                    item = item,
                    selected = selected
                ) {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Glow + highlight animation
    val bgColor by animateColorAsState(
        if (selected) Color(0x33197BFF) else Color(0x33000000),
        tween(300), label = ""
    )

    val borderColor by animateColorAsState(
        if (selected) Color(0xFF197BFF) else Color.Transparent,
        tween(300), label = ""
    )

    // Icon scale animation
    val scale by animateFloatAsState(
        if (selected) 1.15f else 1f,
        tween(250), label = ""
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .background(bgColor, CircleShape)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if (selected) Color(0xFF197BFF) else Color.White,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}
