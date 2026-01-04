package balaji.s3523978project.cryptopricetracker

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
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen(navController = NavHostController(LocalContext.current))
}

@Composable
fun ResetPasswordScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var step2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()



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
                text = "Reset Account Password",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(Modifier.height(20.dp))

            if (!step2) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.white),
                        ),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text(text = "Email") }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.white),
                        ),
                    value = dob,
                    onValueChange = { dob = it },
                    placeholder = { Text(text = "Date of Birth (dd-mm-yyyy)") }
                )

                Spacer(Modifier.height(20.dp))


                Text(
                    modifier = Modifier
                        .clickable {
                            loading = true
                            errorMessage = ""
                            successMessage = ""

                            val key = email.replace(".", ",")

                            FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).get()
                                .addOnSuccessListener { snapshot ->
                                    loading = false

                                    if (!snapshot.exists()) {
                                        errorMessage = "User not found"
                                        return@addOnSuccessListener
                                    }

                                    val dbEmail = snapshot.child("email").value?.toString() ?: ""
                                    val dbDob = snapshot.child("dob").value?.toString() ?: ""

                                    if (dbEmail == email && dbDob == dob) {
                                        step2 = true // show new password fields
                                    } else {
                                        errorMessage = "Email or DOB incorrect"
                                    }
                                }
                                .addOnFailureListener {
                                    loading = false
                                    errorMessage = "Error: ${it.localizedMessage}"
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
                    text = "Verify",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.first_color),
                    )
                )
            }

            if (step2) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.white),
                        ),
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text(text = "New Password") }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.white),
                        ),
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text(text = "Confirm Password") }
                )

                Spacer(Modifier.height(20.dp))


                Text(
                    modifier = Modifier
                        .clickable {
                            errorMessage = ""
                            successMessage = ""

                            if (newPassword != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@clickable
                            }

                            loading = true

                            val key = email.replace(".", ",")

                            FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).child("password").setValue(newPassword)
                                .addOnSuccessListener {
                                    loading = false
                                    successMessage = "Password updated successfully!"


                                    navController.navigate(AppScreens.Login.route) {
                                        popUpTo(AppScreens.ForgotPassword.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    loading = false
                                    errorMessage = "Failed to update password"
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
                    text = "Update Password",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.first_color),
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (loading) Text("Processing...")

        if (errorMessage.isNotEmpty())
            Text(errorMessage, color = MaterialTheme.colorScheme.error)

        if (successMessage.isNotEmpty())
            Text(successMessage, color = MaterialTheme.colorScheme.primary)
    }

}