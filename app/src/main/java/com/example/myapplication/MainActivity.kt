
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {

                var telaAtual by remember {
                    mutableStateOf("login")
                }

                var nomeJogador by remember {
                    mutableStateOf("Jogador")
                }

                if (telaAtual == "login") {

                    LoginScreen(
                        onJogar = { nome ->
                            nomeJogador = nome
                            telaAtual = "simon"
                        },
                        onAdmin = {
                            nomeJogador = "Administrador"
                            telaAtual = "simon"
                        }
                    )

                } else {

                    SimonScreen(
                        nomeJogador = nomeJogador
                    )
                }
            }
        }
    }
}
