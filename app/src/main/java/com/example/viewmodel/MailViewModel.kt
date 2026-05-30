package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.EmailDao
import com.example.data.EmailEntity
import com.example.network.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailViewModel(application: Application) : AndroidViewModel(application) {
    private val emailDao: EmailDao = AppDatabase.getDatabase(application).emailDao()
    private val geminiClient = GeminiClient()

    val currentAccount = MutableStateFlow("raoboss@bharatmail.in")
    val selectedFolder = MutableStateFlow("inbox")
    val selectedLabel = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")

    val aiResponseStatus = MutableStateFlow<String?>(null)

    // Reactive flow combining active states to stream filtered e-mails
    private val _rawEmails = MutableStateFlow<List<EmailEntity>>(emptyList())
    
    val filteredEmails: StateFlow<List<EmailEntity>> = combine(
        _rawEmails,
        selectedFolder,
        selectedLabel,
        searchQuery
    ) { emails, folder, label, query ->
        emails.filter { email ->
            val matchFolder = email.folder.lowercase() == folder.lowercase()
            val matchLabel = label == "All" || email.label.lowercase() == label.lowercase()
            val matchQuery = query.isEmpty() || 
                    email.sender.contains(query, ignoreCase = true) ||
                    email.subject.contains(query, ignoreCase = true) ||
                    email.body.contains(query, ignoreCase = true)
            matchFolder && matchLabel && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe emails for the selected account
        viewModelScope.launch {
            currentAccount.collect { account ->
                seedInitialDataIfNeeded(account)
                emailDao.getEmailsForAccount(account).collect {
                    _rawEmails.value = it
                }
            }
        }
    }

    private suspend fun seedInitialDataIfNeeded(account: String) = withContext(Dispatchers.IO) {
        // Run seed check in background
        emailDao.getEmailsForAccount(account).collect { list ->
            if (list.isEmpty()) {
                val currentTime = System.currentTimeMillis()
                if (account == "raoboss@bharatmail.in") {
                    // Seed Swadeshi Account
                    val defaultEmails = listOf(
                        EmailEntity(
                            sender = "BharatMail Team (भारत-Mail)",
                            senderAddress = "support@bharatmail.in",
                            recipient = "raoboss@bharatmail.in",
                            subject = "Welcome to India's own Email! (भारत-Mail पर स्वागत है)",
                            snippet = "Thank you for joining the indigenous digital revolution...",
                            body = "नमस्ते भारत!\n\nराष्ट्र के अपने पहले सुरक्षित ईमेल नेटवर्क 'भारत-Mail' पर आपका स्वागत है। भारत-Mail पूरी तरह से स्वदेशी है। आपकी डेटा सुरक्षा और संप्रभुता हमारी सर्वोच्च प्राथमिकता है।\n\nविशेषताएं:\n1. पूर्ण स्वदेशी होस्टिंग (डेटा भारत में ही भंडारित रहता है)\n2. समृद्ध हिंदी-इंग्लिश बहुभाषी इंटरफ़ेस\n3. त्वरित AI सामग्री लेखन (Swadeshi AI-Compose Assist)\n\nडिजिटल इंडिया की इस क्रांति में हमारे साथ भागीदारी के लिए धन्यवाद।\n\nसादर,\nभारत-Mail टीम\nजय हिन्द! 🇮🇳",
                            timestamp = currentTime - 3600000 * 2, // 2 hr ago
                            isRead = false,
                            isStarred = true,
                            accountOwner = "raoboss@bharatmail.in",
                            folder = "inbox",
                            label = "General"
                        ),
                        EmailEntity(
                            sender = "Free Fire India (स्वदेशी लीग)",
                            senderAddress = "tournaments@freefire.in",
                            recipient = "raoboss@bharatmail.in",
                            subject = "Tournament Registration Successful! (पंजीकरण सफल)",
                            snippet = "Your team Rao Boss FF has been registered successfully.",
                            body = "जय हिन्द राव बॉस!\n\nसत्र-2026 स्वदेशी ई-स्पोर्ट्स फ्री फायर इंडिया टूर्नामेंट में आपकी टीम 'Rao Boss FF' का पंजीकरण सफलतापूर्वक स्वीकार कर लिया गया है।\n\nमैच का विवरण:\nतिथि: कल दोपहर\nसमय: 12:30 PM IST\nनियम: फेयर प्ले और स्वदेशी सर्वर का उपयोग अनिवार्य है।\n\nअपने हथियारों को तैयार रखें और विजय प्राप्त करें!\n\nधन्यवाद,\nफ्री फायर इंडिया कप कमिटी",
                            timestamp = currentTime - 3600000 * 5, // 5 hr ago
                            isRead = false,
                            isStarred = false,
                            accountOwner = "raoboss@bharatmail.in",
                            folder = "inbox",
                            label = "Gaming"
                        ),
                        EmailEntity(
                            sender = "Google Safety Admin",
                            senderAddress = "no-reply@accounts.google.com",
                            recipient = "raoboss@bharatmail.in",
                            subject = "New login detected on Android Terminal",
                            snippet = "We noticed a new login on your accounts portal...",
                            body = "Dear User,\n\nWe noticed a login to your account raoboss@bharatmail.in from a new Linux/Android development terminal.\n\nDetails:\nIP: 192.168.1.104 (Mumbai, India)\nTime: Today\n\nIf this was indeed you, no further action is necessary. If this wasn't you, please secure your login access immediately.\n\nBest regards,\nGoogle Accounts Protection Squad",
                            timestamp = currentTime - 3600000 * 12, // 12 hr ago
                            isRead = true,
                            isStarred = false,
                            accountOwner = "raoboss@bharatmail.in",
                            folder = "inbox",
                            label = "Security"
                        )
                    )
                    for (email in defaultEmails) {
                        emailDao.insertEmail(email)
                    }
                } else if (account == "raoboss.ff.gaming@gmail.com") {
                    // Seed Gmail account
                    val defaultEmails = listOf(
                        EmailEntity(
                            sender = "Free Fire Diamond Rewards",
                            senderAddress = "diamonds@garena.com",
                            recipient = "raoboss.ff.gaming@gmail.com",
                            subject = "Daily Claim rewards available!",
                            snippet = "Rao Boss, your 150 diamonds and airdrop skins are waiting...",
                            body = "What's up Gamer Rao Boss!\n\nYou have completed 5 games today and unlocked the Daily Gaming Airdrop crate!\n\nClaim rewards:\n- 150 In-game Diamonds\n- Special Golden Ak-47 Skin (7 Days)\n- 500 gold coins\n\nVerify and fetch your gifts inside the game inbox. Game on, Rao Boss!\n\nRegards,\nGarena Free Fire Community Team",
                            timestamp = currentTime - 3600000 * 1, // 1 hr ago
                            isRead = false,
                            isStarred = true,
                            accountOwner = "raoboss.ff.gaming@gmail.com",
                            folder = "inbox",
                            label = "Gaming"
                        ),
                        EmailEntity(
                            sender = "Google Payments India",
                            senderAddress = "payments-noreply@google.com",
                            recipient = "raoboss.ff.gaming@gmail.com",
                            subject = "Receipt for Google Play UPI Purchase: Rs 29.00",
                            snippet = "Your Google Play UPI transaction was completed successfully.",
                            body = "Dear Customer,\n\nThanks for purchasing with Google Play. Here are your transaction details:\n\nMerchant: Garena International Free Fire\nItem ordered: Weekly Special Airdrop Pack\nPayment Method: UPI (RaoBossPay)\nAmount debited: ₹29.00\nStatus: Success\n\nSave this receipt for your records.\n\nThank you,\nGoogle Payments India Ltd.",
                            timestamp = currentTime - 3600000 * 3, // 3 hr ago
                            isRead = true,
                            isStarred = false,
                            accountOwner = "raoboss.ff.gaming@gmail.com",
                            folder = "inbox",
                            label = "Security"
                        )
                    )
                    for (email in defaultEmails) {
                        emailDao.insertEmail(email)
                    }
                }
            }
        }
    }

    fun setAccount(account: String) {
        currentAccount.value = account
    }

    fun setFolder(folder: String) {
        selectedFolder.value = folder
    }

    fun setLabel(label: String) {
        selectedLabel.value = label
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleStarred(email: EmailEntity) {
        viewModelScope.launch {
            emailDao.updateEmail(email.copy(isStarred = !email.isStarred))
        }
    }

    fun toggleRead(email: EmailEntity) {
        viewModelScope.launch {
            emailDao.updateEmail(email.copy(isRead = !email.isRead))
        }
    }

    fun moveEmailToFolder(email: EmailEntity, folder: String) {
        viewModelScope.launch {
            emailDao.updateEmail(email.copy(folder = folder))
        }
    }

    fun sendEmail(recipient: String, subject: String, body: String, label: String = "General") {
        viewModelScope.launch {
            // Add to Database: sent folder
            val newEmail = EmailEntity(
                sender = if (currentAccount.value == "raoboss@bharatmail.in") "Rao Boss (स्वदेशी)" else "Rao Boss FF",
                senderAddress = currentAccount.value,
                recipient = recipient,
                subject = subject,
                snippet = if (body.length > 50) body.substring(0, 47) + "..." else body,
                body = body,
                isRead = true,
                accountOwner = currentAccount.value,
                folder = "sent",
                label = label
            )
            emailDao.insertEmail(newEmail)

            // Auto-Reply simulation if sent to dynamic address!
            simulateAutoReply(recipient, subject, body)
        }
    }

    private fun simulateAutoReply(recipient: String, sentSubject: String, sentBody: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Wait 2 seconds to simulate network delivery delay
                Thread.sleep(2000)
                
                val replySubject = "Re: $sentSubject"
                val replyBody = "नमस्ते राव बॉस!\n\nहम तक आपका सन्देश पहुँच गया है। भारत-Mail सर्वर ने आपके सन्देश:\n\"$sentBody\"\nको सुरक्षित रूप से प्रोसेस कर लिया है।\n\nहमारे कस्टमर केयर या सम्बंधित टीम द्वारा जल्द ही संपर्क किया जायेगा। डिजिटल इंडिया की विकास यात्रा का हिस्सा बनने के लिए धन्यवाद!\n\nजय हिन्द! 🇮🇳"
                
                val incomingEmail = EmailEntity(
                    sender = "BharatMail Auto-Responder",
                    senderAddress = "auto-reply@bharatmail.in",
                    recipient = currentAccount.value,
                    subject = replySubject,
                    snippet = "नमस्ते राव बॉस! हम तक आपका सन्देश पहुँच गया है...",
                    body = replyBody,
                    isRead = false,
                    accountOwner = currentAccount.value,
                    folder = "inbox",
                    label = "General"
                )
                emailDao.insertEmail(incomingEmail)
            }
        }
    }

    fun generateWithAi(prompt: String, receiver: String, subject: String, onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            aiResponseStatus.value = "AI ड्राफ्ट तैयार कर रहा है..."
            val systemInstruction = """
                You are BharatMail AI Assistant, writing an email on behalf of digital Indian user "Rao Boss".
                Maintain a formal or warm indigenous tone. If the prompt contains Hindi terms, write the email in beautifully mixed Hindi-English (Hinglish) or fluent Hindi. If purely English is desired, write professional English.
                Keep it short, direct, polite, and culturally respectful (e.g. including 'नमस्ते', 'सादर' or 'जय हिन्द!' at the end if applicable).
            """.trimIndent()

            val aiPrompt = """
                Writing email to: $receiver
                Subject: $subject
                User description / core instruction: $prompt
                
                Generate only the email message body. Do not include subject fields, just write the polished content details.
            """.trimIndent()

            try {
                val response = withContext(Dispatchers.IO) {
                    geminiClient.generateContent(aiPrompt, systemInstruction)
                }
                onCompleted(response)
                aiResponseStatus.value = null
            } catch (e: Exception) {
                aiResponseStatus.value = "त्रुटि: ${e.localizedMessage}"
            }
        }
    }

    fun restoreMockData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Clear and re-populate
                val account = currentAccount.value
                val existing = _rawEmails.value
                for (email in existing) {
                    emailDao.deleteEmail(email)
                }
                seedInitialDataIfNeeded(account)
            }
        }
    }
}
