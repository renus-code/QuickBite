package com.quickbite.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
// import com.quickbite.app.R // Commented out until the drawable is available

@Composable
fun GiftCardLandingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "QuickBite Gift Cards",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The perfect treat for any occasion. Send a gift card to friends and family, or redeem one for yourself.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // You will need to add a suitable image to your drawable resources
        // For now, this will show a placeholder if R.drawable.gift_card_art is not available.
        // Image(painter = painterResource(id = R.drawable.gift_card_art), contentDescription = "Gift Card Art")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("purchaseGiftCard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Purchase a Gift Card")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("redeemGiftCard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Redeem a Gift Card")
        }
    }
}
