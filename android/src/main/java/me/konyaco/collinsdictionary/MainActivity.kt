package me.konyaco.collinsdictionary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import me.konyaco.collinsdictionary.ui.App
import me.konyaco.collinsdictionary.ui.MyTheme

class MainActivity : AppCompatActivity() {
    private val viewModel by lazy {
        (application as MyApplication).viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                App(viewModel)
            }
        }
    }
}