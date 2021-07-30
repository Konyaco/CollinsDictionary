import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import service.*

@Composable
fun CobuildDictionarySection(section: CobuildDictionarySection, modifier: Modifier = Modifier) {
    SelectionContainer {
        Column(modifier) {
            // Word and IPA
            Row {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = section.word,
                    fontFamily = FontFamily.Serif,
                    fontSize = 48.sp
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = "/" + section.pronunciation.ipa + "/", fontStyle = FontStyle.Italic, fontSize = 14.sp
                )
                // TODO: 2021/7/30 Sound
            }
            // Word Forms
            section.forms?.let { forms ->
                Spacer(Modifier.height(16.dp))
                Row {
                    Text("Word forms: ", modifier = Modifier.alignByBaseline())
                    Column(modifier = Modifier.alignByBaseline()) {
                        forms.forEach {
                            Text(buildAnnotatedString {
                                pushStyle(SpanStyle(fontFamily = FontFamily.Serif))
                                append(it.description)
                                append(": ")
                                pop()
                                pushStyle(
                                    SpanStyle(
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                                append(it.spell)
                                pop()
                            })
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            // Definitions
            section.definitionEntries.forEach { entry ->
                Row {
                    Text("${entry.index}.")
                    Spacer(Modifier.width(8.dp))
                    Text(entry.type)
                }
                Spacer(Modifier.height(8.dp))

                Column(Modifier.padding(start = 24.dp).widthIn(max = with(LocalDensity.current) {
                    // https://material.io/design/typography/understanding-typography.html#readability
                    // According to Material Guidance, 40 - 60 characters width is the best.
                    (16.sp * 50).toDp()
                })) {
                    Text(text = entry.definition.def, fontSize = 16.sp)
                    // Example Sentences
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        entry.definition.examples.forEach { example ->
                            Text(
                                text = example.sentence,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

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
}