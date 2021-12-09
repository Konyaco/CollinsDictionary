package me.konyaco.collinsdictionary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import me.konyaco.collinsdictionary.ui.App
import me.konyaco.collinsdictionary.ui.MyTheme
import me.konyaco.collinsdictionary.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {
    private val component by lazy {
        AppViewModel((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                App(component)
            }
        }
    }
}