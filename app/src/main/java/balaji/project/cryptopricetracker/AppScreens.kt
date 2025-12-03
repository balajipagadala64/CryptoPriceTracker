package balaji.project.cryptopricetracker

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_route")
    object Login : AppScreens("login_route")
    object Register : AppScreens("register_route")
    object ForgotPassword : AppScreens("forgot_password")

    object Home : AppScreens("home_screen")
    object CryptoListScreen : AppScreens("crypto_list_screen")

}