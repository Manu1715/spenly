package com.example.spenly.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import com.example.spenly.data.LocalTransactionRepository
import com.example.spenly.data.Transaction
import com.example.spenly.data.SampleData

// --- Color Palette ---
private val DarkBackground = Color(0xFF040607)
private val CardDark = Color(0xFF151619)
private val Muted = Color(0xFF6B6B6F)
private val FieldBackground = Color(0xFF222326)
private val PanelSurface = Color(0xFF0E0F10)
private val Green = Color(0xFF4CAF50)
private val Red = Color(0xFFFF5252)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val repository = LocalTransactionRepository.current
    val scope = rememberCoroutineScope()
    var selectedType by remember { mutableStateOf(TransactionType.Expense) }
    var category by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payee by remember { mutableStateOf<String?>(null) }

    // State for the calendar
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showPayeeDialog by remember { mutableStateOf(false) }

    // Receipt state (single image URI)
    var receiptUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // uri is null if user cancels
        receiptUri = uri
    }

    // --- Main Bottom Sheet UI ---
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetDefaults.DragHandle()
                // Top bar inside the sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Cancel",
                        color = Color.White,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                    Text(
                        "Add Transaction",
                        color = Color(0xFF9EC7E3),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Save",
                        color = Color.White,
                        modifier = Modifier.clickable {
                            focusManager.clearFocus(true)
                            if (category != null && amount.isNotBlank()) {
                                try {
                                    val amountValue = amount.toDouble()
                                    if (amountValue > 0) {
                                        val transaction = Transaction(
                                            id = 0, // Room will auto-generate
                                            title = category ?: "Transaction",
                                            category = category ?: "",
                                            amount = amountValue,
                                            date = selectedDate, // Use the selected date
                                            isIncome = selectedType == TransactionType.Income,
                                            note = note,
                                            payee = payee,
                                            receiptUri = receiptUri?.toString()
                                        )
                                        scope.launch {
                                            repository.addTransaction(transaction)
                                        }
                                        onSave()
                                    }
                                } catch (e: NumberFormatException) {
                                    // Invalid amount
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        // --- Scrollable Content ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = "My Account",
                    color = Color(0xFF2AA6D6),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            // Type segmented control
            item {
                FormRow(title = "Type") {
                    SegmentedTypeControl(
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it }
                    )
                }
            }

            // Category
            item {
                FormRow(title = "Category") {
                    SelectableField(
                        text = category ?: "Select Category",
                        onClick = { showCategoryDialog = true }
                    )
                }
            }

            // Amount
            item {
                FormRow(title = "Amount") {
                    AmountTextField(
                        value = amount,
                        onValueChange = { input ->
                            val sanitized = input.replace(Regex("[^0-9.]"), "")
                            val parts = sanitized.split('.')
                            amount = when {
                                parts.size <= 1 -> sanitized
                                parts.size == 2 -> parts[0] + "." + parts[1].take(2)
                                else -> parts[0] + "." + parts[1]
                            }
                        }
                    )
                }
            }

            // Date Picker
            item {
                FormRow(title = "Date") {
                    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    SelectableField(
                        text = selectedDate.format(formatter),
                        onClick = { showDatePicker = true },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select Date",
                                tint = Color(0xFF7D7D80)
                            )
                        }
                    )
                }
            }

            // Note
            item {
                FormRow(title = "Note") {
                    NoteTextField(
                        value = note,
                        onValueChange = { note = it }
                    )
                }
            }

            // Payee
            item {
                FormRow(title = "Payee") {
                    SelectableField(
                        text = payee ?: "Who did you pay?",
                        onClick = { showPayeeDialog = true }
                    )
                }
            }

            // Receipt
            item {
                FormRow(title = "Receipt") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FieldBackground)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (receiptUri != null) "Receipt selected" else "Add receipt",
                            color = Color(0xFFBFBFC1)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = Color(0xFF7D7D80)
                        )
                    }
                }
            }
        }
    }

    // --- Dialogs ---
    if (showCategoryDialog) {
        CategoryDialog(
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = {
                category = it
                showCategoryDialog = false
            }
        )
    }

    if (showPayeeDialog) {
        PayeeDialog(
            onDismiss = { showPayeeDialog = false },
            onPayeeSelected = {
                payee = it
                showPayeeDialog = false
            }
        )
    }

    // --- Date Picker (fixed with UTC) ---
    if (showDatePicker) {
        val utcZone = ZoneId.of("UTC")

        // "Today" at midnight in UTC, to match DatePicker's utcTimeMillis
        val todayUtcMillis = remember {
            LocalDate.now(utcZone)
                .atStartOfDay(utcZone)
                .toInstant()
                .toEpochMilli()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(utcZone)
                .toInstant()
                .toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= todayUtcMillis

                override fun isSelectableYear(year: Int): Boolean =
                    year <= LocalDate.now(utcZone).year
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(utcZone)
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    fun closeAndNavigate(action: () -> Unit) {
        if (hasNavigated) return
        hasNavigated = true
        scope.launch {
            sheetState.hide()
            action()
        }
    }

    AddTransactionSheet(
        sheetState = sheetState,
        onDismiss = { closeAndNavigate(onCancel) },
        onSave = { closeAndNavigate(onSave) }
    )
}

// --- Reusable Components for the Form ---

@Composable
fun FormRow(title: String, content: @Composable () -> Unit) {
    Column {
        Text(text = title, color = Muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun SegmentedTypeControl(selectedType: TransactionType, onTypeSelected: (TransactionType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(CardDark),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val types = listOf(TransactionType.Expense, TransactionType.Income)
        types.forEach { type ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (selectedType == type)
                            if (type == TransactionType.Expense) AccentRed else AccentGreen
                        else Color.Transparent
                    )
                    .clickable { onTypeSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.name,
                    color = if (selectedType == type) Color.White else Color(0xFFCCCCCC),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SelectableField(
    text: String,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FieldBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = Color(0xFFBFBFC1), modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color(0xFF7D7D80)
        )
    }
}

@Composable
fun AmountTextField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("0.00", color = Color(0xFF8C8C90)) },
        leadingIcon = { Text("₹", color = Color(0xFF9A9A9B), fontSize = 20.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = AccentRed,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun NoteTextField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Add a note", color = Color(0xFF8C8C90)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = AccentRed,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category", color = Color.White, fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                // Unified Search/Add Field
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search or type to add", color = Color(0xFF8C8C90)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentRed,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(10.dp))

                // Combined List
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    // 1. Filtered Categories
                    val filtered = SampleData.categories.filter {
                        it.contains(query, ignoreCase = true)
                    }

                    // 2. Option to add custom entry if it doesn't exist in filtered list
                    // and user has typed something
                    if (query.isNotBlank() && filtered.none { it.equals(query, ignoreCase = true) }) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCategorySelected(query.trim()) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF2AA6D6))
                                Spacer(Modifier.width(8.dp))
                                Text("Add \"$query\"", color = Color(0xFF2AA6D6), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f))
                        }
                    }

                    items(filtered) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategorySelected(item) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item, color = Color.White, fontSize = 16.sp)
                        }
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f))
                    }
                }
            }
        },
        confirmButton = {}, // No confirm needed as clicking an item selects it
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF9EC7E3))
            ) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = PanelSurface,
    )
}

@Composable
fun PayeeDialog(
    onDismiss: () -> Unit,
    onPayeeSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Payee", color = Color.White, fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                // Unified Search/Add Field
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search or type to add", color = Color(0xFF8C8C90)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentRed,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.height(10.dp))

                // Combined List
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    val filtered = SampleData.contacts.filter {
                        it.contains(query, ignoreCase = true)
                    }

                    // Option to add custom payee if not in list
                    if (query.isNotBlank() && filtered.none { it.equals(query, ignoreCase = true) }) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPayeeSelected(query.trim()) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF2AA6D6))
                                Spacer(Modifier.width(8.dp))
                                Text("Add \"$query\"", color = Color(0xFF2AA6D6), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f))
                        }
                    }

                    items(filtered) { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPayeeSelected(name) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = Color.White, fontSize = 16.sp)
                        }
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f))
                    }
                }
            }
        },
        confirmButton = {}, // No explicit done button needed as selection closes dialog
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF9EC7E3))
            ) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = PanelSurface,
    )
}

enum class TransactionType { Expense, Income }

// --- Previewing the Bottom Sheet ---

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewAddTransactionSheet() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Preview requires proper repository setup - skipped for now
    // Note: Preview needs a proper repository instance with database
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)), // Dim background for preview
        contentAlignment = Alignment.BottomCenter
    ) {
        // Preview disabled - requires database setup
        Text("Preview requires database", color = Color.White)
    }
}
