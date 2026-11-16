package com.example.myapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.AdminScreen
import com.example.myapplication.ui.screens.DesafiosScreen
import com.example.myapplication.ui.screens.EditarRegistroScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.RankingScreen
import com.example.myapplication.ui.screens.SimonScreen
import com.example.myapplication.ui.viewmodel.AdminViewModel
import com.example.myapplication.ui.viewmodel.AppViewModelFactory
import com.example.myapplication.ui.viewmodel.DesafiosViewModel
import com.example.myapplication.ui.viewmodel.EditarRegistroViewModel
import com.example.myapplication.ui.viewmodel.LoginViewModel
import com.example.myapplication.ui.viewmodel.RankingViewModel
import com.example.myapplication.ui.viewmodel.SimonViewModel

object Rotas {
    const val LOGIN = "login"
    const val SIMON = "simon"
    const val DESAFIOS = "desafios"
    const val RANKING = "ranking"
    const val EDITAR_REGISTRO = "editar/{jogadorId}"
    const val ADMIN = "admin"

    fun editarComId(jogadorId: Int): String = "editar/$jogadorId"
}

private data class BottomNavItem(
    val titulo: String,
    val rota: String,
    val icone: ImageVector
)

@Composable
fun AppNavigation(
    factory: AppViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry?.destination?.route

    val exibirBottomBar = rotaAtual in listOf(
        Rotas.SIMON,
        Rotas.DESAFIOS,
        Rotas.RANKING
    )

    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val loginUiState by loginViewModel.uiState.collectAsState()

    val simonViewModel: SimonViewModel = viewModel(factory = factory)
    val simonUiState by simonViewModel.uiState.collectAsState()

    val sairERetornarAoLogin: () -> Unit = {
        loginViewModel.sairDoPerfil()
        simonViewModel.resetarPartida()
        navController.navigate(Rotas.LOGIN) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = Color(0xFFFCF9FF),
        bottomBar = {
            if (exibirBottomBar) {
                SimonBottomNavigationBar(
                    rotaAtual = rotaAtual,
                    onNavegar = { rotaDestino ->
                        navController.navigate(rotaDestino) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.LOGIN) {
                LoginScreen(
                    uiState = loginUiState,
                    onTextoChange = { loginViewModel.atualizarTextoEntrada(it) },
                    onHabilitarEdicao = { loginViewModel.habilitarEdicaoNomeOuCodigo() },
                    onClicarJogar = {
                        loginViewModel.entrarModoJogador(
                            onNavegarJogador = { nome ->
                                simonViewModel.definirNomeJogador(nome)
                                navController.navigate(Rotas.SIMON) {
                                    launchSingleTop = true
                                }
                            },
                            onNavegarAdmin = {
                                navController.navigate(Rotas.ADMIN) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    onClicarAdmin = {
                        loginViewModel.entrarPainelAdmin {
                            navController.navigate(Rotas.ADMIN) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Rotas.SIMON) {
                SimonScreen(
                    uiState = simonUiState,
                    onIniciarSequencia = { simonViewModel.iniciarSequencia() },
                    onSelecionarCor = { cor -> simonViewModel.selecionarCor(cor) },
                    onSelecionarModo = { desafio -> simonViewModel.selecionarModoJogo(desafio) },
                    onSair = sairERetornarAoLogin
                )
            }

            composable(Rotas.DESAFIOS) {
                val desafiosViewModel: DesafiosViewModel = viewModel(factory = factory)
                val desafiosUiState by desafiosViewModel.uiState.collectAsState()

                DesafiosScreen(
                    uiState = desafiosUiState,
                    onJogarDesafio = { desafio ->
                        desafiosViewModel.selecionarDesafio(desafio.id)
                        navController.navigate(Rotas.SIMON) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rotas.RANKING) {
                val rankingViewModel: RankingViewModel = viewModel(factory = factory)
                val rankingUiState by rankingViewModel.uiState.collectAsState()

                RankingScreen(
                    uiState = rankingUiState,
                    onFiltroChange = { filtro -> rankingViewModel.selecionarFiltro(filtro) },
                    onAlternarOrdenacao = { rankingViewModel.alternarOrdenacaoPontos() },
                    onEditarJogador = { jogador ->
                        navController.navigate(Rotas.editarComId(jogador.id))
                    },
                    onExcluirJogador = { jogador ->
                        rankingViewModel.excluirJogador(jogador)
                    }
                )
            }

            composable(
                route = Rotas.EDITAR_REGISTRO,
                arguments = listOf(
                    navArgument("jogadorId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val jogadorId = backStackEntry.arguments?.getInt("jogadorId") ?: 0
                val editarViewModel: EditarRegistroViewModel = viewModel(factory = factory)
                val editarUiState by editarViewModel.uiState.collectAsState()

                LaunchedEffect(jogadorId) {
                    editarViewModel.carregarJogador(jogadorId)
                }

                EditarRegistroScreen(
                    uiState = editarUiState,
                    onApelidoChange = { editarViewModel.atualizarApelido(it) },
                    onVelocidadeChange = { editarViewModel.atualizarVelocidadePadrao(it) },
                    onVoltar = {
                        navController.navigate(Rotas.RANKING) {
                            popUpTo(Rotas.RANKING) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSalvarAlteracoes = {
                        editarViewModel.salvarAlteracoes {
                            navController.navigate(Rotas.RANKING) {
                                popUpTo(Rotas.RANKING) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onExcluirRegistro = {
                        editarViewModel.excluirRegistroAtual {
                            sairERetornarAoLogin()
                        }
                    }
                )
            }

            composable(Rotas.ADMIN) {
                val adminViewModel: AdminViewModel = viewModel(factory = factory)
                val adminUiState by adminViewModel.uiState.collectAsState()

                AdminScreen(
                    uiState = adminUiState,
                    onNomeChange = { adminViewModel.atualizarNomeDesafio(it) },
                    onSequenciaChange = { adminViewModel.atualizarSequenciaCores(it) },
                    onDificuldadeChange = { adminViewModel.atualizarDificuldade(it) },
                    onPublicarOuSalvar = { adminViewModel.publicarOuSalvarDesafio() },
                    onEditarDesafio = { desafio -> adminViewModel.carregarParaEdicao(desafio) },
                    onExcluirDesafio = { desafio ->
                        adminViewModel.excluirDesafio(desafio)
                    },
                    onExcluirRegistroEmEdicao = {
                        adminViewModel.excluirDesafioEmEdicao()
                    },
                    onCancelarEdicao = { adminViewModel.cancelarEdicao() },
                    onVoltarLogin = {
                        adminViewModel.cancelarEdicao()
                        sairERetornarAoLogin()
                    }
                )
            }
        }
    }
}

@Composable
private fun SimonBottomNavigationBar(
    rotaAtual: String?,
    onNavegar: (String) -> Unit
) {
    val itens = listOf(
        BottomNavItem("Jogar", Rotas.SIMON, Icons.Outlined.SportsEsports),
        BottomNavItem("Desafios", Rotas.DESAFIOS, Icons.Outlined.Bolt),
        BottomNavItem("Ranking", Rotas.RANKING, Icons.Outlined.Leaderboard),
        BottomNavItem("Opções", Rotas.editarComId(0), Icons.Outlined.Settings)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF6F3FC)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                color = Color(0xFFE7E2F2),
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                itens.forEach { item ->
                    val selecionado = when (item.titulo) {
                        "Jogar" -> rotaAtual == Rotas.SIMON
                        "Desafios" -> rotaAtual == Rotas.DESAFIOS
                        "Ranking" -> rotaAtual == Rotas.RANKING
                        "Opções" -> rotaAtual?.startsWith("editar") == true
                        else -> false
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavegar(item.rota) }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(
                                    if (selecionado) Color(0xFFE3DCFA) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icone,
                                contentDescription = item.titulo,
                                tint = if (selecionado) Color(0xFF3E3486) else Color(0xFF5A5666),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = item.titulo,
                            fontSize = 11.sp,
                            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Medium,
                            color = if (selecionado) Color(0xFF3E3486) else Color(0xFF5A5666)
                        )
                    }
                }
            }
        }
    }
}
