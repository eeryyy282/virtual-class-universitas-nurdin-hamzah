package com.mjs.virtualclassuniversitasnurdinhamzah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mjs.virtualclassuniversitasnurdinhamzah.ui.theme.VirtualclassuniversitasnurdinhamzahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VirtualclassuniversitasnurdinhamzahTheme {

            }
        }
    }
}
