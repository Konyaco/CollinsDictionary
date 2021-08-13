package me.konyaco.collinsdictionary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.konyaco.collinsdictionary.service.CobuildDictionarySection
import me.konyaco.collinsdictionary.service.DefinitionEntry
import me.konyaco.collinsdictionary.service.WordForm
import me.konyaco.collinsdictionary.service.soundPlayer
import me.konyaco.collinsdictionary.ui.MyRes
import me.konyaco.collinsdictionary.ui.SourceSerifProFontFamily

@Composable
fun CobuildDictionarySection(
    section: CobuildDictionarySection,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val soundPlayer = soundPlayer
            var soundPlaying by remember { mutableStateOf(false) }
            var error by remember { mutableStateOf(false) }

            if (section.pronunciation.soundUrl != null) WordInfoWithSound(
                word = section.word,
                ipa = section.pronunciation.ipa,
                soundPlaying = soundPlaying,
                soundPlayError = error,
                onSoundClick = {
                    soundPlayer.play(
                        url = section.pronunciation.soundUrl,
                        onStart = {
                            soundPlaying = true
                            error = false
                        },
                        onStop = { soundPlaying = false },
                        onError = {
                            println(it)
                            soundPlaying = false
                            error = true
                        }
                    )
                }
            ) else WordInfo(section.word, section.pronunciation.ipa)
            // Word Forms
            section.forms?.let { WordForms(it, Modifier.fillMaxWidth()) }
            // Definitions
            Definitions(section.definitionEntries, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun Divider(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(5.dp, 10.dp).background(MaterialTheme.colors.primary))
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun WordInfoWithSound(
    word: String,
    ipa: String,
    soundPlaying: Boolean, // TODO: 2021/8/13
    soundPlayError: Boolean,
    onSoundClick: () -> Unit
) {
    // Word and IPA
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = word,
            fontFamily = SourceSerifProFontFamily,
            fontSize = 34.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSoundClick
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "/$ipa/",
                fontSize = 24.sp,
                color = MaterialTheme.colors.onBackground.copy(0.54f),
                lineHeight = 28.sp
            )
            // Sound playing animation
            Icon(
                modifier = Modifier.offset(y = 1.dp), // To visually align to horizon
                painter = MyRes.Sound,
                contentDescription = "Sound",
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun WordInfo(
    word: String,
    ipa: String
) {
    // Word and IPA
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = word,
            fontFamily = SourceSerifProFontFamily,
            fontSize = 34.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "/$ipa/",
            fontSize = 24.sp,
            color = MaterialTheme.colors.onBackground.copy(0.54f),
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun WordForms(forms: List<WordForm>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Divider("WORD FORMS")
        Spacer(Modifier.height(4.dp))
        forms.forEach { form ->
            Row(Modifier.sizeIn(maxWidth = 512.dp)) {
                Text(
                    modifier = Modifier.alignByBaseline().weight(1f),
                    text = form.description,
                    color = MaterialTheme.colors.onBackground.copy(0.54f),
                    fontSize = 16.sp // TODO: Font: Roboto
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = form.spell,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    fontFamily = SourceSerifProFontFamily
                )
            }
        }
    }
}

@Composable
private fun Definitions(definitionEntries: List<DefinitionEntry>, modifier: Modifier = Modifier) {
    Column(modifier) {
        Divider("DEFINITIONS")
        Spacer(Modifier.height(8.dp))
        definitionEntries.forEach { entry ->
            // Word Type
            Row {
                Text(
                    text = "${entry.index}.",
                    color = MaterialTheme.colors.primary,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = entry.type.uppercase(),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Column(Modifier.widthIn(max = with(LocalDensity.current) {
                // https://material.io/design/typography/understanding-typography.html#readability
                // According to Material Guidance, 40 - 60 characters width is the best.
                (16.sp * 50).toDp()
            })) {
                // Definition
                Text(
                    modifier = Modifier.padding(start = 18.dp),
                    text = entry.definition.def,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground.copy(0.54f),
                    lineHeight = (18.75).sp // TODO: Font Roboto
                )
                // Example Sentences
                Spacer(Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    entry.definition.examples.forEach { example ->
                        ExampleSentence(example.sentence)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExampleSentence(sentence: String) {
    Row {
        Box(
            Modifier.padding(top = 10.dp, bottom = 6.dp, start = 8.dp, end = 8.dp).size(2.dp)
                .background(MaterialTheme.colors.onBackground, CircleShape)
        )
        Text(
            text = sentence,
            fontSize = 16.sp,
            fontFamily = SourceSerifProFontFamily,
            lineHeight = 22.sp
        )
    }
}


/*
@Preview
@Composable
private fun Preview() {
    MyTheme {
        CobuildDictionarySection(
            CobuildDictionarySection(
                word = "example",
                forms = listOf(WordForm("plural", "examples")),
                pronunciation = Pronunciation("ɪgzɑːmpəl", null),
                definitionEntries = listOf(
                    DefinitionEntry(
                        1, "COUNTABLE NOUN", Definition(
                            "An example of something is a particular situation, object, or person which shows that what is being claimed is true.",
                            listOf(
                                ExampleSentence(
                                    "The doctors gave numerous examples of patients being expelled from hospital.",
                                    "+ of",
                                    null,
                                    emptyList()
                                ),
                                ExampleSentence(
                                    "Listed below are just a few examples of some of the family benefits available.",
                                    "+ of",
                                    null,
                                    emptyList()
                                )
                            )
                        ),
                        extraDefinitions = emptyList()
                    ),
                    DefinitionEntry(
                        2, "COUNTABLE NOUN", definition = Definition(
                            "An example of a particular class of objects or styles is something that has many of the typical features of such a class or style, and that you consider clearly represents it.",
                            listOf(
                                ExampleSentence(
                                    "Symphonies 103 and 104 stand as perfect examples of early symphonic construction.",
                                    "+ of",
                                    null,
                                    emptyList()
                                ),
                                ExampleSentence(
                                    "The plaque illustrated in Figure 1 is an example of his work at this time.",
                                    null,
                                    null,
                                    emptyList()
                                )
                            )
                        ),
                        emptyList()
                    )
                )
            )
        )
    }
}*/
