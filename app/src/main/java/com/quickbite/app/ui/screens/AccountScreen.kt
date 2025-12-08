package com.quickbite.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.quickbite.app.components.QuickBiteTopAppBar
import com.quickbite.app.model.Address
import com.quickbite.app.viewmodel.UserViewModel

@Composable
fun AccountScreen(
    navController: NavHostController,
    userVM: UserViewModel,
    onLogout: () -> Unit = {}
) {
    val user by userVM.user.collectAsState()
    val message by userVM.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editStreet by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editProvince by remember { mutableStateOf("") }
    var editPostalCode by remember { mutableStateOf("") }
    var editingType by remember { mutableStateOf("Profile") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { userVM.updateUserProfile(avatarId = it.toString()) }
        }
    )

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            userVM.clearMessage()
        }
    }

    if (showAvatarDialog) {
        // ... (Avatar Dialog remains the same)
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") })
                    OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("Phone") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    userVM.updateUserProfile(displayName = editName, phoneNumber = editPhone)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }

    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Manage Addresses") },
            text = {
                Column {
                    user?.addresses?.forEach { address ->
                        Text(address.toDisplayString(), modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = editStreet, onValueChange = { editStreet = it }, label = { Text("Street") })
                    OutlinedTextField(value = editCity, onValueChange = { editCity = it }, label = { Text("City") })
                    OutlinedTextField(value = editProvince, onValueChange = { editProvince = it }, label = { Text("Province") })
                    OutlinedTextField(value = editPostalCode, onValueChange = { editPostalCode = it }, label = { Text("Postal Code") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newAddress = Address(editStreet, editCity, editProvince, editPostalCode)
                    userVM.addAddress(newAddress)
                    editStreet = ""
                    editCity = ""
                    editProvince = ""
                    editPostalCode = ""
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddressDialog = false }) { Text("Done") } }
        )
    }

    Scaffold(
        topBar = { QuickBiteTopAppBar(title = "User Account", canNavigateBack = false) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val avatarId = user?.avatarId
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp) 
                            .clickable { showAvatarDialog = true }
                    ) {
                        if (avatarId != null && avatarId.contains("content://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(Uri.parse(avatarId))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val avatarColor = when (avatarId) {
                                "avatar_1" -> Color(0xFF2196F3)
                                "avatar_2" -> Color(0xFFE91E63)
                                "avatar_3" -> Color(0xFF4CAF50)
                                "avatar_4" -> Color(0xFFFF9800)
                                else -> Color(0xFFE0E0E0)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(avatarColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    tint = Color.White
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Avatar",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
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
                    subtitle = "Update your name, phone number",
                    onClick = { showEditDialog = true }
                )
                Divider(modifier = Modifier.padding(horizontal = 56.dp))
                AccountMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Saved Places",
                    subtitle = user?.addresses?.firstOrNull()?.toDisplayString() ?: "Add an address",
                    onClick = { showAddressDialog = true }
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
                        onLogout()
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
