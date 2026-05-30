package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.EmailEntity
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Pure dark background
                ) {
                    BharatMailAppScreen()
                }
            }
        }
    }
}

// Professional Polish Theme Color Palette
val BharatDarkBackground = Color(0xFF121212)
val BharatSurface = Color(0xFF1E1E1E)
val BharatSurfaceLight = Color(0xFF2C2C2C)
val BharatPrimary = Color(0xFF6366F1)       // Premium Indigo-500
val BharatSecondary = Color(0xFFF59E0B)     // Elegant Amber-500 Gold
val BharatCardBorder = Color(0x1BFFFFFF)     // white/10 thin subtle border layout
val BharatTextPrimary = Color(0xFFF3F4F6)    // Cool Gray 100
val BharatTextSecondary = Color(0xFF9CA3AF)  // Cool Gray 400
val BharatAccentGreen = Color(0xFF10B981)    // Emerald active green
val BharatAccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BharatMailAppScreen(viewModel: MailViewModel = viewModel()) {
    val context = LocalContext.current
    
    // Core Email States
    val emails by viewModel.filteredEmails.collectAsStateWithLifecycle()
    val activeAccount by viewModel.currentAccount.collectAsStateWithLifecycle()
    val activeFolder by viewModel.selectedFolder.collectAsStateWithLifecycle()
    val activeLabel by viewModel.selectedLabel.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val aiResponseStatus by viewModel.aiResponseStatus.collectAsStateWithLifecycle()

    // Navigation state from bottom bar (inbox / meet / chat)
    var currentBottomTab by remember { mutableStateOf("inbox") }

    // UI overlays
    var selectedEmail by remember { mutableStateOf<EmailEntity?>(null) }
    var showComposeView by remember { mutableStateOf(false) }
    var composeToField by remember { mutableStateOf("") }
    var composeSubjectField by remember { mutableStateOf("") }
    var composeBodyField by remember { mutableStateOf("") }
    var composeLabelField by remember { mutableStateOf("General") }

    var showProfileDropdown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        floatingActionButton = {
            // Compose FAB only in Inbox tab and when no card is selected
            if (currentBottomTab == "inbox" && !showComposeView && selectedEmail == null) {
                FloatingActionButton(
                    onClick = {
                        composeToField = ""
                        composeSubjectField = ""
                        composeBodyField = ""
                        composeLabelField = "General"
                        showComposeView = true
                    },
                    containerColor = BharatSecondary,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "लिखें", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "लिखें (Compose)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = BharatDarkBackground,
        bottomBar = {
            // High-fidelity swadeshi bottom navigation bar matching Professional Polish
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(BharatSurface)
                    .border(BorderStroke(1.dp, Color(0x14FFFFFF)), RectangleShape)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Inbox Tab Link
                val isInbox = currentBottomTab == "inbox"
                Column(
                    modifier = Modifier
                        .clickable { currentBottomTab = "inbox" }
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isInbox) BharatPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Inbox",
                            tint = if (isInbox) BharatPrimary else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Inbox",
                        color = if (isInbox) BharatPrimary else BharatTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Meet Tab Link
                val isMeet = currentBottomTab == "meet"
                Column(
                    modifier = Modifier
                        .clickable { currentBottomTab = "meet" }
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isMeet) BharatPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share, // Using Share icon as Meet session visual
                            contentDescription = "Meet",
                            tint = if (isMeet) BharatPrimary else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Meet",
                        color = if (isMeet) BharatPrimary else BharatTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Chat Tab Link
                val isChat = currentBottomTab == "chat"
                Column(
                    modifier = Modifier
                        .clickable { currentBottomTab = "chat" }
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isChat) BharatPrimary.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face, // Using Face icon as Chat visual
                            contentDescription = "Chat",
                            tint = if (isChat) BharatPrimary else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chat",
                        color = if (isChat) BharatPrimary else BharatTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (solid background matching #1E1E1E from the theme html)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(BharatSurface)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = BharatSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "भारत-Mail",
                            color = BharatSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    // Account Profile Avatar
                    Box {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BharatPrimary)
                                .border(2.dp, BharatSurfaceLight, CircleShape)
                                .clickable { showProfileDropdown = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeAccount.firstOrNull()?.uppercase() ?: "R",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        DropdownMenu(
                            expanded = showProfileDropdown,
                            onDismissRequest = { showProfileDropdown = false },
                            modifier = Modifier
                                .background(BharatSurface)
                                .border(1.dp, Color(0xFF2C2C2C), RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("raoboss@bharatmail.in", color = BharatTextPrimary, fontWeight = FontWeight.Bold)
                                        Text("स्वदेशी मेल (Indigenous Digital India)", color = BharatSecondary, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    viewModel.setAccount("raoboss@bharatmail.in")
                                    showProfileDropdown = false
                                    Toast.makeText(context, "स्वदेशी अकाउंट सक्रिय", Toast.LENGTH_SHORT).show()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "चयनित",
                                        tint = if (activeAccount == "raoboss@bharatmail.in") BharatSecondary else Color.Transparent
                                    )
                                }
                            )
                            HorizontalDivider(color = Color(0x1BFFFFFF))
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("raoboss.ff.gaming@gmail.com", color = BharatTextPrimary, fontWeight = FontWeight.Bold)
                                        Text("Google Gmail", color = BharatTextSecondary, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    viewModel.setAccount("raoboss.ff.gaming@gmail.com")
                                    showProfileDropdown = false
                                    Toast.makeText(context, "Gmail अकाउंट सक्रिय", Toast.LENGTH_SHORT).show()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "चयनित",
                                        tint = if (activeAccount == "raoboss.ff.gaming@gmail.com") BharatSecondary else Color.Transparent
                                    )
                                }
                            )
                            HorizontalDivider(color = Color(0x1bFFFFFF))
                            DropdownMenuItem(
                                text = { Text("डेटा डिलीट और रिसेट", color = Color.Red, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    viewModel.restoreMockData()
                                    showProfileDropdown = false
                                    Toast.makeText(context, "इनबॉक्स को रिसेट किया गया", Toast.LENGTH_SHORT).show()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "रिसेट", tint = Color.Red)
                                }
                            )
                        }
                    }
                }

                // Render dynamic view depending on active tab selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentBottomTab) {
                        "inbox" -> InboxTabView(
                            emails = emails,
                            activeAccount = activeAccount,
                            activeFolder = activeFolder,
                            activeLabel = activeLabel,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onFolderSelected = { viewModel.setFolder(it) },
                            onLabelSelected = { viewModel.setLabel(it) },
                            onItemClick = { email ->
                                selectedEmail = email
                                if (!email.isRead) {
                                    viewModel.toggleRead(email)
                                }
                            },
                            onStarToggle = { viewModel.toggleStarred(it) },
                            onDelete = { email ->
                                if (email.folder == "trash") {
                                    viewModel.moveEmailToFolder(email, "trash_perm_delete")
                                    Toast.makeText(context, "स्थायी रूप से हटा दिया गया", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.moveEmailToFolder(email, "trash")
                                    Toast.makeText(context, "कचरा (Trash) में भेजा गया", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRestore = { viewModel.restoreMockData() }
                        )
                        "meet" -> MeetTabView()
                        "chat" -> ChatTabView()
                    }
                }
            }

            // Expanded Email Detail Overlay Dialog
            selectedEmail?.let { email ->
                MailDetailOverlay(
                    email = email,
                    onClose = { selectedEmail = null },
                    onDelete = {
                        viewModel.moveEmailToFolder(email, "trash")
                        selectedEmail = null
                        Toast.makeText(context, "कचरा (Trash) में भेजा गया", Toast.LENGTH_SHORT).show()
                    },
                    onStarToggle = { viewModel.toggleStarred(email) },
                    onSmartReply = { replyDraftBody ->
                        composeToField = email.senderAddress
                        composeSubjectField = "Re: ${email.subject}"
                        composeBodyField = replyDraftBody
                        composeLabelField = "AI Drafted"
                        showComposeView = true
                        selectedEmail = null
                    },
                    viewModel = viewModel
                )
            }

            // Compose Screen Overlay Dialog
            if (showComposeView) {
                ComposeEmailOverlay(
                    senderAccount = activeAccount,
                    initialTo = composeToField,
                    initialSubject = composeSubjectField,
                    initialBody = composeBodyField,
                    initialLabel = composeLabelField,
                    onClose = { showComposeView = false },
                    onSend = { to, subject, body, label ->
                        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                            Toast.makeText(context, "कृपया सारे फ़ील्ड भरें", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.sendEmail(to, subject, body, label)
                            showComposeView = false
                            Toast.makeText(context, "ईमेल सफलतापूर्वक भेजा गया। जय हिन्द! 🇮🇳", Toast.LENGTH_LONG).show()
                        }
                    },
                    viewModel = viewModel,
                    aiResponseStatus = aiResponseStatus
                )
            }
        }
    }
}

@Composable
fun InboxTabView(
    emails: List<EmailEntity>,
    activeAccount: String,
    activeFolder: String,
    activeLabel: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFolderSelected: (String) -> Unit,
    onLabelSelected: (String) -> Unit,
    onItemClick: (EmailEntity) -> Unit,
    onStarToggle: (EmailEntity) -> Unit,
    onDelete: (EmailEntity) -> Unit,
    onRestore: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar in rounded container bg-[#2C2C2C] matching HTML style precisely
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(BharatSurfaceLight, RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "खोजें",
                tint = BharatTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "ईमेल सर्च करें (Search emails)...",
                        color = BharatTextSecondary,
                        fontSize = 14.sp
                    )
                }
                val keyboardController = LocalSoftwareKeyboardController.current
                BasicTextFieldHelper(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    textColor = BharatTextPrimary,
                    fontSize = 14f,
                    maxLines = 1,
                    onSearch = { keyboardController?.hide() }
                )
            }
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChanged("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "साफ करें",
                        tint = BharatTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Active Account Banner Label (Indiginous check) using standard Info icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Active Owner",
                tint = BharatTextSecondary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "अभी एक्टिव है: $activeAccount".uppercase(),
                color = BharatTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            if (activeAccount.endsWith("@bharatmail.in")) {
                Text(
                    text = "🇮🇳 स्वदेशी",
                    color = BharatSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(BharatPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Folder scroll row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val folders = listOf(
                Triple("inbox", "इनबॉक्स (Inbox)", Icons.Default.Email),
                Triple("sent", "भेजे गए (Sent)", Icons.Default.Send),
                Triple("starred", "तारांकित (Starred)", Icons.Default.Star),
                Triple("trash", "कचरा (Trash)", Icons.Default.Delete)
            )
            items(folders) { (folderKey, folderTitle, icon) ->
                val isSelected = activeFolder == folderKey
                Button(
                    onClick = { onFolderSelected(folderKey) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) BharatPrimary else BharatSurface,
                        contentColor = if (isSelected) Color.White else BharatTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, BharatCardBorder),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(imageVector = icon, contentDescription = folderTitle, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = folderTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Horizontal Category labels
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val labels = listOf("All", "General", "Gaming", "Security")
            items(labels) { label ->
                val isSelected = activeLabel == label
                val labelHindi = when (label) {
                    "All" -> "सभी (All)"
                    "General" -> "सामान्य (General)"
                    "Gaming" -> "गेमिंग (Gaming)"
                    "Security" -> "सुरक्षा (Security)"
                    else -> label
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) BharatSecondary.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) BharatSecondary else Color(0x33FFFFFF),
                            RoundedCornerShape(50)
                        )
                        .clickable { onLabelSelected(label) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = labelHindi,
                        color = if (isSelected) BharatSecondary else BharatTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main email items lists
        if (emails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Inbox Empty",
                        tint = BharatTextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "कोई ईमेल नहीं मिला (No Mail)",
                        color = BharatTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "आपका स्वदेशी इनबॉक्स पूरी तरह सुरक्षित और साफ है।",
                        color = BharatTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        style = LocalTextStyle.current.copy(lineHeight = 16.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRestore,
                        colors = ButtonDefaults.buttonColors(containerColor = BharatPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("सैंपल मेल लोड करें (Load Dummy Emails)", fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(emails) { email ->
                    EmailItemRow(
                        email = email,
                        onClick = { onItemClick(email) },
                        onStarToggle = { onStarToggle(email) },
                        onDelete = { onDelete(email) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmailItemRow(
    email: EmailEntity,
    onClick: () -> Unit,
    onStarToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(email.timestamp))
    val avatarColor = when {
        email.sender.contains("BharatMail", ignoreCase = true) -> BharatPrimary
        email.sender.contains("Free Fire", ignoreCase = true) -> BharatSecondary
        email.sender.contains("Google", ignoreCase = true) -> Color(0xFFEA4335)
        else -> Color(0xFF4B5563)
    }
    val isUnread = !email.isRead

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) Color(0xFF1E1E1E) else Color(0xFF16151B)
        ),
        border = BorderStroke(
            1.dp,
            if (isUnread) BharatPrimary.copy(alpha = 0.5f) else Color(0x14FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = email.sender.firstOrNull()?.uppercase() ?: "B",
                        color = if (avatarColor == BharatSecondary) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BharatAccentGreen)
                            .border(2.dp, Color(0xFF16151B), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = email.sender,
                        color = Color.White,
                        fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formattedTime,
                        color = BharatTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email.subject,
                    color = if (isUnread) BharatSecondary else Color.White,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email.snippet,
                    color = BharatTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTextStyle.current.copy(lineHeight = 15.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val badgeColor = when (email.label) {
                        "Gaming" -> BharatSecondary
                        "Security" -> Color(0xFFE57373)
                        else -> BharatPrimary
                    }
                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = email.label,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "डिलीट",
                                tint = BharatTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onStarToggle,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "स्टार",
                                tint = if (email.isStarred) BharatSecondary else BharatTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// MEET MODULE TAB VIEW (Bharat-Meet) using standard icons
@Composable
fun MeetTabView() {
    val context = LocalContext.current
    var showActiveRoomCode by remember { mutableStateOf<String?>(null) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var inputRoomCode by remember { mutableStateOf("") }
    
    // Simulated active live rooms
    val activeSchedules = remember {
        listOf(
            "फ्री फायर इंडिया रणनीति सत्र (FFI Gamer Meet)" to "12:30 PM",
            "स्वदेशी डिजिटल डेटा संप्रभुता सम्मिट (Data Security)" to "03:00 PM",
            "भारत-Mail उत्पाद विकास और रोडमैप बैठक" to "कल दोपहर"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Hero Intro Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BharatSurface),
            border = BorderStroke(1.dp, BharatCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BharatSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Videocam", tint = BharatSecondary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "भारत-संवाद (Bharat-Meet)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "पूर्णतः सुरक्षित, स्वदेशी वीडियो मीटिंग समाधान। बिना किसी डेटा शेयरिंग के एचडी गुणवत्ता में असीमित वीडियो कॉलिंग का आनंद लें।",
                    color = BharatTextSecondary,
                    fontSize = 12.sp,
                    style = LocalTextStyle.current.copy(lineHeight = 16.sp)
                )
            }
        }

        // Action grid buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Button 1: Host Room
            Button(
                onClick = {
                    val randomCode = "BM-RAO-" + (1000..9999).random() + "-FF"
                    showActiveRoomCode = randomCode
                    Toast.makeText(context, "स्वदेशी मीटिंग रूम तैयार है!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BharatPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Host")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "नया सम्मेलन (Host)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Button 2: Join with code
            Button(
                onClick = { showJoinDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = BharatSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BharatSecondary),
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Code", tint = BharatSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "कोड डालें (Join)",
                        color = BharatSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0x1BFFFFFF))
        Spacer(modifier = Modifier.height(16.dp))

        // Schedule Meetings List title
        Text(
            text = "आगामी स्वदेशी बैठकें (Scheduled Sessions)",
            color = BharatSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activeSchedules) { (title, time) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1E)),
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = title,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "समय (Time): $time",
                                color = BharatTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(
                            onClick = {
                                showActiveRoomCode = "BM-SCHED-" + (1000..9999).random()
                                Toast.makeText(context, "सम्मेलन प्रारंभ किया जा रहा है...", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "चलाएं",
                                tint = BharatAccentGreen
                            )
                        }
                    }
                }
            }
        }
    }

    // Join with Code Interactive Dialogue
    if (showJoinDialog) {
        Dialog(onDismissRequest = { showJoinDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BharatSurface),
                border = BorderStroke(1.dp, BharatCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "बैठक में शामिल हों",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputRoomCode,
                        onValueChange = { inputRoomCode = it },
                        placeholder = { Text("उदा: BM-RAO-7721-FF", color = BharatTextSecondary, fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = BharatTextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BharatSecondary,
                            unfocusedIndicatorColor = BharatCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showJoinDialog = false }) {
                            Text("रद्द", color = BharatTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (inputRoomCode.trim().isNotEmpty()) {
                                    showActiveRoomCode = inputRoomCode
                                    showJoinDialog = false
                                } else {
                                    Toast.makeText(context, "कृपया वैध कोड डालें", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BharatSecondary)
                        ) {
                            Text("शामिल हों", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Active Simulated Video Conference Session Overlay Screen using text widgets inside styled buttons
    showActiveRoomCode?.let { code ->
        var isCameraOn by remember { mutableStateOf(true) }
        var isMicrophoneOn by remember { mutableStateOf(true) }
        var isScreenSharing by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0E13)),
                border = BorderStroke(1.dp, BharatSecondary)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    // Header inside active video layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "स्वदेशी सुरक्षित संवाद सेतु",
                            color = BharatSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(BharatAccentGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("लाइव LIVE", color = BharatAccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "मीटिंग कोड: $code",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Video Feed Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1E24))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCameraOn) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Profile illustration representing video stream
                                Box(
                                    modifier = Modifier.size(80.dp).clip(CircleShape).background(BharatPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("राव Boss", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "आपका वीडियो स्ट्रीम सक्रिय है",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (isScreenSharing) {
                                    Text(
                                        "(स्क्रीन साझाकरण सक्रिय)",
                                        color = BharatSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Camera Off",
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("कैमरा अक्षम कर दिया गया है", color = BharatTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Video Control Panel buttons using standard core icons with clear labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Toggle Microphone (Using standard Check/Close icon with text helper)
                        IconButton(
                            onClick = { isMicrophoneOn = !isMicrophoneOn },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isMicrophoneOn) Color(0xFF2C2C35) else BharatAccentRed)
                        ) {
                            Text(
                                text = if (isMicrophoneOn) "Mic" else "Muted",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Toggle Camera
                        IconButton(
                            onClick = { isCameraOn = !isCameraOn },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isCameraOn) Color(0xFF2C2C35) else BharatAccentRed)
                        ) {
                            Text(
                                text = if (isCameraOn) "Cam" else "Off",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Toggle Screen Share
                        IconButton(
                            onClick = { isScreenSharing = !isScreenSharing },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isScreenSharing) BharatSecondary else Color(0xFF2C2C35))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Screen",
                                tint = if (isScreenSharing) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Disconnect Button
                        IconButton(
                            onClick = {
                                showActiveRoomCode = null
                                Toast.makeText(context, "सम्मेलन संपन्न हुआ। जय हिन्द!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BharatAccentRed)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "End Call",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// CHAT MODULE TAB VIEW (Bharat-Chat) using Face icon as base Chat identity
@Composable
fun ChatTabView() {
    val context = LocalContext.current
    var inputChatMessage by remember { mutableStateOf("") }
    
    // Chat entities list
    val chatRooms = remember {
        mutableStateListOf(
            Triple("फ्री फायर स्वदेशी स्क्वाड 🎮", "राव बॉस, पंजीकरण तो हो गया, कल मैच है!", "09:45 AM"),
            Triple("BharatMail AI असिस्टेंट 🤖", "आपकी सुरक्षा हमारी परम संप्रभुता प्राथमिकता है।", "कल दोपहर"),
            Triple("Garena Rewards Core", "Unclaimed Golden AK skins expires in 2 days.", "Yesterday")
        )
    }

    var selectedChatName by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Hero description card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BharatSurface),
            border = BorderStroke(1.dp, BharatCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BharatSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "Chat Logo", tint = BharatSecondary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "भारत-गपशप (Bharat-Chat)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "एन्ड-टू-एन्ड एन्क्रिप्टेड स्वदेशी मैसेंजर। अपने गेमिंग साथियों और अधिकारियों के साथ तीव्र संदेशों का त्वरित आदान-प्रदान करें।",
                    color = BharatTextSecondary,
                    fontSize = 12.sp,
                    style = LocalTextStyle.current.copy(lineHeight = 16.sp)
                )
            }
        }

        HorizontalDivider(color = Color(0x1BFFFFFF))
        Spacer(modifier = Modifier.height(16.dp))

        // Chat list view
        Text(
            text = "सक्रिय संवाद सूत्र (Conversation Threads)",
            color = BharatSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatRooms.size) { index ->
                val (title, snippet, time) = chatRooms[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedChatName = title },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    border = BorderStroke(1.dp, Color(0x1AFFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chat group profile letter avatar
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BharatPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title.firstOrNull()?.uppercase() ?: "G",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(BharatAccentGreen)
                                    .border(1.5.dp, Color(0xFF1E1E24), CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = time,
                                    color = BharatTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = snippet,
                                color = BharatTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    // Active Chat Dialog Details panel (Instant messaging interactivity)
    selectedChatName?.let { chatTitle ->
        val conversationHistory = remember {
            mutableStateListOf(
                "सादर जय हिन्द राव बॉस, सत्र 2026 भारत कप के लिए आपकी योजना क्या है?" to false,
                "कल दोपहर 12:30 बजे मैच है, हम पूर्णतः तैयार हैं!" to true,
                "अद्भुत! सर्वर कनेक्टिविटी पूर्णतः स्वदेशी है, ऑल द बेस्ट!" to false
            )
        }

        Dialog(onDismissRequest = { selectedChatName = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BharatSurface),
                border = BorderStroke(1.dp, BharatSecondary)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(14.dp)
                ) {
                    // Chat header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { selectedChatName = null }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "वापस", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = chatTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BharatAccentGreen)
                        )
                    }

                    HorizontalDivider(color = Color(0x1BFFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Conversation texts scroll area
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conversationHistory.size) { idx ->
                            val (msg, isMe) = conversationHistory[idx]
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isMe) 12.dp else 0.dp,
                                                bottomEnd = if (isMe) 0.dp else 12.dp
                                            )
                                        )
                                        .background(if (isMe) BharatPrimary else BharatSurfaceLight)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        style = LocalTextStyle.current.copy(lineHeight = 16.sp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Send bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputChatMessage,
                            onValueChange = { inputChatMessage = it },
                            placeholder = { Text("नया सन्देश (New message)...", color = BharatTextSecondary, fontSize = 11.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = BharatTextPrimary,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = BharatSecondary,
                                unfocusedIndicatorColor = BharatCardBorder
                            ),
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputChatMessage.trim().isNotEmpty()) {
                                    val userText = inputChatMessage
                                    conversationHistory.add(userText to true)
                                    inputChatMessage = ""
                                    Toast.makeText(context, "सन्देश भेजा गया", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BharatSecondary)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "भेजें", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// Mail Reader Panel (Large clean Overlay)
@Composable
fun MailDetailOverlay(
    email: EmailEntity,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onStarToggle: () -> Unit,
    onSmartReply: (String) -> Unit,
    viewModel: MailViewModel
) {
    val context = LocalContext.current
    var isTranslating by remember { mutableStateOf(false) }
    var translatedBody by remember { mutableStateOf<String?>(null) }
    var isGeneratingReply by remember { mutableStateOf(false) }

    val formattedDate = SimpleDateFormat("dd-MMM-yyyy, hh:mm a", Locale.getDefault()).format(Date(email.timestamp))

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .border(1.dp, BharatCardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BharatSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "बंद", tint = Color.White)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onStarToggle) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "तारांकित",
                                tint = if (email.isStarred) BharatSecondary else BharatTextSecondary
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "कचरा", tint = BharatTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Mail Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Subject lines
                    Text(
                        text = email.subject,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        style = LocalTextStyle.current.copy(lineHeight = 24.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sender and Recipient metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BharatPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = email.sender.firstOrNull()?.uppercase() ?: "S",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(text = email.sender, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "from: ${email.senderAddress}", color = BharatTextSecondary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "to: ${email.recipient}", color = BharatTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(start = 46.dp))
                    Text(text = "दिनांक: $formattedDate", color = BharatTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(start = 46.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0x1BFFFFFF))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Email message text rendering box
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                val bodyTextToShow = translatedBody ?: email.body
                                Text(
                                    text = bodyTextToShow,
                                    color = BharatTextPrimary,
                                    fontSize = 14.sp,
                                    style = LocalTextStyle.current.copy(lineHeight = 20.sp)
                                )
                                
                                if (translatedBody != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "(सुरक्षित AI अनुवादित पाठ)",
                                        color = BharatSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0x1BFFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                // AI Smart translation & Smart reply section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Real AI Translate to Hindi/English
                        Button(
                            onClick = {
                                if (translatedBody != null) {
                                    translatedBody = null
                                } else {
                                    isTranslating = true
                                    viewModel.generateWithAi(
                                        prompt = "कृपया इस ईमेल संदेश का अत्यंत शुद्ध हिंदी (या यदि पहले से हिंदी है, तो अंग्रेजी) में रूपांतरण / अनुवाद प्रस्तुत करें। संदेश इस प्रकार है:\n\n${email.body}",
                                        receiver = email.senderAddress,
                                        subject = email.subject,
                                        onCompleted = { response ->
                                            translatedBody = response
                                            isTranslating = false
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (translatedBody != null) BharatSecondary else BharatPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "अनुवाद",
                                modifier = Modifier.size(16.dp),
                                tint = if (translatedBody != null) Color.Black else Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTranslating) "रूपांतरण..." else if (translatedBody != null) "मूल सन्देश" else "AI हिंदी/English",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (translatedBody != null) Color.Black else Color.White
                            )
                        }

                        // AI Smart Quick Reply Draft
                        Button(
                            onClick = {
                                isGeneratingReply = true
                                viewModel.generateWithAi(
                                    prompt = "कृपया इस ईमेल संदेश का एक सुंदर, संक्रांत और भारतीय मूल्यों के अनुरूप सकारात्मक स्मार्ट रिप्लाई सन्देश लिखे। मूल ईमेल:\nSender: ${email.sender}\nSubject: ${email.subject}\nBody:\n${email.body}",
                                    receiver = email.senderAddress,
                                    subject = email.subject,
                                    onCompleted = { response ->
                                        isGeneratingReply = false
                                        onSmartReply(response)
                                        Toast.makeText(context, "AI स्मार्ट रिप्लाई ड्राफ्ट तैयार!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BharatSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BharatSecondary)
                        ) {
                            Text(
                                text = if (isGeneratingReply) "AI सोच रहा है..." else "AI स्मार्ट रिप्लाई (Reply)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BharatSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Full screen styled cover compose dialogue
@Composable
fun ComposeEmailOverlay(
    senderAccount: String,
    initialTo: String,
    initialSubject: String,
    initialBody: String,
    initialLabel: String,
    onClose: () -> Unit,
    onSend: (String, String, String, String) -> Unit,
    viewModel: MailViewModel,
    aiResponseStatus: String?
) {
    var to by remember { mutableStateOf(initialTo) }
    var subject by remember { mutableStateOf(initialSubject) }
    var body by remember { mutableStateOf(initialBody) }
    var label by remember { mutableStateOf(initialLabel) }

    var aiPromptInput by remember { mutableStateOf("") }
    var showAiAssistSheet by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(1.dp, BharatCardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BharatSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Action Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "वापस", tint = Color.White)
                    }
                    Text(
                        text = "नया मैसेज (Compose)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(
                        onClick = { onSend(to, subject, body, label) }
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "भेजें", tint = BharatSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Standard email details fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(text = "से: $senderAccount", color = BharatTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = to,
                        onValueChange = { to = it },
                        label = { Text("किसको (To):", color = BharatSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = BharatTextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BharatSecondary,
                            unfocusedIndicatorColor = BharatCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("विषय (Subject):", color = BharatSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = BharatTextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BharatSecondary,
                            unfocusedIndicatorColor = BharatCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Smart label tagging selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category Label:", color = BharatTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        val activeSelector = listOf("General", "Gaming", "Security")
                        for (lbl in activeSelector) {
                            val active = label == lbl
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (active) BharatSecondary else BharatSurfaceLight)
                                    .clickable { label = lbl }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = lbl,
                                    color = if (active) Color.Black else BharatTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Floating Assistant Quick Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BharatPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, BharatPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { showAiAssistSheet = !showAiAssistSheet }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Face, contentDescription = "AI", tint = BharatSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "स्वदेशी AI कंपोज़ सहायक (AI Compose Assist)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "1-टैप में ईमेल लिखे या अनुवादित करें।",
                                    color = BharatTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (showAiAssistSheet) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle",
                                tint = BharatSecondary
                            )
                        }
                    }

                    // Smart assist interface drawer inside composer
                    if (showAiAssistSheet) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .background(BharatSurfaceLight, RoundedCornerShape(12.dp))
                                .border(1.dp, BharatCardBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                "AI को बताएं आपको क्या लिखना है:",
                                color = BharatSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = aiPromptInput,
                                onValueChange = { aiPromptInput = it },
                                placeholder = {
                                    Text(
                                        "जैसे: 'फ्री फायर टूर्नामेंट सपोर्ट हेतु अनुरोध पत्र' या 'Google Sec. को स्पष्टीकरण दें'",
                                        color = BharatTextSecondary,
                                        fontSize = 11.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = BharatTextPrimary,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = BharatPrimary,
                                    unfocusedIndicatorColor = BharatCardBorder
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Action Button: Draft with AI
                                Button(
                                    onClick = {
                                        if (aiPromptInput.trim().isNotEmpty()) {
                                            viewModel.generateWithAi(
                                                prompt = aiPromptInput,
                                                receiver = to,
                                                subject = subject,
                                                onCompleted = { result ->
                                                    body = result
                                                    showAiAssistSheet = false
                                                    aiPromptInput = ""
                                                }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BharatPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "AI ड्राफ्ट करें",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Action Button: Translate to Pure Hindi
                                Button(
                                    onClick = {
                                        if (body.trim().isNotEmpty()) {
                                            viewModel.generateWithAi(
                                                prompt = "कृपया निम्नलिखित अंग्रेजी अथवा हिंग्लिश पाठ को शुद्ध, सुंदर हिंदी भाषा में रूपांतरित करें:\n\n$body",
                                                receiver = to,
                                                subject = subject,
                                                onCompleted = { result ->
                                                    body = result
                                                    showAiAssistSheet = false
                                                }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BharatSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, BharatSecondary),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "हिंदी में अनुवाद",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BharatSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email message body input field
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        placeholder = { Text("अपना संदेश यहाँ लिखें (Type your message here)...", color = BharatTextSecondary, fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = BharatTextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BharatPrimary,
                            unfocusedIndicatorColor = BharatCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // AI Response Status Overlay bar if working
                aiResponseStatus?.let { status ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(BharatSecondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, BharatSecondary, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = BharatSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = status, color = BharatSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// BasicTextField helper with standard typography configuration
@Composable
fun BasicTextFieldHelper(
    value: String,
    onValueChange: (String) -> Unit,
    textColor: Color,
    fontSize: Float = 14f,
    maxLines: Int = 1,
    onSearch: () -> Unit
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        maxLines = maxLines,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
