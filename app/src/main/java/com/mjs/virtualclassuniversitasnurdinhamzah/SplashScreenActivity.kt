package com.mjs.virtualclassuniversitasnurdinhamzah

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mjs.virtualclassuniversitasnurdinhamzah.ui.theme.Lato
import com.mjs.virtualclassuniversitasnurdinhamzah.ui.theme.VirtualclassuniversitasnurdinhamzahTheme
import com.mjs.virtualclassuniversitasnurdinhamzah.ui.theme.classic_blue
import com.mjs.virtualclassuniversitasnurdinhamzah.ui.theme.white

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VirtualclassuniversitasnurdinhamzahTheme {
                SplashPreview()
            }
        }
        val delay: Long = 2500
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, delay)
    }
}

@Composable
fun LogoUnh() {
    Image(
        painterResource(R.drawable.logo_unh),
        contentDescription = "Logo UNH",
        modifier = Modifier
            .height(190.dp)
            .width(190.dp)
    )
}

@Composable
fun CopyrightMjs() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = classic_blue
        )
    ) {
        Text(
            text = "© Muhammad Juzairi Safitli",
            modifier = Modifier
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            fontFamily = Lato,
            color = white,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    VirtualclassuniversitasnurdinhamzahTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            LogoUnh()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 34.dp
                    ),
                contentAlignment = Alignment.BottomCenter,
            ) {
                CopyrightMjs()
            }
        }
    }
}