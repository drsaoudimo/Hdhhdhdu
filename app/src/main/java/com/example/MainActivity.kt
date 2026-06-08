package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var inputN by remember { mutableStateOf("115792089237316195423570985008687907853269984665640564039457584007913129639935") }
    var result by remember { mutableStateOf<SacredFactorizer.Result?>(null) }
    var isComputing by remember { mutableStateOf(false) }
    val logs = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to the bottom of logs
    LaunchedEffect(logs.size) {
        if (logs.size > 0) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Scaffold(
        containerColor = Color(0xFFFDF7FF),
        topBar = {
            Column {
                // Fake Status Bar Row (Optional, adding for pure mimicry of the design aesthetic)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(SimpleDateFormat("H:mm", Locale.getDefault()).format(Date()), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1D1B20).copy(alpha=0.7f))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(16.dp, 10.dp).border(1.dp, Color(0xFF1D1B20), RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(12.dp, 10.dp).background(Color(0xFF1D1B20), RoundedCornerShape(50)))
                    }
                }
                
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
                        IconButton(onClick = { }, modifier = Modifier.padding(start = 4.dp).size(48.dp)) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF1D1B20))
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(42.dp)
                                .background(Color(0xFFEADDFF), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Σ", fontWeight = FontWeight.Bold, color = Color(0xFF21005D), fontSize = 18.sp)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = Color(0xFFCAC4D0))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFF3EDF7))
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(selected = true, icon = "◆", label = "Analyzer")
                    BottomNavItem(selected = false, icon = "▤", label = "History")
                    BottomNavItem(selected = false, icon = "⚙", label = "Settings")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Semi-Prime Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "TARGET N (128-BIT RSA)",
                        fontSize = 12.sp,
                        color = Color(0xFF6750A4),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3EDF7), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = inputN,
                            onValueChange = { if (it.all { char -> char.isDigit() }) inputN = it },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = Color(0xFF1D1B20),
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Detected: 128-bit semi-prime", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "READY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D), // green-700
                            modifier = Modifier
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp)) // green-100
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            var selectedStrategy by remember { mutableStateOf("Auto-Hybrid") }
            val strategies = listOf("Auto-Hybrid", "Sacred A1", "GCD Tree", "SVD-NTT", "Pollard-Brent")

            // Carousel Section
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(strategies) { strategy ->
                    StrategyChip(
                        text = strategy,
                        active = selectedStrategy == strategy,
                        onClick = { selectedStrategy = strategy }
                    )
                }
            }

            // Live Log Terminal Region
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F), RoundedCornerShape(24.dp))
                    .shadow(elevation = 16.dp)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (result != null) "Factorization Complete" else "Live Execution Logs", color = Color(0xFFE6E1E5), fontWeight = FontWeight.Medium)
                        Text("20 Active Strategies", fontSize = 10.sp, color = Color(0xFFD0BCFF).copy(alpha=0.8f), fontFamily = FontFamily.Monospace)
                    }

                    if (result != null) {
                        // Display Final Result here instead of logs if we are done
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxWidth().background(Color(0x334ADE80), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF4ADE80).copy(0.4f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    FactorRow(label = "Factor p", value = result!!.p.toString())
                                    FactorRow(label = "Factor q", value = result!!.q.toString())
                                }
                            }
                            Text(
                                "STRATEGY: ${result!!.strategy}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80),
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        // Display Logs
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color(0x66000000), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                                items(logs) { log ->
                                    Text(
                                        text = "> $log",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                if (logs.isEmpty() && !isComputing) {
                                    item {
                                        Text(
                                            "> System Ready. Awaiting trigger...",
                                            color = Color(0xFF4ADE80).copy(alpha=0.5f),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Primary Action Button
            Button(
                onClick = {
                    if (inputN.isBlank() || isComputing) return@Button
                    scope.launch {
                        isComputing = true
                        result = null
                        logs.clear()
                        try {
                            val n = BigInteger(inputN)
                            val res = SacredFactorizer.factorize(n, selectedStrategy) { logLine ->
                                withContext(Dispatchers.Main) {
                                    logs.add(logLine)
                                }
                            }
                            if (res != null) {
                                result = res
                            }
                        } catch (e: Exception) {
                            logs.add("CRITICAL ERROR: Exception thrown -> \${e.message}")
                        } finally {
                            isComputing = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("factorize_button"),
                enabled = !isComputing,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6750A4).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isComputing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("FACTORIZE N", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun StrategyChip(text: String, active: Boolean, onClick: () -> Unit = {}) {
    val bg = if (active) Color(0xFF6750A4) else Color(0xFFEADDFF)
    val color = if (active) Color.White else Color(0xFF21005D)
    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
        onClick = onClick
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FactorRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(top = 4.dp), fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun BottomNavItem(selected: Boolean, icon: String, label: String) {
    val alpha = if (selected) 1f else 0.6f
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
                .background(if (selected) Color(0xFFE8DEF8) else Color.Transparent, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp, color = Color(0xFF1D1B20).copy(alpha = alpha), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = Color(0xFF1D1B20).copy(alpha = alpha), fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }
}
