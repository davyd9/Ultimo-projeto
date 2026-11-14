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

                when (telaAtual) {
                    "login" -> {
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
                    }

                    "simon" -> {
                        SimonScreen(
                            nomeJogador = nomeJogador,
                            onAbaSelecionada = { aba ->
                                when (aba) {
                                    "Jogar" -> telaAtual = "simon"
                                    "Desafios" -> telaAtual = "desafios"
                                    // Quando fizerem as outras telas:
                                    // "Ranking" -> telaAtual = "ranking"
                                    // "Opções" -> telaAtual = "opcoes"
                                }
                            }
                        )
                    }

                    "desafios" -> {
                        DesafiosScreen(
                            onAbaSelecionada = { aba ->
                                when (aba) {
                                    "Jogar" -> telaAtual = "simon"
                                    "Desafios" -> telaAtual = "desafios"
                                }
                            },
                            onJogarDesafio = { desafio ->
                                telaAtual = "simon"
                            }
                        )
                    }
                }
            }
        }
    }
}
