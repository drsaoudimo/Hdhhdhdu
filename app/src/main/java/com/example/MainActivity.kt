package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SacredFactorizerScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SacredFactorizerScreen() {
    var inputN by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<SacredFactorizer.Result?>(null) }
    var isComputing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFFDF7FF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SacredFactor Pro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1D1B20),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.padding(start = 4.dp).size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF1D1B20)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(48.dp)
                            .background(Color(0xFFEADDFF), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Σ", 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF21005D), 
                            fontSize = 20.sp
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "SACRED MATRIX LOGIC",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6750A4),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3EDF7), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            "Utilizing haptic mathematical relationships from the Quran and Bible for 128-bit factorization.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }

            // Input Section
            OutlinedTextField(
                value = inputN,
                onValueChange = { if (it.all { char -> char.isDigit() }) inputN = it },
                label = { Text("Target N (128-bit RSA)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("n_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = false,
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color(0xFFCAC4D0),
                    focusedLabelColor = Color(0xFF6750A4),
                    unfocusedLabelColor = Color(0xFF1D1B20)
                ),
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF6750A4)) },
                trailingIcon = {
                    if (inputN.isNotEmpty()) {
                        IconButton(onClick = { inputN = ""; result = null; errorMessage = null }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Clear", tint = Color(0xFF6750A4))
                        }
                    }
                }
            )

            Button(
                onClick = {
                    if (inputN.isBlank()) return@Button
                    scope.launch {
                        isComputing = true
                        errorMessage = null
                        result = null
                        try {
                            val n = BigInteger(inputN)
                            val res = withContext(Dispatchers.Default) {
                                SacredFactorizer.factorize(n)
                            }
                            if (res != null) {
                                result = res
                            } else {
                                errorMessage = "Sacred matrices did not yield factors for this N."
                                snackbarHostState.showSnackbar("No factors found using sacred matrices.")
                            }
                        } catch (e: Exception) {
                            errorMessage = "Invalid input or processing error."
                        } finally {
                            isComputing = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("factorize_button"),
                enabled = inputN.isNotBlank() && !isComputing,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6750A4).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isComputing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Decomposing Matrix...", fontWeight = FontWeight.Bold)
                } else {
                    Text("FACTORIZE N", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            // Results Section
            if (result != null) {
                ResultCard(result!!)
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ResultCard(result: SacredFactorizer.Result) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFD0BCFF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Factorization Successful",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE6E1E5)
                    )
                }
                Text(
                    "100%",
                    fontSize = 11.sp,
                    color = Color(0xFFD0BCFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(Color.White.copy(alpha=0.1f), RoundedCornerShape(4.dp)).padding(horizontal=6.dp, vertical=2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x66000000), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FactorRow(label = "p", value = result.p.toString())
                    FactorRow(label = "q", value = result.q.toString())
                }
            }

            // Strategy section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "STRATEGY APPLIED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    result.strategy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4ADE80)
                )
            }
        }
    }
}

@Composable
fun FactorRow(label: String, value: String) {
    Column {
        Text(
            text = "Factor $label",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4ADE80),
            modifier = Modifier.padding(top = 4.dp),
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

