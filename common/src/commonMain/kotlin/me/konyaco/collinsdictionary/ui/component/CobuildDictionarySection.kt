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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.konyaco.collinsdictionary.service.*
import me.konyaco.collinsdictionary.ui.MyRes
import me.konyaco.collinsdictionary.ui.SourceSerifProFontFamily

@Composable
fun CobuildDictionarySection(
    section: CobuildDictionarySection,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val soundPlayer = LocalSoundPlayer.current

            var soundPlaying by remember { mutableStateOf(false) }
            var error by remember { mutableStateOf(false) }

            if (section.pronunciation.soundUrl != null) WordInfoWithSound(
                word = section.word,
                frequency = section.frequency,
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
                            soundPlaying = false
                            error = true
                        }
                    )
                }
            ) else WordInfo(section.word, section.pronunciation.ipa, section.frequency)
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
    frequency: Int?,
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
            // TODO: Sound playing animation
            Icon(
                modifier = Modifier.offset(y = 1.dp), // To visually align to horizon
                painter = MyRes.Sound,
                contentDescription = "Sound",
                tint = MaterialTheme.colors.primary
            )
        }
        frequency?.let {
            Spacer(Modifier.height(4.dp))
            WordFrequency(it)
        }
    }
}

@Composable
private fun WordInfo(
    word: String,
    ipa: String,
    frequency: Int?
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
        frequency?.let {
            Spacer(Modifier.height(4.dp))
            WordFrequency(it)
        }
    }
}

@Composable
private fun WordFrequency(frequency: Int, modifier: Modifier = Modifier) {
    val activeColor: Color = MaterialTheme.colors.primary
    val inactiveColor: Color = activeColor.copy(0.24f)

    Row(
        modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape)
                    .background(if (it < frequency) activeColor else inactiveColor)
            )
        }
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
            DefinitionEntry(entry)
        }
    }
}

@Composable
private fun DefinitionEntry(entry: DefinitionEntry) {
    // Word Type
    WordType(entry)

    Spacer(Modifier.height(8.dp))

    Column(Modifier.widthIn(max = with(LocalDensity.current) {
        // https://material.io/design/typography/understanding-typography.html#readability
        // According to Material Guidance, 40 - 60 characters width is the best.
        (16.sp * 50).toDp()
    })) {
        // Definition
        Text(
            modifier = Modifier.padding(horizontal = 18.dp),
            text = entry.definition.def,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground.copy(0.54f),
            textAlign = TextAlign.Justify,
            lineHeight = (18.75).sp // TODO: Font Roboto
        )
        // Example Sentences
        Spacer(Modifier.height(4.dp))

        ExampleSentences(entry.definition.examples)

        Spacer(Modifier.height(8.dp))

        entry.definition.synonyms?.let {
            Synonyms(
                modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                words = it
            )
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun WordType(entry: DefinitionEntry) {
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
}


@Composable
private fun ExampleSentences(examples: List<ExampleSentence>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        examples.forEach { example ->
            ExampleSentence(example.sentence)
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
            modifier = Modifier.padding(end = 18.dp),
            text = sentence,
            fontSize = 16.sp,
            fontFamily = SourceSerifProFontFamily,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify
        )
    }
}