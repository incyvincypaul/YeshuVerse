package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    currentLanguage: LanguageEnum,
    onBackClick: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    val context = LocalContext.current
    var tapCount by remember { mutableStateOf(0) }
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }

    val isHindi = currentLanguage == LanguageEnum.HINDI

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "YeshuVerse के बारे में"
                            LanguageEnum.MALAYALAM -> "YeshuVerse-നെക്കുറിച്ച്"
                            else -> "About YeshuVerse"
                        },
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBlack,
                    titleContentColor = Gold
                )
            )
        },
        containerColor = AppBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = when (currentLanguage) {
                    LanguageEnum.HINDI ->
                        "YeshuVerse एक ग्लोबल लाइव रोसरी ऐप है, जो दुनिया भर के विश्वासियों को एक साथ प्रार्थना करने के लिए जोड़ता है।"
                    LanguageEnum.MALAYALAM ->
                        "ലോകമെമ്പാടുമുള്ള വിശ്വാസികളെ ഒരുമിച്ച് പ്രാർത്ഥിക്കാൻ ഒരുമിപ്പിക്കുന്ന ആഗോള ലൈവ് ജപമാല ആപ്പാണ് YeshuVerse."
                    else ->
                        "YeshuVerse is a global Live Rosary app connecting believers around the world to pray together."
                },
                color = TextWhite,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = when (currentLanguage) {
                    LanguageEnum.HINDI -> "सुझाव या शिकायत भेजें"
                    LanguageEnum.MALAYALAM -> "അഭിപ്രായങ്ങളും സഹായവും അയക്കുക"
                    else -> "Send Feedback or Support"
                },
                color = Gold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "अपना संदेश यहां लिखें..."
                            LanguageEnum.MALAYALAM -> "നിങ്ങളുടെ സന്ദേശം ഇവിടെ എഴുതുക..."
                            else -> "Write your message here..."
                        },
                        color = TextGray
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = GoldDim,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = Gold
                ),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("yeshuverse@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "YeshuVerse Feedback")
                        putExtra(Intent.EXTRA_TEXT, feedbackText)
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "ईमेल भेजें"
                        LanguageEnum.MALAYALAM -> "ഇമെയിൽ അയക്കുക"
                        else -> "Send Email"
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version: 1.0.0",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
                    .clickable {
                        tapCount++
                        if (tapCount >= 7) {
                            tapCount = 0
                            showAdminPinDialog = true
                        }
                    }
            )
        }
    }

    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminPinDialog = false
                enteredPin = ""
                pinError = false
            },
            title = {
                Text(
                    text = "Admin Access",
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter Admin Password:",
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        singleLine = true,
                        isError = pinError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError) {
                        Text(
                            text = "Incorrect Password.",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredPin.trim() == "gharanayeshuka") {
                            showAdminPinDialog = false
                            enteredPin = ""
                            pinError = false
                            onOpenAdmin()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black)
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdminPinDialog = false
                        enteredPin = ""
                        pinError = false
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
}
