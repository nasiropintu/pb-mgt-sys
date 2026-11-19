package com.nasirpintu.pblos

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import com.nasirpintu.pblos.data.BackupManager
import com.nasirpintu.pblos.notifications.ReminderManager
import com.nasirpintu.pblos.ui.SettingsView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.text.style.TextDecoration
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
import com.nasirpintu.pblos.data.InboxItemEntity
import com.nasirpintu.pblos.data.InboxWithAttachments
import com.nasirpintu.pblos.data.SpaceEntity
import com.nasirpintu.pblos.data.TaskWithSpace
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Navy = Color(0xFF111C2F)
private val BrandBlue = Color(0xFF2858A5)
private val AccentRed = Color(0xFFE65353)
private val AppBackground = Color(0xFFF3F6FA)
private val CardSurface = Color(0xFFF7F9FC)
private val Ink = Color(0xFF182230)
private val Muted = Color(0xFF667085)
private val Border = Color(0xFFE1E7EF)
private val SuccessGreen = Color(0xFF2D7A4F)
private val AmberWarn = Color(0xFFA86625)

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

enum class AppNavTab(val label: String, val icon: String) {
    TODAY("Today", "☀️"),
    INBOX("Inbox", "📥"),
    TASKS("Tasks", "✓"),
    SPACES("Spaces", "📁"),
    SETTINGS("Settings", "⚙️")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DailyOrganizerApp() }
    }
}

@Composable
private fun DailyOrganizerApp() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repository = remember(context) { CaptureRepository.get(context) }
    val scope = rememberCoroutineScope()

    val inbox by repository.observeInbox().collectAsStateWithLifecycle(initialValue = emptyList())
    val tasks by repository.observeTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    val focusTasks by repository.observeFocusTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    val pendingTasks by repository.observePendingTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    val spaces by repository.observeSpaces().collectAsStateWithLifecycle(initialValue = emptyList())
    val latestCapture by repository.observeLatestCapture().collectAsStateWithLifecycle(initialValue = null)

    var currentTab by remember { mutableStateOf(AppNavTab.TODAY) }
    var showAbout by remember { mutableStateOf(false) }
    var showCreateTask by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskWithSpace?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpaceFilter by remember { mutableStateOf<String?>(null) }

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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        ReminderManager.createNotificationChannel(context)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var pendingExportPassphrase by remember { mutableStateOf<String?>(null) }
    var pendingImportPassphrase by remember { mutableStateOf<String?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val passphrase = pendingExportPassphrase
        pendingExportPassphrase = null
        if (uri != null && !passphrase.isNullOrBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    val backupFile = BackupManager(context).exportEncryptedBackup(passphrase)
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        backupFile.inputStream().use { input -> input.copyTo(out) }
                    }
                    backupFile.delete()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Encrypted backup saved successfully!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        val passphrase = pendingImportPassphrase
        pendingImportPassphrase = null
        if (uri != null && !passphrase.isNullOrBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    val tempFile = File(context.cacheDir, "import_${System.currentTimeMillis()}.bin")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { out -> input.copyTo(out) }
                    }
                    val result = BackupManager(context).importEncryptedBackup(tempFile, passphrase)
                    tempFile.delete()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            if (result.success) "Restore successful: ${result.message}" else result.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Application Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(shape = RoundedCornerShape(10.dp), color = BrandBlue) {
                        Text("P&B", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("P&B MGT Sys", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("PRIVATE LIFE & WORK ORGANIZER", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Muted, letterSpacing = .7.sp)
                    }
                    TextButton(onClick = { isSearching = !isSearching; if (!isSearching) searchQuery = "" }) {
                        Text(if (isSearching) "✕ Close" else "🔍 Search", color = BrandBlue, fontSize = 12.sp)
                    }
                    TextButton(onClick = { showAbout = true }) {
                        Text("About", color = BrandBlue, fontSize = 12.sp)
                    }
                }

                // Search Bar Overlay (Active when toggled)
                if (isSearching) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search notes, recordings, or tasks…", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                HorizontalDivider(color = Border)

                // Main Content View
                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (isSearching && searchQuery.isNotBlank()) {
                        SearchView(
                            query = searchQuery,
                            tasks = tasks.filter { it.task.title.contains(searchQuery, ignoreCase = true) || (it.task.description?.contains(searchQuery, ignoreCase = true) == true) },
                            inbox = inbox.filter { it.item.rawContent?.contains(searchQuery, ignoreCase = true) == true },
                            onToggleTask = { id, done -> scope.launch { repository.toggleTaskCompletion(id, done); if (!done) ReminderManager.cancelTaskReminder(context, id) } },
                            onEditTask = { editingTask = it },
                            fileFor = repository::fileFor,
                        )
                    } else {
                        when (currentTab) {
                            AppNavTab.TODAY -> TodayView(
                                focusTasks = focusTasks,
                                pendingTasks = pendingTasks,
                                latestCapture = latestCapture,
                                onToggleTask = { id, done -> scope.launch { repository.toggleTaskCompletion(id, done); if (!done) ReminderManager.cancelTaskReminder(context, id) } },
                                onRemoveFocus = { id -> scope.launch { repository.toggleTaskFocus(id, true) } },
                                onAddFocus = { id -> scope.launch { repository.toggleTaskFocus(id, false) } },
                                onEditTask = { editingTask = it },
                                onJumpToInbox = { currentTab = AppNavTab.INBOX },
                                onCreateTask = { showCreateTask = true },
                            )
                            AppNavTab.INBOX -> InboxView(
                                isReady = isReady,
                                isBusy = isBusy,
                                captureError = captureError,
                                textDraft = textDraft,
                                onTextDraftChange = { textDraft = it },
                                onSaveText = {
                                    scope.launch {
                                        try {
                                            isBusy = true
                                            captureError = null
                                            repository.saveText(textDraft)
                                            textDraft = ""
                                        } catch (e: Exception) {
                                            captureError = e.message ?: "Could not save."
                                        } finally {
                                            isBusy = false
                                        }
                                    }
                                },
                                activeRecording = activeRecording,
                                elapsedSeconds = elapsedSeconds,
                                onStartVoice = {
                                    captureError = null
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                        scope.launch { startVoice() }
                                    } else {
                                        pendingMicrophoneRequest = true
                                        microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                onStopVoice = {
                                    val id = activeRecording
                                    if (id != null) {
                                        scope.launch {
                                            isBusy = true
                                            runCatching { repository.stopVoiceCapture(id) }
                                                .onFailure { captureError = it.message ?: "Could not finish recording." }
                                            activeRecording = null
                                            isBusy = false
                                        }
                                    }
                                },
                                inbox = inbox,
                                fileFor = repository::fileFor,
                            )
                            AppNavTab.TASKS -> TasksView(
                                tasks = if (selectedSpaceFilter == null) tasks else tasks.filter { it.task.spaceId == selectedSpaceFilter },
                                spaces = spaces,
                                selectedSpaceFilter = selectedSpaceFilter,
                                onSelectSpaceFilter = { selectedSpaceFilter = it },
                                onToggleTask = { id, done -> scope.launch { repository.toggleTaskCompletion(id, done); if (!done) ReminderManager.cancelTaskReminder(context, id) } },
                                onToggleFocus = { id, isFocused -> scope.launch { repository.toggleTaskFocus(id, isFocused) } },
                                onEditTask = { editingTask = it },
                                onCreateTask = { showCreateTask = true },
                            )
                            AppNavTab.SPACES -> SpacesView(
                                spaces = spaces,
                                tasks = tasks,
                                onSelectSpace = { spaceId ->
                                    selectedSpaceFilter = spaceId
                                    currentTab = AppNavTab.TASKS
                                },
                            )
                            AppNavTab.SETTINGS -> SettingsView(
                                onExport = { passphrase: String ->
                                    pendingExportPassphrase = passphrase
                                    val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                                    createDocumentLauncher.launch("pblos-backup-$timestamp.bin")
                                },
                                onImport = { passphrase: String ->
                                    pendingImportPassphrase = passphrase
                                    openDocumentLauncher.launch(arrayOf("*/*"))
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Border)

                // Persistent Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(64.dp),
                ) {
                    AppNavTab.values().forEach { tab ->
                        val selected = currentTab == tab && (!isSearching || searchQuery.isBlank())
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                currentTab = tab
                                isSearching = false
                                searchQuery = ""
                            },
                            icon = { Text(tab.icon, fontSize = 17.sp) },
                            label = { Text(tab.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandBlue,
                                selectedTextColor = BrandBlue,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted,
                                indicatorColor = Color(0xFFEBF1FB),
                            ),
                        )
                    }
                }
            }
        }

        if (showCreateTask) {
            CreateTaskDialog(
                spaces = spaces,
                onDismiss = { showCreateTask = false },
                onCreate = { title, desc, spaceId, priority, dueAt, isFocus ->
                    scope.launch {
                        val id = repository.createTask(
                            title = title,
                            description = desc,
                            spaceId = spaceId,
                            priority = priority,
                            dueAt = dueAt,
                            isFocusToday = isFocus,
                        )
                        dueAt?.let { iso ->
                            val millis = ReminderManager.parseIsoToMillis(iso)
                            if (millis != null && millis > System.currentTimeMillis()) {
                                val space = spaces.find { it.id == spaceId }
                                ReminderManager.scheduleTaskReminder(context, id, title, space?.name, millis)
                            }
                        }
                        showCreateTask = false
                    }
                },
            )
        }

        editingTask?.let { taskWithSpace ->
            EditTaskDialog(
                taskWithSpace = taskWithSpace,
                spaces = spaces,
                onDismiss = { editingTask = null },
                onSave = { title, desc, spaceId, priority, dueAt, isFocus ->
                    scope.launch {
                        repository.updateTaskDetails(
                            id = taskWithSpace.task.id,
                            title = title,
                            description = desc,
                            spaceId = spaceId,
                            priority = priority,
                            dueAt = dueAt,
                            isFocusToday = isFocus,
                        )
                        ReminderManager.cancelTaskReminder(context, taskWithSpace.task.id)
                        dueAt?.let { iso ->
                            val millis = ReminderManager.parseIsoToMillis(iso)
                            if (millis != null && millis > System.currentTimeMillis()) {
                                val space = spaces.find { it.id == spaceId }
                                ReminderManager.scheduleTaskReminder(context, taskWithSpace.task.id, title, space?.name, millis)
                            }
                        }
                        editingTask = null
                    }
                },
                onDelete = {
                    scope.launch {
                        ReminderManager.cancelTaskReminder(context, taskWithSpace.task.id)
                        repository.deleteTask(taskWithSpace.task.id)
                        editingTask = null
                    }
                },
            )
        }

        if (showAbout) AboutCreditsDialog(onClose = { showAbout = false }, openLink = uriHandler::openUri)
    }
}

// -------------------------------------------------------------
// TAB 1: TODAY VIEW (TOP 3 PRIORITIES, RECENT NOTE, DUE TASKS)
// -------------------------------------------------------------
@Composable
private fun TodayView(
    focusTasks: List<TaskWithSpace>,
    pendingTasks: List<TaskWithSpace>,
    latestCapture: InboxItemEntity?,
    onToggleTask: (String, Boolean) -> Unit,
    onRemoveFocus: (String) -> Unit,
    onAddFocus: (String) -> Unit,
    onEditTask: (TaskWithSpace) -> Unit = {},
    onJumpToInbox: () -> Unit,
    onCreateTask: () -> Unit,
) {
    val dateString = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(dateString, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BrandBlue, letterSpacing = .8.sp)
                    Text("Today's Focus", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Navy)
                }
                Surface(color = Color(0xFFE7F3EC), shape = RoundedCornerShape(12.dp)) {
                    Text("● All local", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        // Section: Top 3 Priorities
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TOP 3 PRIORITIES", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BrandBlue, letterSpacing = 1.sp)
                        Text("${focusTasks.size} / 3", fontSize = 11.sp, color = Muted, fontFamily = FontFamily.Monospace)
                    }
                    Text("Choose the 3 most important things to complete today.", fontSize = 11.sp, color = Muted, modifier = Modifier.padding(top = 2.dp, bottom = 10.dp))

                    if (focusTasks.isEmpty()) {
                        Surface(
                            color = CardSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Text("No focus priorities set yet today. Promote a task below or tap '+ Add Task'.", fontSize = 11.sp, color = Muted, modifier = Modifier.padding(12.dp))
                        }
                    } else {
                        focusTasks.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("${index + 1}.", fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 14.sp, modifier = Modifier.width(20.dp))
                                Checkbox(
                                    checked = item.task.status == "completed",
                                    onCheckedChange = { onToggleTask(item.task.id, item.task.status == "completed") },
                                    colors = CheckboxDefaults.colors(checkedColor = BrandBlue),
                                )
                                Column(modifier = Modifier.weight(1f).clickable { onEditTask(item) }) {
                                    Text(
                                        item.task.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Ink,
                                        textDecoration = if (item.task.status == "completed") TextDecoration.LineThrough else null,
                                    )
                                    item.space?.let {
                                        Text(it.name, fontSize = 9.sp, color = Muted, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                TextButton(onClick = { onRemoveFocus(item.task.id) }) {
                                    Text("Remove", fontSize = 10.sp, color = Muted)
                                }
                            }
                        }
                    }

                    if (focusTasks.size < 3) {
                        Spacer(Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onCreateTask,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("+ Add Priority Task", fontSize = 11.sp, color = BrandBlue)
                        }
                    }
                }
            }
        }

        // Section: Continue Recent Capture
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("RECENT CAPTURE", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BrandBlue, letterSpacing = .9.sp)
                    if (latestCapture != null) {
                        Text(
                            if (latestCapture.captureType == "voice") "🎙 Voice Note (${latestCapture.capturedAt.take(10)})"
                            else (latestCapture.rawContent ?: "Text note"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Ink,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
                        )
                    } else {
                        Text("No notes captured yet.", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp, bottom = 6.dp))
                    }
                    TextButton(onClick = onJumpToInbox, modifier = Modifier.align(Alignment.End)) {
                        Text("Open Inbox →", fontSize = 11.sp, color = BrandBlue)
                    }
                }
            }
        }

        // Section: Pending Tasks
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pending Tasks", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Text("${pendingTasks.size}", fontSize = 11.sp, color = BrandBlue, fontFamily = FontFamily.Monospace)
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                Text("No pending tasks. You're all caught up!", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(vertical = 10.dp))
            }
        } else {
            items(pendingTasks.take(5), key = { it.task.id }) { item ->
                TaskItemCard(
                    item = item,
                    onToggleCompletion = { onToggleTask(item.task.id, item.task.status == "completed") },
                    onToggleFocus = {
                        if (item.task.isFocusToday == 1) onRemoveFocus(item.task.id)
                        else onAddFocus(item.task.id)
                    },
                    onClick = { onEditTask(item) },
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: INBOX VIEW (HERO VOICE, TEXT ENTRY, SAVED FEED)
// -------------------------------------------------------------
@Composable
private fun InboxView(
    isReady: Boolean,
    isBusy: Boolean,
    captureError: String?,
    textDraft: String,
    onTextDraftChange: (String) -> Unit,
    onSaveText: () -> Unit,
    activeRecording: String?,
    elapsedSeconds: Long,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    inbox: List<InboxWithAttachments>,
    fileFor: (AttachmentEntity) -> File?,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("INBOX", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, color = BrandBlue, letterSpacing = 1.2.sp)
            Text("Say it. Save it. Find it. Act on it.", color = Navy, fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
            Text("Your life and work, in one calm place.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
        }

        
        item {
            if (activeRecording == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable(enabled = isReady && !isBusy, onClick = onStartVoice),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(shape = CircleShape, color = Color(0xFFEBF1FB), modifier = Modifier.size(42.dp)) {
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
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
                            onClick = onStartVoice,
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
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(shape = CircleShape, color = AccentRed, modifier = Modifier.size(12.dp)) {}
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Recording…", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(formatDuration(elapsedSeconds), color = Navy, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                        }
                        Button(
                            onClick = onStopVoice,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("■ Stop & Save", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Text Note Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(13.dp)) {
                    Text("Text note", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textDraft,
                        onValueChange = onTextDraftChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isReady && activeRecording == null && !isBusy,
                        placeholder = { Text("Write a thought, task, or reminder…", fontSize = 13.sp) },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(9.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        enabled = isReady && textDraft.isNotBlank() && activeRecording == null && !isBusy,
                        onClick = onSaveText,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(if (isBusy) "Saving…" else "Save note", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            captureError?.let { Text(it, color = Color(0xFFB42318), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Saved Captures", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("${inbox.size}", color = BrandBlue, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }

        if (inbox.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your inbox is calm", color = Navy, fontWeight = FontWeight.SemiBold)
                    Text("Captured notes and recordings will appear here.", color = Muted, fontSize = 11.sp)
                }
            }
        } else {
            items(inbox, key = { it.item.id }) { entry ->
                CaptureCard(entry, fileFor)
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: TASKS VIEW (TASK LIST, SPACE FILTER, ADD TASK)
// -------------------------------------------------------------
@Composable
private fun TasksView(
    tasks: List<TaskWithSpace>,
    spaces: List<SpaceEntity>,
    selectedSpaceFilter: String?,
    onSelectSpaceFilter: (String?) -> Unit,
    onToggleTask: (String, Boolean) -> Unit,
    onToggleFocus: (String, Boolean) -> Unit,
    onCreateTask: () -> Unit,
    onEditTask: (TaskWithSpace) -> Unit = {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("TASKS", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BrandBlue, letterSpacing = 1.sp)
                    Text("All Tasks", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy)
                }
                Button(onClick = onCreateTask, colors = ButtonDefaults.buttonColors(containerColor = BrandBlue), shape = RoundedCornerShape(8.dp)) {
                    Text("+ New Task", fontSize = 12.sp)
                }
            }
        }

        // Space Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = if (selectedSpaceFilter == null) BrandBlue else CardSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    modifier = Modifier.clickable { onSelectSpaceFilter(null) },
                ) {
                    Text("All", color = if (selectedSpaceFilter == null) Color.White else Ink, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                spaces.forEach { space ->
                    val isSelected = selectedSpaceFilter == space.id
                    Surface(
                        color = if (isSelected) BrandBlue else CardSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                        modifier = Modifier.clickable { onSelectSpaceFilter(space.id) },
                    ) {
                        Text(space.name, color = if (isSelected) Color.White else Ink, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No tasks found", color = Navy, fontWeight = FontWeight.SemiBold)
                    Text("Tap '+ New Task' above to add your first item.", color = Muted, fontSize = 11.sp)
                }
            }
        } else {
            items(tasks, key = { it.task.id }) { item ->
                TaskItemCard(
                    item = item,
                    onToggleCompletion = { onToggleTask(item.task.id, item.task.status == "completed") },
                    onToggleFocus = { onToggleFocus(item.task.id, item.task.isFocusToday == 1) },
                    onClick = { onEditTask(item) },
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: SPACES VIEW (SHOP, PERSONAL, APP DEV CONTEXTS)
// -------------------------------------------------------------
@Composable
private fun SpacesView(
    spaces: List<SpaceEntity>,
    tasks: List<TaskWithSpace>,
    onSelectSpace: (String) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("SPACES", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BrandBlue, letterSpacing = 1.sp)
            Text("Life & Work Contexts", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Keep personal notes, business records, and projects neatly isolated.", fontSize = 11.sp, color = Muted, modifier = Modifier.padding(top = 2.dp, bottom = 4.dp))
        }

        items(spaces, key = { it.id }) { space ->
            val spaceTasks = tasks.filter { it.task.spaceId == space.id }
            val pendingCount = spaceTasks.count { it.task.status != "completed" }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelectSpace(space.id) },
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = BrandBlue, modifier = Modifier.size(10.dp)) {}
                            Spacer(Modifier.width(8.dp))
                            Text(space.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy)
                        }
                        Surface(color = CardSurface, shape = RoundedCornerShape(6.dp)) {
                            Text("$pendingCount pending", fontSize = 10.sp, color = BrandBlue, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                        }
                    }
                    space.description?.let {
                        Text(it, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 6.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("View ${spaceTasks.size} tasks in ${space.name} →", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandBlue)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SEARCH OVERLAY VIEW
// -------------------------------------------------------------
@Composable
private fun SearchView(
    query: String,
    tasks: List<TaskWithSpace>,
    inbox: List<InboxWithAttachments>,
    onToggleTask: (String, Boolean) -> Unit,
    onEditTask: (TaskWithSpace) -> Unit = {},
    fileFor: (AttachmentEntity) -> File?,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Search Results for \"$query\"", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy)
        }

        if (tasks.isNotEmpty()) {
            item {
                Text("MATCHING TASKS (${tasks.size})", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
            }
            items(tasks, key = { "search_task_${it.task.id}" }) { item ->
                TaskItemCard(
                    item = item,
                    onToggleCompletion = { onToggleTask(item.task.id, item.task.status == "completed") },
                    onToggleFocus = {},
                    onClick = { onEditTask(item) },
                )
            }
        }

        if (inbox.isNotEmpty()) {
            item {
                Text("MATCHING CAPTURES (${inbox.size})", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
            }
            items(inbox, key = { "search_inbox_${it.item.id}" }) { entry ->
                CaptureCard(entry, fileFor)
            }
        }

        if (tasks.isEmpty() && inbox.isEmpty()) {
            item {
                Text("No tasks or captures match your search.", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 20.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// REUSABLE TASK ITEM CARD
// -------------------------------------------------------------
@Composable
private fun TaskItemCard(
    item: TaskWithSpace,
    onToggleCompletion: () -> Unit,
    onToggleFocus: () -> Unit,
    onClick: () -> Unit = {},
) {
    val isCompleted = item.task.status == "completed"
    val isFocused = item.task.isFocusToday == 1

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isFocused) BrandBlue.copy(alpha = 0.5f) else Border),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleCompletion() },
                colors = CheckboxDefaults.colors(checkedColor = BrandBlue),
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text(
                    item.task.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) Muted else Ink,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                )
                if (!item.task.description.isNullOrBlank()) {
                    Text(item.task.description, fontSize = 11.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    item.space?.let {
                        Surface(color = CardSurface, shape = RoundedCornerShape(4.dp)) {
                            Text(it.name, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = BrandBlue, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    item.task.dueAt?.let { dueIso ->
                        ReminderManager.formatDisplayDate(dueIso)?.let { formattedDate ->
                            val isOverdue = ReminderManager.parseIsoToMillis(dueIso)?.let { it < System.currentTimeMillis() } == true && !isCompleted
                            Surface(
                                color = if (isOverdue) Color(0xFFFFECEB) else CardSurface,
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                Text(
                                    "⏰ $formattedDate",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isOverdue) AccentRed else BrandBlue,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    if (item.task.priority != "normal") {
                        Surface(color = if (item.task.priority == "urgent") Color(0xFFFFECEB) else Color(0xFFFFF7E6), shape = RoundedCornerShape(4.dp)) {
                            Text(item.task.priority.uppercase(), fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = if (item.task.priority == "urgent") AccentRed else AmberWarn, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                }
            }
            TextButton(onClick = onToggleFocus) {
                Text(if (isFocused) "★ Focus" else "☆", color = if (isFocused) BrandBlue else Muted, fontSize = 13.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// DUE DATE & REMINDER PICKER ROW
// -------------------------------------------------------------
@Composable
private fun DueDatePickerRow(
    dueAt: String?,
    onDueAtChange: (String?) -> Unit,
) {
    val context = LocalContext.current
    val isNone = dueAt == null

    val today6pm = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
    }
    val isToday6pm = dueAt?.let { ReminderManager.parseIsoToMillis(it) == today6pm } == true

    val tomorrow9am = remember {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val isTomorrow9am = dueAt?.let { ReminderManager.parseIsoToMillis(it) == tomorrow9am } == true
    val isCustom = !isNone && !isToday6pm && !isTomorrow9am

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Reminder / Due Date", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
                color = if (isNone) BrandBlue else CardSurface,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                modifier = Modifier.clickable { onDueAtChange(null) },
            ) {
                Text("None", color = if (isNone) Color.White else Ink, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(
                color = if (isToday6pm) BrandBlue else CardSurface,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                modifier = Modifier.clickable { onDueAtChange(ReminderManager.formatMillisToIso(today6pm)) },
            ) {
                Text("Today 6 PM", color = if (isToday6pm) Color.White else Ink, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(
                color = if (isTomorrow9am) BrandBlue else CardSurface,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                modifier = Modifier.clickable { onDueAtChange(ReminderManager.formatMillisToIso(tomorrow9am)) },
            ) {
                Text("Tomorrow 9 AM", color = if (isTomorrow9am) Color.White else Ink, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(
                color = if (isCustom) BrandBlue else CardSurface,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                modifier = Modifier.clickable {
                    val c = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val timeCal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    c.set(year, month, dayOfMonth, hourOfDay, minute, 0)
                                    onDueAtChange(ReminderManager.formatMillisToIso(c.timeInMillis))
                                },
                                timeCal.get(Calendar.HOUR_OF_DAY),
                                timeCal.get(Calendar.MINUTE),
                                false,
                            ).show()
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH),
                    ).show()
                },
            ) {
                val label = if (isCustom) ReminderManager.formatDisplayDate(dueAt) ?: "Custom" else "Custom…"
                Text(label, color = if (isCustom) Color.White else Ink, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// CREATE TASK DIALOG
// -------------------------------------------------------------
@Composable
private fun CreateTaskDialog(
    spaces: List<SpaceEntity>,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String?, spaceId: String?, priority: String, dueAt: String?, isFocus: Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSpaceId by remember { mutableStateOf(spaces.firstOrNull()?.id) }
    var priority by remember { mutableStateOf("normal") }
    var dueAt by remember { mutableStateOf<String?>(null) }
    var isFocusToday by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("New Task", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Task title…", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Optional details or notes…", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp),
                )
                Text("Select Space", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    spaces.forEach { space ->
                        val isSelected = selectedSpaceId == space.id
                        Surface(
                            color = if (isSelected) BrandBlue else CardSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                            modifier = Modifier.clickable { selectedSpaceId = space.id },
                        ) {
                            Text(space.name, color = if (isSelected) Color.White else Ink, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
                Text("Priority", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("normal", "high", "urgent").forEach { p ->
                        val isSelected = priority == p
                        Surface(
                            color = if (isSelected) (if (p == "urgent") AccentRed else BrandBlue) else CardSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                            modifier = Modifier.clickable { priority = p },
                        ) {
                            Text(
                                p.uppercase(),
                                color = if (isSelected) Color.White else Ink,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
                DueDatePickerRow(dueAt = dueAt, onDueAtChange = { dueAt = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFocusToday, onCheckedChange = { isFocusToday = it }, colors = CheckboxDefaults.colors(checkedColor = BrandBlue))
                    Text("Add to Today's Top 3 Focus", fontSize = 12.sp, color = Ink)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = { onCreate(title, description, selectedSpaceId, priority, dueAt, isFocusToday) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            ) { Text("Create Task") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Muted) }
        },
    )
}

// -------------------------------------------------------------
// EDIT TASK DIALOG
// -------------------------------------------------------------
@Composable
private fun EditTaskDialog(
    taskWithSpace: TaskWithSpace,
    spaces: List<SpaceEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String?, spaceId: String?, priority: String, dueAt: String?, isFocus: Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val task = taskWithSpace.task
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description.orEmpty()) }
    var selectedSpaceId by remember { mutableStateOf(task.spaceId) }
    var priority by remember { mutableStateOf(task.priority) }
    var dueAt by remember { mutableStateOf(task.dueAt) }
    var isFocusToday by remember { mutableStateOf(task.isFocusToday == 1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Edit Task", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Task title…", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Optional details or notes…", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp),
                )
                Text("Select Space", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    spaces.forEach { space ->
                        val isSelected = selectedSpaceId == space.id
                        Surface(
                            color = if (isSelected) BrandBlue else CardSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                            modifier = Modifier.clickable { selectedSpaceId = space.id },
                        ) {
                            Text(
                                space.name,
                                color = if (isSelected) Color.White else Ink,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
                Text("Priority", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("normal", "high", "urgent").forEach { p ->
                        val isSelected = priority == p
                        Surface(
                            color = if (isSelected) (if (p == "urgent") AccentRed else BrandBlue) else CardSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                            modifier = Modifier.clickable { priority = p },
                        ) {
                            Text(
                                p.uppercase(),
                                color = if (isSelected) Color.White else Ink,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
                DueDatePickerRow(dueAt = dueAt, onDueAtChange = { dueAt = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isFocusToday,
                        onCheckedChange = { isFocusToday = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandBlue),
                    )
                    Text("Add to Today's Top 3 Focus", fontSize = 12.sp, color = Ink)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = { onSave(title, description, selectedSpaceId, priority, dueAt, isFocusToday) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            ) { Text("Save Changes") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = onDelete) {
                    Text("Delete", color = AccentRed)
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Muted)
                }
            }
        },
    )
}

// -------------------------------------------------------------
// REUSABLE CAPTURE CARD & AUDIO BUTTON
// -------------------------------------------------------------
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
    }, enabled = !isLoading && !playbackError) {
        Text(when { playbackError -> "Audio unavailable"; isLoading -> "Loading audio…"; isPlaying -> "Ⅱ Pause audio"; else -> "▶ Play audio" }, fontSize = 11.sp)
    }
}

private fun formatDuration(seconds: Long): String = "%02d:%02d".format(seconds / 60, seconds % 60)

// -------------------------------------------------------------
// ABOUT & CREDITS DIALOG
// -------------------------------------------------------------
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
                Text("P&B MGT Sys is designed to run locally, with zero server costs, built on the shoulders of these open-source projects:", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            row.forEach { credit ->
                Card(
                    modifier = Modifier.weight(1f).clickable(enabled = openSource && credit.url != null) { credit.url?.let { openLink?.invoke(it) } },
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(credit.name, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            if (openSource && credit.url != null) Text("↗", color = Muted, fontSize = 10.sp)
                        }
                        Text(credit.author, color = BrandBlue, fontFamily = FontFamily.Monospace, fontSize = 9.sp, modifier = Modifier.padding(vertical = 2.dp))
                        Text(credit.detail, color = Muted, fontSize = 10.sp)
                    }
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}
