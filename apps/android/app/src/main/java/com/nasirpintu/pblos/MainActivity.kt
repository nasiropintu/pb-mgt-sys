package com.nasirpintu.pblos

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nasirpintu.pblos.data.AttachmentEntity
import com.nasirpintu.pblos.data.CaptureRepository
import com.nasirpintu.pblos.data.InboxWithAttachments
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Navy = Color(0xFF111C2F)
private val BrandBlue = Color(0xFF2858A5)
private val AccentRed = Color(0xFFE65353)
private val AppBackground = Color(0xFFF3F6FA)
private val CardSurface = Color(0xFFF7F9FC)
private val Ink = Color(0xFF182230)
private val Muted = Color(0xFF667085)
private val Border = Color(0xFFE1E7EF)

private val collaborators = listOf(
    Credit("Gemini", "Google DeepMind", "Architecture & Agentic Workflows"),
    Credit("Manus", "Manus AI", "Autonomous Planning & Synthesis"),
    Credit("ChatGPT", "OpenAI", "Logic Optimization & Reasoning"),
    Credit("DeepSeek", "DeepSeek", "Algorithmic Engineering & Core Logic"),
    Credit("Claude", "Anthropic", "Systems Architecture & Clean Design"),
    Credit("Vibe by Mistral", "Mistral AI", "Fast Generation & Execution"),
)

private val openSource = listOf(
    Credit("React 19", "by Meta", "Reusable interface components and application state", "https://react.dev/"),
    Credit("React DOM", "by Meta", "Rendering React interfaces in the desktop window", "https://react.dev/"),
    Credit("Electron", "by OpenJS Foundation", "Desktop runtime powered by Chromium and Node.js", "https://www.electronjs.org/"),
    Credit("Vite", "by Vite Team", "Frontend development server and production bundler", "https://vite.dev/"),
    Credit("TypeScript", "by Microsoft", "Typed application code and shared domain contracts", "https://www.typescriptlang.org/"),
    Credit("Jetpack Compose & Material 3", "by AndroidX / Google", "Native Android UI toolkit and design components", "https://developer.android.com/jetpack/compose"),
    Credit("Room", "by AndroidX", "Structured local database with SQLite-backed persistence", "https://developer.android.com/training/data-storage/room"),
    Credit("Lifecycle Runtime Compose", "by AndroidX", "Lifecycle-aware collection of local capture updates", "https://developer.android.com/jetpack/androidx/releases/lifecycle"),
    Credit("Kotlin", "by JetBrains", "Language used for the Android application", "https://kotlinlang.org/"),
    Credit("AndroidX Activity Compose", "by Android Jetpack", "Compose integration with the Android activity lifecycle", "https://developer.android.com/jetpack/androidx"),
    Credit("Gradle", "by Gradle Inc.", "Build automation and dependency management", "https://gradle.org/"),
    Credit("Android Gradle Plugin", "by Google", "Android application build and packaging support", "https://developer.android.com/build"),
    Credit("electron-builder", "by electron-userland community", "Windows desktop packaging and installer generation", "https://www.electron.build/"),
)

private val socials = listOf(
    Social("GitHub", "@nasiropintu", "https://github.com/nasiropintu"),
    Social("X (Twitter)", "@nasiropintu", "https://x.com/nasiropintu"),
    Social("Instagram", "@the_np2", "https://www.instagram.com/the_np2/"),
    Social("Facebook", "mohammadnasiruddin.pintu", "https://www.facebook.com/mohammadnasiruddin.pintu/"),
)

private data class Social(val name: String, val handle: String, val url: String)
private data class Credit(val name: String, val author: String, val detail: String, val url: String? = null)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaptureHome() }
    }
}

@Composable
private fun CaptureHome() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repository = remember(context) { CaptureRepository.get(context) }
    val scope = rememberCoroutineScope()
    val inbox by repository.observeInbox().collectAsStateWithLifecycle(initialValue = emptyList())

    var showAbout by remember { mutableStateOf(false) }
    var isReady by remember { mutableStateOf(false) }
    var isBusy by remember { mutableStateOf(false) }
    var captureError by remember { mutableStateOf<String?>(null) }
    var textDraft by remember { mutableStateOf("") }
    var activeRecording by remember { mutableStateOf<String?>(null) }
    var recordingStartedAt by remember { mutableStateOf(0L) }
    var elapsedSeconds by remember { mutableStateOf(0L) }
    var pendingMicrophoneRequest by remember { mutableStateOf(false) }

    val startVoice: suspend () -> Unit = {
        try {
            isBusy = true
            captureError = null
            val session = repository.startVoiceCapture()
            activeRecording = session.id
            recordingStartedAt = session.startedAtElapsedMs
        } catch (error: Exception) {
            captureError = error.message ?: "Could not start recording."
        } finally {
            isBusy = false
        }
    }
    val microphonePermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingMicrophoneRequest) scope.launch { startVoice() }
        else if (!granted) captureError = "Microphone permission is needed to record a voice note."
        pendingMicrophoneRequest = false
    }

    LaunchedEffect(repository) {
        try {
            repository.initialize()
            isReady = true
        } catch (error: Exception) {
            captureError = "Local storage could not be opened: ${error.message ?: "unknown error"}"
        }
    }
    LaunchedEffect(activeRecording, recordingStartedAt) {
        while (activeRecording != null) {
            elapsedSeconds = ((android.os.SystemClock.elapsedRealtime() - recordingStartedAt) / 1000L).coerceAtLeast(0L)
            delay(500)
        }
        elapsedSeconds = 0L
    }

    DisposableEffect(lifecycleOwner, activeRecording) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val recordingId = activeRecording
                if (recordingId != null) scope.launch {
                    runCatching { repository.stopVoiceCapture(recordingId) }
                    activeRecording = null
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val colors = lightColorScheme(
        primary = BrandBlue,
        onPrimary = Color.White,
        secondary = Color(0xFF496B9C),
        background = AppBackground,
        surface = Color.White,
        onSurface = Ink,
        surfaceVariant = CardSurface,
        outline = Border,
    )

    MaterialTheme(colorScheme = colors) {
        Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = BrandBlue) {
                        Text("P&B", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
                    }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("P&B Management Sys", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("PERSONAL & BUSINESS WORKSPACE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Muted, letterSpacing = .8.sp)
                    }
                    TextButton(onClick = { showAbout = true }) { Text("About", color = BrandBlue) }
                }
                Spacer(Modifier.height(18.dp))
                Text("INBOX", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, color = BrandBlue, letterSpacing = 1.2.sp)
                Text("Capture it now. Organize it later.", color = Navy, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

                // 1. Voice Note Hero Card (Top Priority Capture)
                if (activeRecording == null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().clickable(enabled = isReady && !isBusy) {
                            captureError = null
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                scope.launch { startVoice() }
                            } else {
                                pendingMicrophoneRequest = true
                                microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEBF1FB),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🎙", fontSize = 18.sp)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Voice Note", color = Navy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tap to record instant voice note", color = Muted, fontSize = 11.sp)
                                }
                            }
                            Button(
                                enabled = isReady && !isBusy,
                                onClick = {
                                    captureError = null
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                        scope.launch { startVoice() }
                                    } else {
                                        pendingMicrophoneRequest = true
                                        microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(if (isBusy) "Starting…" else "Record", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = AccentRed,
                                    modifier = Modifier.size(12.dp)
                                ) {}
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Recording…", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(formatDuration(elapsedSeconds), color = Navy, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        val id = activeRecording
                                        if (id != null) {
                                            isBusy = true
                                            runCatching { repository.stopVoiceCapture(id) }
                                                .onFailure { captureError = it.message ?: "Could not finish the recording." }
                                            activeRecording = null
                                            isBusy = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("■ Stop & Save", fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (!isReady) Text("Opening local inbox…", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                captureError?.let { Text(it, color = Color(0xFFB42318), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

                Spacer(Modifier.height(10.dp))

                // 2. Text Note Card
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("Text note", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textDraft,
                            onValueChange = { textDraft = it },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isReady && activeRecording == null && !isBusy,
                            placeholder = { Text("Write a thought, task, or reminder…", fontSize = 13.sp) },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(9.dp),
                        )
                        Spacer(Modifier.height(9.dp))
                        Button(
                            enabled = isReady && textDraft.isNotBlank() && activeRecording == null && !isBusy,
                            onClick = { scope.launch {
                                try {
                                    isBusy = true
                                    captureError = null
                                    repository.saveText(textDraft)
                                    textDraft = ""
                                } catch (error: Exception) {
                                    captureError = error.message ?: "Could not save this note."
                                } finally {
                                    isBusy = false
                                }
                            } },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(8.dp),
                        ) { Text(if (isBusy) "Saving…" else "Save note", fontSize = 12.sp) }
                    }
                }

                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = Border)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Saved captures", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("${inbox.size}", color = BrandBlue, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
                if (inbox.isEmpty()) {
                    Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your inbox is ready", color = Navy, fontWeight = FontWeight.SemiBold)
                        Text("Saved notes and voice recordings will appear here.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(inbox, key = { it.item.id }) { entry -> CaptureCard(entry, repository::fileFor) }
                    }
                }
                TextButton(onClick = { showAbout = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Crafted with ❤️ by NasirPintu", color = BrandBlue, fontSize = 12.sp) }
            }
        }
        if (showAbout) AboutCreditsDialog(onClose = { showAbout = false }, openLink = uriHandler::openUri)
    }
}

@Composable
private fun CaptureCard(entry: InboxWithAttachments, fileFor: (AttachmentEntity) -> File?) {
    val item = entry.item
    val isVoice = item.captureType == "voice"
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border), shape = RoundedCornerShape(10.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(if (isVoice) "VOICE NOTE" else "TEXT NOTE", color = BrandBlue, fontFamily = FontFamily.Monospace, fontSize = 9.sp, letterSpacing = .7.sp)
                Text(if (item.status == "saved") "Saved" else if (item.status == "failed") "Incomplete" else item.status.replaceFirstChar { it.uppercase() }, color = if (item.status == "failed") AccentRed else Muted, fontSize = 10.sp)
            }
            if (!item.rawContent.isNullOrBlank()) {
                Text(item.rawContent, color = Ink, fontSize = 13.sp, modifier = Modifier.padding(top = 7.dp))
            }
            if (isVoice) {
                val attachment = entry.attachments.firstOrNull()
                val file = attachment?.let(fileFor)
                Spacer(Modifier.height(8.dp))
                if (file != null) AudioPlaybackButton(file)
                else Text("Audio file is incomplete or unavailable.", color = Muted, fontSize = 11.sp)
            }
            item.lastError?.let { Text(it, color = Color(0xFFB54740), fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp)) }
            Text(item.capturedAt.replace('T', ' ').removeSuffix("Z"), color = Muted, fontFamily = FontFamily.Monospace, fontSize = 9.sp, modifier = Modifier.padding(top = 7.dp))
        }
    }
}

@Composable
private fun AudioPlaybackButton(file: File) {
    var player by remember(file.path) { mutableStateOf<MediaPlayer?>(null) }
    var isLoading by remember(file.path) { mutableStateOf(false) }
    var isPlaying by remember(file.path) { mutableStateOf(false) }
    var playbackError by remember(file.path) { mutableStateOf(false) }

    DisposableEffect(file.path) {
        onDispose { runCatching { player?.release() } }
    }

    OutlinedButton(onClick = {
        val current = player
        if (current != null) {
            runCatching {
                if (current.isPlaying) { current.pause(); isPlaying = false }
                else { current.start(); isPlaying = true }
            }.onFailure { playbackError = true }
        } else {
            try {
                isLoading = true
                val mediaPlayer = MediaPlayer()
                player = mediaPlayer
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.setOnPreparedListener {
                    isLoading = false
                    isPlaying = true
                    it.start()
                }
                mediaPlayer.setOnCompletionListener {
                    runCatching { it.seekTo(0) }
                    isPlaying = false
                }
                mediaPlayer.setOnErrorListener { mp, _, _ ->
                    runCatching { mp.release() }
                    player = null
                    isLoading = false
                    isPlaying = false
                    playbackError = true
                    true
                }
                mediaPlayer.prepareAsync()
            } catch (_: Exception) {
                runCatching { player?.release() }
                player = null
                isLoading = false
                playbackError = true
            }
        }
    }, enabled = !isLoading && !playbackError) { Text(when { playbackError -> "Audio unavailable"; isLoading -> "Loading audio…"; isPlaying -> "Ⅱ Pause audio"; else -> "▶ Play audio" }, fontSize = 11.sp) }
}

private fun formatDuration(seconds: Long): String = "%02d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun AboutCreditsDialog(onClose: () -> Unit, openLink: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = Color.White,
        title = null,
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth().padding(end = 22.dp, top = 6.dp, bottom = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.nasirpintu), contentDescription = "NasirPintu", contentScale = ContentScale.Crop, modifier = Modifier.size(68.dp).clip(CircleShape))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("♡ Crafted with Love", color = AccentRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                        Text("NasirPintu", color = Navy, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text("Mohammad Nasiruddin Pintu · Creator & Architect", color = Muted, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
                Text("Dedicated to building free, private-by-design, serverless tools that empower people without paywalls, subscriptions, or data tracking.", color = Muted, fontSize = 12.sp)
                SectionDivider()
                SectionTitle("CONNECT WITH NASIRPINTU")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    SocialCard(socials[0], openLink, Modifier.weight(1f))
                    SocialCard(socials[1], openLink, Modifier.weight(1f))
                    SocialCard(socials[2], openLink, Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    SocialCard(socials[3], openLink, Modifier.weight(1f))
                    Spacer(Modifier.weight(2f))
                }
                SectionDivider()
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("♧  AI AGENT COLLABORATION CREDITS", Modifier.weight(1f))
                    Text("Human-Directed · Agent-Assisted", fontFamily = FontFamily.Monospace, color = Muted, fontSize = 8.sp)
                }
                Text("Every feature in this suite is conceived and architected by NasirPintu, engineered in active synergy with frontier artificial intelligence models and autonomous coding agents.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
                CreditRows(collaborators)
                SectionDivider()
                SectionTitle("‹/›  OPEN-SOURCE COMMUNITY CREDITS")
                Text("P&B Management Sys is designed to run locally, with zero server costs, built on the shoulders of these open-source projects:", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
                CreditRows(openSource, openSource = true, openLink = openLink)
                SectionDivider()
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 12.dp)) {
                    Text("♧", color = Color(0xFF3B8066), fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Privacy Guarantee: All document processing stays strictly on your device. Zero uploads. Zero telemetry.", color = Muted, fontSize = 10.sp)
                }
            }
        },
        confirmButton = { TextButton(onClick = onClose) { Text("Close", color = BrandBlue) } },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SectionDivider() {
    Spacer(Modifier.height(14.dp))
    HorizontalDivider(color = Border)
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, color = Color(0xFF263D5D), fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Medium, letterSpacing = .2.sp)
}

@Composable
private fun SocialCard(social: Social, openLink: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable { openLink(social.url) }, colors = CardDefaults.cardColors(containerColor = CardSurface), border = androidx.compose.foundation.BorderStroke(1.dp, Border), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 9.dp)) {
            Text(social.name, color = Ink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(social.handle, color = Muted, fontFamily = FontFamily.Monospace, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CreditRows(items: List<Credit>, openSource: Boolean = false, openLink: ((String) -> Unit)? = null) {
    items.chunked(2).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { credit ->
                val cardModifier = Modifier.weight(1f).then(
                    if (openSource && credit.url != null && openLink != null) Modifier.clickable { openLink(credit.url) } else Modifier,
                )
                Card(modifier = cardModifier, colors = CardDefaults.cardColors(containerColor = CardSurface), border = androidx.compose.foundation.BorderStroke(1.dp, Border), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(horizontal = 9.dp, vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(credit.name, color = Ink, fontFamily = if (openSource) FontFamily.Monospace else FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            if (openSource) Text(credit.author, color = Muted, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (!openSource) Text(credit.author, color = Muted, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                        Text(credit.detail, color = Muted, fontSize = 9.sp, lineHeight = 12.sp)
                    }
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(6.dp))
    }
}
