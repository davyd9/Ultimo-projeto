package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.ui.viewmodel.SimonUiState

private val RoxoSimon = Color(0xFF5145AA)
private val RoxoCentro = Color(0xFF352B73)
private val FundoSimon = Color(0xFFFCF9FF)

private val CorVerde = Color(0xFF5BA87D)
private val CorVermelha = Color(0xFFCD5D5D)
private val CorAmarela = Color(0xFFDFBD56)
private val CorAzul = Color(0xFF5383CE)

@Composable
fun SimonScreen(
    uiState: SimonUiState,
    onIniciarSequencia: () -> Unit,
    onSelecionarCor: (Int) -> Unit,
    onSelecionarModo: (DesafioEntity) -> Unit = {},
    onSair: () -> Unit = {}
) {
    var menuModosAberto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoSimon)
            .statusBarsPadding()
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.SportsEsports,
                    contentDescription = "Simon",
                    tint = Color(0xFF3E3486),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simon",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF282254)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF2EEFA)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PONTOS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E8A98)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${uiState.pontos}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF282254)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(Color(0xFFDFD9EC))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "RODADA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E8A98)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${uiState.rodada}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF282254)
                            )
                        }
                    }
                }

                // Botão de Sair (Logout / Trocar Perfil) na tela principal do jogo
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEDE8FA),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSair() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Sair para o Login",
                            tint = RoxoSimon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sair",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoxoSimon
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDF3E4)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = uiState.mensagemStatus,
                fontSize = 12.sp,
                color = Color(0xFF7A7685)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(10.dp, RoundedCornerShape(34.dp))
                .background(Color(0xFFECE9F2), RoundedCornerShape(34.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SimonColorPad(
                        nome = "Verde",
                        cor = CorVerde,
                        acesa = uiState.corAcesa == 0,
                        alinhamentoTexto = Alignment.TopStart,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelecionarCor(0) }
                    )
                    SimonColorPad(
                        nome = "Vermelho",
                        cor = CorVermelha,
                        acesa = uiState.corAcesa == 1,
                        alinhamentoTexto = Alignment.TopEnd,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelecionarCor(1) }
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SimonColorPad(
                        nome = "Amarelo",
                        cor = CorAmarela,
                        acesa = uiState.corAcesa == 2,
                        alinhamentoTexto = Alignment.BottomStart,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelecionarCor(2) }
                    )
                    SimonColorPad(
                        nome = "Azul",
                        cor = CorAzul,
                        acesa = uiState.corAcesa == 3,
                        alinhamentoTexto = Alignment.BottomEnd,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelecionarCor(3) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(RoxoCentro)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onIniciarSequencia,
            enabled = !uiState.reproduzindo && !uiState.aguardandoJogador,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoxoSimon,
                contentColor = Color.White,
                disabledContainerColor = RoxoSimon.copy(alpha = 0.65f),
                disabledContentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Sequência",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Toque nas cores na mesma ordem apresentada.",
            fontSize = 11.sp,
            color = Color(0xFF8E8A98)
        )

        Spacer(modifier = Modifier.height(14.dp))

        val (badgeBg, badgeText, iconeModo) = when (uiState.modoAtualDificuldade.lowercase()) {
            "difícil", "dificil" -> Triple(Color(0xFFEDE8FA), RoxoSimon, Icons.Outlined.EmojiEvents)
            "médio", "medio" -> Triple(Color(0xFFF6E6D5), Color(0xFF8C5319), Icons.Outlined.Bolt)
            else -> Triple(Color(0xFFE0F2E8), Color(0xFF247A4D), Icons.Outlined.Bolt)
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (uiState.modosDisponiveis.isNotEmpty()) {
                            menuModosAberto = true
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                border = BorderStroke(1.dp, Color(0xFFECE7F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconeModo,
                                contentDescription = null,
                                tint = badgeText,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = uiState.modoAtualTitulo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E24)
                            )
                            Text(
                                text = uiState.modoAtualDetalhe,
                                fontSize = 11.sp,
                                color = Color(0xFF8E8A98)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = uiState.modoAtualDificuldade,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = menuModosAberto,
                onDismissRequest = { menuModosAberto = false },
                modifier = Modifier.background(Color.White)
            ) {
                uiState.modosDisponiveis.forEach { desafio ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${desafio.titulo} (${desafio.dificuldade})",
                                    fontWeight = if (desafio.id == uiState.modoAtualId) FontWeight.Bold else FontWeight.Normal,
                                    color = if (desafio.id == uiState.modoAtualId) RoxoSimon else Color(0xFF1E1E24)
                                )
                            }
                        },
                        onClick = {
                            onSelecionarModo(desafio)
                            menuModosAberto = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SimonColorPad(
    nome: String,
    cor: Color,
    acesa: Boolean,
    alinhamentoTexto: Alignment,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(22.dp))
            .background(if (acesa) cor.copy(alpha = 0.55f) else cor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = alinhamentoTexto
    ) {
        Text(
            text = nome,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
