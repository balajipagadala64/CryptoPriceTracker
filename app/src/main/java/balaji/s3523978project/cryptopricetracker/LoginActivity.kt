package balaji.s3523978project.cryptopricetracker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase


@Preview(showBackground = true)
@Composable
fun InvestorSignInScreenPreview() {
    InvestorSignInScreen(navController = NavHostController(LocalContext.current))
}

@Composable
fun InvestorSignInScreen(navController: NavController) {

    var accountEmail by remember { mutableStateOf("") }
    var accountPassword by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()

    val context1 = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.first_color),
            ),
    ) {


        Image(
            painter = painterResource(id = R.drawable.icon_cryptocurrency), // Replace with your actual SVG drawable
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(y = (-20).dp)
                .background(
                    color = colorResource(id = R.color.first_color),
                    shape = RoundedCornerShape(
                        topStart = 40.dp,
                        topEnd = 40.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .border(
                    width = 2.dp,
                    color = colorResource(id = R.color.first_color),
                    shape = RoundedCornerShape(
                        topStart = 40.dp,
                        topEnd = 40.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )

                )
        ) {

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Crypto Price Tracker",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(
                        color = colorResource(id = R.color.white),
                    ),
                value = accountEmail,
                onValueChange = { accountEmail = it },
                placeholder = { Text(text = "Email") }
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(
                        color = colorResource(id = R.color.white),
                    ),
                value = accountPassword,
                onValueChange = { accountPassword = it },
                placeholder = { Text(text = "Password") }
            )

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                modifier = Modifier
                    .clickable {
                        when {


                            accountEmail.isBlank() -> {
                                Toast.makeText(context, "MailID missing", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            accountPassword.isBlank() -> {
                                Toast.makeText(context, "Password missing", Toast.LENGTH_SHORT)
                                    .show()

                            }

                            else -> {

                                val database = FirebaseDatabase.getInstance()
                                val databaseReference = database.reference

                                val sanitizedEmail = accountEmail.replace(".", ",")

                                databaseReference.child("InsertorAccounts").child(sanitizedEmail)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if (snapshot.exists()) {
                                            val chefData =
                                                snapshot.getValue(InvestorData::class.java)
                                            chefData?.let {

                                                if (accountPassword == it.password) {

                                                    AccountSp.markLoginStatus(context1, true)
                                                    AccountSp.saveEmail(
                                                        context1,
                                                        email = accountEmail
                                                    )
                                                    AccountSp.saveName(context1, it.name)


                                                    Toast.makeText(
                                                        context,
                                                        "Login Successfull",
                                                        Toast.LENGTH_SHORT
                                                    ).show()


                                                    navController.navigate(AppScreens.Home.route) {
                                                        popUpTo(AppScreens.Login.route) {
                                                            inclusive = true
                                                        }
                                                    }

                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Incorrect Credentials",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "No User Found",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.addOnFailureListener { exception ->
                                        println("Error retrieving data: ${exception.message}")
                                    }

                            }
                        }
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(
                        color = colorResource(id = R.color.second_color),
                        shape = RoundedCornerShape(
                            10.dp
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.second_color),
                        shape = RoundedCornerShape(
                            10.dp
                        )
                    )
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                text = "Login",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.first_color),
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable {
                        navController.navigate(AppScreens.ForgotPassword.route)
                    },
                text = "Reset My Password",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.second_color),
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable {

                        navController.navigate(AppScreens.Register.route) {
                            popUpTo(AppScreens.Login.route) { inclusive = true }
                        }

                    },
                text = "Take Me To Registration",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.second_color),
                )
            )


        }

    }
}


data class InvestorData
    (
    var name: String = "",
    var dob: String = "",
    var gender: String = "",
    var email: String = "",
    var password: String = "",
)