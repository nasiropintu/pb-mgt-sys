package com.nasirpintu.pblos.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF111C2F)
private val BrandBlue = Color(0xFF2858A5)
private val Border = Color(0xFFE1E7EF)
private val Ink = Color(0xFF182230)
private val Muted = Color(0xFF667085)
private val CardSurface = Color(0xFFF7F9FC)

@Composable
fun SettingsView(
    onExport: (passphrase: String) -> Unit,
    onImport: (passphrase: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var exportPassphrase by remember { mutableStateOf("") }
    var importPassphrase by remember { mutableStateOf("") }
    var exportFeedback by remember { mutableStateOf<String?>(null) }
    var importFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                text = "DATA MANAGEMENT",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
                letterSpacing = 1.sp,
            )
            Text(
                text = "Settings & Backup",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
            )
            Text(
                text = "Client-side encrypted backup with AES-256-GCM and PBKDF2.",
                fontSize = 12.sp,
                color = Muted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        // Export Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔒", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Export Encrypted Backup",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Navy,
                    )
                }
                Text(
                    text = "Generates a password-protected backup archive of your devices, spaces, tasks, and inbox data.",
                    fontSize = 12.sp,
                    color = Muted,
                )
                OutlinedTextField(
                    value = exportPassphrase,
                    onValueChange = {
                        exportPassphrase = it
                        exportFeedback = null
                    },
                    label = { Text("Backup Passphrase") },
                    placeholder = { Text("Enter a secure passphrase…") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )
                Button(
                    onClick = {
                        if (exportPassphrase.isBlank()) {
                            exportFeedback = "Passphrase cannot be empty."
                        } else {
                            onExport(exportPassphrase)
                            exportFeedback = "Backup initiated."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Export Data", fontSize = 12.sp)
                }
                exportFeedback?.let {
                    Text(it, fontSize = 12.sp, color = if (it.contains("empty")) Color(0xFFE65353) else BrandBlue)
                }
            }
        }

        // Import Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📥", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Import / Restore Backup",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Navy,
                    )
                }
                Text(
                    text = "Decrypts and restores your database using your backup passphrase and verifies SHA-256 data integrity.",
                    fontSize = 12.sp,
                    color = Muted,
                )
                OutlinedTextField(
                    value = importPassphrase,
                    onValueChange = {
                        importPassphrase = it
                        importFeedback = null
                    },
                    label = { Text("Decryption Passphrase") },
                    placeholder = { Text("Enter backup passphrase…") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )
                Button(
                    onClick = {
                        if (importPassphrase.isBlank()) {
                            importFeedback = "Passphrase cannot be empty."
                        } else {
                            onImport(importPassphrase)
                            importFeedback = "Restore initiated."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Import & Restore", fontSize = 12.sp)
                }
                importFeedback?.let {
                    Text(it, fontSize = 12.sp, color = if (it.contains("empty")) Color(0xFFE65353) else BrandBlue)
                }
            }
        }
    }
}
