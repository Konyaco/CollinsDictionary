package me.konyaco.collinsdictionary.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.konyaco.collinsdictionary.ui.myColors

@Composable
fun SearchBox(
    modifier: Modifier,
    value: String,
    onValueChange: (newValue: String) -> Unit,
    isSearching: Boolean,
    onSearchClick: () -> Unit
) {
    Box(modifier.fillMaxWidth()) {
        Surface(Modifier.fillMaxWidth().wrapContentHeight(), color = myColors.searchBoxBackground) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    modifier = Modifier.matchParentSize(),
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    keyboardActions = KeyboardActions(
                        onDone = { onSearchClick() },
                        onSearch = { onSearchClick() }
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Search
                    ),
                    textStyle = TextStyle(
                        color = myColors.onSearchBox.copy(0.6f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    decorationBox = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            it()
                            if (value.isEmpty()) Text(
                                "Search...",
                                fontSize = 16.sp,
                                color = myColors.onSearchBox.copy(0.6f)
                            ) else IconButton(
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp),
                                onClick = {
                                    onValueChange("")
                                },
                                enabled = !isSearching
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    "Clear",
                                    tint = myColors.onSearchBox.copy(LocalContentAlpha.current)
                                )
                            }
                        }
                    }
                )

                val focusManager = LocalFocusManager.current
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        focusManager.clearFocus()
                        onSearchClick()
                    },
                    enabled = !isSearching && value.isNotBlank()
                ) {
                    Icon(
                        Icons.Filled.Search, "Search", tint = myColors.onSearchBox
                            .copy(LocalContentAlpha.current)
                    )
                }
            }
        }
        Box(Modifier.align(Alignment.BottomCenter).offset(y = 4.dp).height(4.dp).fillMaxWidth()) {
            AnimatedVisibility(
                isSearching,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    }
}