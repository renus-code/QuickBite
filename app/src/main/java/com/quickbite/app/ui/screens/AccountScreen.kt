package com.quickbite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun AccountScreen(
    navController: NavHostController,
    userVM: UserViewModel,
    onLogout: () -> Unit = {} // Added callback
) {
    val user by userVM.user.collectAsState()
    val message by userVM.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog States
    var showEditDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editPaymentDetail by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf("Credit Card") }
    var editingType by remember { mutableStateOf("Profile") } // "Profile", "Payment", "Address"

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            userVM.clearMessage()
        }
    }

    fun openEditDialog(type: String) {
        editingType = type
        editName = user?.displayName ?: ""
        editPhone = user?.phoneNumber ?: ""
        editAddress = user?.address ?: ""
        
        val currentPayment = user?.paymentMethod ?: ""
        if (currentPayment.startsWith("Credit Card")) {
            selectedPaymentType = "Credit Card"
            editPaymentDetail = currentPayment.removePrefix("Credit Card: ").trim()
        } else if (currentPayment.startsWith("PayPal")) {
            selectedPaymentType = "PayPal"
            editPaymentDetail = currentPayment.removePrefix("PayPal: ").trim()
        } else if (currentPayment.startsWith("Apple Pay")) {
            selectedPaymentType = "Apple Pay"
            editPaymentDetail = currentPayment.removePrefix("Apple Pay: ").trim()
        } else {
            selectedPaymentType = "Credit Card"
            editPaymentDetail = currentPayment
        }
        
        showEditDialog = true
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Choose Profile Picture") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val avatars = listOf("avatar_1" to Color(0xFF2196F3), "avatar_2" to Color(0xFFE91E63), "avatar_3" to Color(0xFF4CAF50), "avatar_4" to Color(0xFFFF9800))
                    avatars.forEach { (id, color) ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(color, CircleShape)
                                .clickable {
                                    userVM.updateUserProfile(avatarId = id)
                                    showAvatarDialog = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit $editingType") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (editingType) {
                        "Profile" -> {
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "Address" -> {
                            OutlinedTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                label = { Text("Address") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                        "Payment" -> {
                            Column {
                                Text("Select Method:", style = MaterialTheme.typography.labelLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = selectedPaymentType == "Credit Card", onClick = { selectedPaymentType = "Credit Card" })
                                    Text("Credit Card")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = selectedPaymentType == "PayPal", onClick = { selectedPaymentType = "PayPal" })
                                    Text("PayPal")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = selectedPaymentType == "Apple Pay", onClick = { selectedPaymentType = "Apple Pay" })
                                    Text("Apple Pay")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = editPaymentDetail,
                                    onValueChange = { editPaymentDetail = it },
                                    label = { Text(if (selectedPaymentType == "Credit Card") "Card Number (Last 4)" else "Email/ID") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val finalPaymentString = if (editingType == "Payment") "$selectedPaymentType: $editPaymentDetail" else null
                    userVM.updateUserProfile(
                        phoneNumber = if (editingType == "Profile") editPhone else null,
                        address = if (editingType == "Address") editAddress else null,
                        paymentMethod = finalPaymentString
                    )
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuickBiteTopAppBar(
                title = "User Account", // Reverted to "User Account"
                canNavigateBack = false
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Use theme background
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // Use theme surface
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar Logic
                    val avatarColor = when (user?.avatarId) {
                        "avatar_1" -> Color(0xFF2196F3)
                        "avatar_2" -> Color(0xFFE91E63)
                        "avatar_3" -> Color(0xFF4CAF50)
                        "avatar_4" -> Color(0xFFFF9800)
                        else -> Color(0xFFE0E0E0) // Light gray default
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(avatarColor, CircleShape)
                            .clickable { showAvatarDialog = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                         Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.White
                        )
                        // Overlay edit icon if default
                        if (user?.avatarId == null) {
                             Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = user?.displayName ?: "Guest",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user?.email ?: "Sign in to continue",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!user?.phoneNumber.isNullOrEmpty()) {
                         Text(
                            text = user?.phoneNumber!!,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Added Wallet Balance Display
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Wallet Balance: $${user?.balance ?: 0.0}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Menu Options
            Text(
                text = "Account Settings",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AccountMenuItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update phone number",
                    onClick = { openEditDialog("Profile") }
                )
                Divider(modifier = Modifier.padding(horizontal = 56.dp))
                AccountMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Saved Places",
                    subtitle = user?.address ?: "Add home address",
                    onClick = { openEditDialog("Address") }
                )
                Divider(modifier = Modifier.padding(horizontal = 56.dp))
                AccountMenuItem(
                    icon = Icons.Default.CreditCard,
                    title = "Payment Methods",
                    subtitle = user?.paymentMethod ?: "Add payment method",
                    onClick = { openEditDialog("Payment") }
                )
                Divider(modifier = Modifier.padding(horizontal = 56.dp))
                AccountMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    subtitle = "App preferences",
                    onClick = { navController.navigate("settings") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Logout / Delete
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AccountMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Log Out",
                    subtitle = null,
                    textColor = MaterialTheme.colorScheme.error,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        userVM.logout()
                        onLogout() // Call the callback
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
