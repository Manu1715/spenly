package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Locale
import kotlin.math.abs

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
                    // The CalculatorDropdown handles the logic to show/hide.
                    CalculatorDropdown(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        // This button simply toggles the state.
                        IconButton(onClick = { expanded = !expanded }) {
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
        // 1. Render the anchor (IconButton)
        anchor()

        // 2. THE FIX: Overlay an invisible box over the anchor when expanded.
        if (expanded) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // No ripple effect
                    ) {
                        onExpandedChange(false)
                    }
            )
        }

        // 3. Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            modifier = Modifier
                .width(320.dp)
                .height(520.dp),
            offset = DpOffset(x = (-260).dp, y = 8.dp),
            properties = PopupProperties(
                focusable = true,
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
                        val evalResult = ExpressionBuilder(realExpression).build().evaluate()
                        val formattedResult = formatResult(evalResult.toString())
                        onResultChange(formattedResult)
                        onExpressionChange(expression)
                    }
                } catch (e: Exception) {
                    onResultChange("Error")
                }
            }
            else -> {
                val isOperator = action in listOf("+", "-", "×", "÷")

                if (result.isNotBlank() && result != "Error") {
                    if (isOperator) {
                        onExpressionChange(result + action)
                        onResultChange("")
                    } else {
                        onExpressionChange(action)
                        onResultChange("")
                    }
                } else {
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
            // USE AutoResizingText HERE for Expression
            AutoResizingText(
                text = expression.ifEmpty { "0" },
                baseFontSize = 20.sp,
                color = CalculatorTextSecondary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // USE AutoResizingText HERE for Result
            AutoResizingText(
                text = result.ifEmpty { "0" },
                baseFontSize = 32.sp,
                color = if (result == "Error") MaterialTheme.colorScheme.error else CalculatorText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
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
                        SheetCalculatorButton(
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
fun SheetCalculatorButton(
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

/**
 * A helper component that shrinks the font size automatically if the text
 * is too long to fit on a single line.
 */
@Composable
fun AutoResizingText(
    text: String,
    baseFontSize: TextUnit,
    color: Color,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = 12.sp
) {
    var currentFontSize by remember { mutableStateOf(baseFontSize) }

    // Reset to base font size when text changes significantly (e.g. reset)
    LaunchedEffect(text) {
        if (text == "0" || text.isEmpty()) {
            currentFontSize = baseFontSize
        }
    }

    Text(
        text = text,
        color = color,
        fontSize = currentFontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                // If it overflows, shrink font size
                if (currentFontSize > minFontSize) {
                    currentFontSize *= 0.9f
                }
            }
        },
        modifier = modifier
    )
}

private fun formatResult(result: String): String {
    return try {
        val value = result.toDouble()

        // Handle Error or Infinity
        if (value.isInfinite() || value.isNaN()) return result

        // Check magnitude to switch to Scientific Notation for very big/small numbers
        val absValue = abs(value)
        val isLarge = absValue >= 1e12 // e.g., 1 Trillion
        val isTiny = absValue > 0 && absValue < 1e-9

        if (isLarge || isTiny) {
            // Use scientific notation (e.g., 1.23E12)
            String.format(Locale.US, "%.6e", value)
        } else if (value == value.toLong().toDouble()) {
            // It's an integer (e.g., 123.0 -> 123)
            value.toLong().toString()
        } else {
            // Standard decimal formatting
            String.format(Locale.US, "%.8f", value)
                .trimEnd('0')
                .trimEnd('.')
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
