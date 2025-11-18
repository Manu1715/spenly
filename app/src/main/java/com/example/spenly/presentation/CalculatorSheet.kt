    package com.example.spenly.presentation

    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Calculate
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.DpOffset
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.ui.window.PopupProperties

    // Theme colors matching app
    private val CalculatorBackground = Color(0xFF111113)
    private val CalculatorCard = Color(0xFF151619)
    private val CalculatorButtonDark = Color(0xFF222326)
    private val AccentCyan = Color(0xFF26C6DA)
    private val CalculatorText = Color.White
    private val CalculatorTextSecondary = Color(0xFFB0B0B0)

    /**
     * The main screen containing a TopAppBar with a calculator icon that toggles the dropdown.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreenWithCalculator() {
        var expanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Spenly") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        // The CalculatorDropdown is anchored to the IconButton.
                        CalculatorDropdown(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            // This is the anchor view (the icon button)
                            IconButton(onClick = { expanded = !expanded }) { // MODIFICATION: Toggle the dropdown
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Toggle Calculator",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Your main app content goes here
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Main Content Area")
            }
        }
    }

    @Composable
    fun CalculatorDropdown(
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        anchor: @Composable () -> Unit
    ) {
        var expression by remember { mutableStateOf("") }
        var result by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.wrapContentSize(Alignment.TopStart)
        ) {
            // Render the anchor (IconButton) - this should be clickable even when dropdown is open
            anchor()

            // Dropdown menu that appears when expanded
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    // Close the dropdown when dismissed (click outside, back press, etc.)
                    // Note: This won't be called when clicking the anchor itself
                    onExpandedChange(false)
                },
                modifier = Modifier
                    .width(320.dp)
                    .height(520.dp),
                offset = DpOffset(x = (-260).dp, y = 8.dp),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                containerColor = CalculatorBackground
            ) {
                CalculatorContent(
                    expression = expression,
                    result = result,
                    onExpressionChange = { expression = it },
                    onResultChange = { result = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
        }
    }

    @Composable
    fun CalculatorContent(
        expression: String,
        result: String,
        onExpressionChange: (String) -> Unit,
        onResultChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val onAction: (String) -> Unit = { action ->
            when (action) {
                "AC" -> {
                    onExpressionChange("")
                    onResultChange("")
                }
                "⌫" -> {
                    if (expression.isNotBlank()) {
                        onExpressionChange(expression.dropLast(1))
                        onResultChange("")
                    }
                }
                "%" -> {
                    try {
                        if (expression.isNotBlank()) {
                            val number = expression.toDoubleOrNull()
                            if (number != null) {
                                val percentResult = (number / 100).toString()
                                onResultChange(formatResult(percentResult))
                                onExpressionChange(percentResult)
                            }
                        }
                    } catch (e: Exception) {
                        onResultChange("Error")
                    }
                }
                "=" -> {
                    try {
                        val realExpression = expression.replace('×', '*').replace('÷', '/')
                        if (realExpression.isNotBlank()) {
                            val evalResult = net.objecthunter.exp4j.ExpressionBuilder(realExpression).build().evaluate()
                            val formattedResult = formatResult(evalResult.toString())
                            onResultChange(formattedResult)
                            onExpressionChange(expression) // Keep the original expression visible
                        }
                    } catch (e: Exception) {
                        onResultChange("Error")
                    }
                }
                else -> {
                    val isOperator = action in listOf("+", "-", "×", "÷")

                    // Case 1: A result is shown.
                    if (result.isNotBlank() && result != "Error") {
                        // User presses an operator to continue the calculation.
                        if (isOperator) {
                            onExpressionChange(result + action)
                            onResultChange("")
                        }
                        // User presses a number, starting a new calculation.
                        else {
                            onExpressionChange(action)
                            onResultChange("")
                        }
                    }
                    // Case 2: No result is shown, just continue building the expression.
                    else {
                        // Prevent adding multiple operators in a row
                        val lastChar = expression.lastOrNull()
                        if (isOperator && lastChar != null && lastChar in listOf('+', '-', '×', '÷', '.')) {
                            onExpressionChange(expression.dropLast(1) + action)
                        } else {
                            onExpressionChange(expression + action)
                        }
                    }
                }
            }
        }

        val buttonRows = listOf(
            listOf("AC", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        Column(
            modifier = modifier
                .background(Color.DarkGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CalculatorCard)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    color = CalculatorTextSecondary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = result.ifEmpty { "0" },
                    color = if (result == "Error") MaterialTheme.colorScheme.error else CalculatorText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { symbol ->
                            val weight = if (symbol == "0") 2f else 1f
                            CalculatorButton(
                                symbol = symbol,
                                modifier = Modifier
                                    .weight(weight)
                                    .height(60.dp),
                                onClick = { onAction(symbol) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CalculatorButton(
        symbol: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val (backgroundColor, textColor) = when {
            symbol in listOf("AC", "⌫", "%") -> Pair(CalculatorButtonDark, CalculatorText)
            symbol in listOf("+", "-", "×", "÷", "=") -> Pair(AccentCyan, Color.Black)
            else -> Pair(CalculatorCard, CalculatorText)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(onClick = onClick)
        ) {
            Text(
                text = symbol,
                fontSize = 20.sp,
                fontWeight = if (symbol == "=") FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }

    // Helper function to format result
    private fun formatResult(result: String): String {
        return try {
            val value = result.toDouble()
            if (value == value.toLong().toDouble()) {
                value.toLong().toString()
            } else {
                String.format("%.8f", value).trimEnd('0').trimEnd('.')
            }
        } catch (e: Exception) {
            result
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenWithCalculatorPreview() {
        MainScreenWithCalculator()
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 520)
    @Composable
    fun CalculatorContentPreview() {
        var expression by remember { mutableStateOf("12+5") }
        var result by remember { mutableStateOf("17") }

        CalculatorContent(
            expression = expression,
            result = result,
            onExpressionChange = { expression = it },
            onResultChange = { result = it }
        )
    }
