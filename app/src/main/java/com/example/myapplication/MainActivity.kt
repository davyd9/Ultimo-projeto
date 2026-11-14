package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.RankingRepository
import com.example.myapplication.ui.EditarRegistroScreen
import com.example.myapplication.ui.RankingJogadorUi
import com.example.myapplication.ui.RankingScreen
import com.example.myapplication.ui.RankingViewModel
import com.example.myapplication.ui.RankingViewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {

                val database = remember { AppDatabase.getInstance(applicationContext) }
                val rankingRepository = remember {
                    RankingRepository(
                        rankingDao = database.rankingDao(),
                        perfilConfigDao = database.perfilConfigDao()
                    )
                }
                val rankingViewModel: RankingViewModel = viewModel(
                    factory = RankingViewModelFactory(rankingRepository)
                )
                val rankingUiState by rankingViewModel.uiState.collectAsState()

                var telaAtual by remember {
                    mutableStateOf("login")
                }

                var nomeJogador by remember {
                    mutableStateOf("Jogador")
                }

                val navegarParaAba: (String) -> Unit = { aba ->
                    when (aba) {
                        "Jogar" -> telaAtual = "simon"
                        "Desafios" -> telaAtual = "desafios"
                        "Ranking" -> telaAtual = "ranking"
                        "Opções" -> {
                            rankingViewModel.selecionarJogadorPadraoOuUltimo("Marina")
                            telaAtual = "opcoes"
                        }
                    }
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
                            onAbaSelecionada = navegarParaAba
                        )
                    }

                    "desafios" -> {
                        DesafiosScreen(
                            onAbaSelecionada = navegarParaAba,
                            onJogarDesafio = { desafio ->
                                telaAtual = "simon"
                            }
                        )
                    }

                    "ranking" -> {
                        RankingScreen(
                            uiState = rankingUiState,
                            onFiltroChange = { filtro ->
                                rankingViewModel.selecionarFiltro(filtro)
                            },
                            onAlternarOrdenacao = {
                                rankingViewModel.alternarOrdenacaoPontos()
                            },
                            onEditarJogador = { jogador ->
                                rankingViewModel.selecionarJogadorParaEdicao(jogador)
                                telaAtual = "opcoes"
                            },
                            onExcluirJogador = { jogador ->
                                rankingViewModel.excluirJogador(jogador)
                            },
                            onAbaSelecionada = navegarParaAba
                        )
                    }

                    "opcoes" -> {
                        val jogadorEmEdicao = rankingUiState.jogadorSelecionado
                            ?: rankingUiState.todosJogadores.find {
                                it.apelido.equals("Marina", ignoreCase = true)
                            }
                            ?: rankingUiState.todosJogadores.firstOrNull()
                            ?: RankingJogadorUi(
                                id = 0,
                                posicao = 4,
                                inicial = "M",
                                apelido = "Marina",
                                dificuldade = "Fácil",
                                velocidadePadrao = "Lenta • Fácil",
                                pontos = 650
                            )

                        EditarRegistroScreen(
                            jogador = jogadorEmEdicao,
                            onVoltar = {
                                telaAtual = "ranking"
                            },
                            onSalvarAlteracoes = { novoApelido, novaVelocidade ->
                                rankingViewModel.salvarAlteracoesJogador(
                                    id = jogadorEmEdicao.id,
                                    novoApelido = novoApelido,
                                    novaVelocidadePadrao = novaVelocidade,
                                    pontosAtuais = jogadorEmEdicao.pontos
                                )
                                telaAtual = "ranking"
                            },
                            onExcluirRegistro = {
                                rankingViewModel.excluirJogador(jogadorEmEdicao)
                                telaAtual = "ranking"
                            }
                        )
                    }
                }
            }
        }
    }
}
