package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.remote.FirebaseDesafioSource
import com.example.myapplication.data.repository.DesafioRepository
import com.example.myapplication.data.repository.RankingRepository
import com.example.myapplication.ui.AdminScreen
import com.example.myapplication.ui.AdminViewModel
import com.example.myapplication.ui.AdminViewModelFactory
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

                val desafioRepository = remember {
                    DesafioRepository(
                        desafioDao = database.desafioDao(),
                        firebaseSource = FirebaseDesafioSource()
                    )
                }
                val adminViewModel: AdminViewModel = viewModel(
                    factory = AdminViewModelFactory(desafioRepository)
                )
                val adminUiState by adminViewModel.uiState.collectAsState()

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
                                telaAtual = "admin"
                            }
                        )
                    }

                    "admin" -> {
                        AdminScreen(
                            uiState = adminUiState,
                            onNomeChange = { adminViewModel.atualizarNomeDesafio(it) },
                            onSequenciaChange = { adminViewModel.atualizarSequenciaCores(it) },
                            onDificuldadeChange = { adminViewModel.atualizarDificuldade(it) },
                            onPublicarOuSalvar = { adminViewModel.publicarOuSalvarDesafio() },
                            onEditarDesafio = { desafio -> adminViewModel.carregarParaEdicao(desafio) },
                            onExcluirDesafio = { desafio -> adminViewModel.excluirDesafio(desafio) },
                            onCancelarEdicao = { adminViewModel.cancelarEdicao() },
                            onVoltarLogin = {
                                adminViewModel.cancelarEdicao()
                                telaAtual = "login"
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
                            desafiosSincronizados = adminUiState.desafios,
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
