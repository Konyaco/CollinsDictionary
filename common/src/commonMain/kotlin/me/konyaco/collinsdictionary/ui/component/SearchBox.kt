package me.konyaco.collinsdictionary.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
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
    onSearchClick: () -> Unit
) {
    Surface(modifier, color = myColors.searchBoxBackground) {
        Box(contentAlignment = Alignment.CenterStart) {
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
                maxLines = 1,
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
                        )
                    }
                }
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onSearchClick
            ) {
                Icon(Icons.Filled.Search, "Search", tint = myColors.onSearchBox)
            }
        }
    }
}