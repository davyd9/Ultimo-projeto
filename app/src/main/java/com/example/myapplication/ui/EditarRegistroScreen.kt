package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.logic.VelocidadeConfigHelper

private val RoxoEdicao = Color(0xFF5145AA)
private val FundoEdicao = Color(0xFFFCF9FF)
private val CardLilasInfo = Color(0xFFF0ECFC)
private val BordaCampo = Color(0xFF8A8594)
private val VermelhoExcluir = Color(0xFFB83B2A)
private val BordaVermelhaSuave = Color(0xFFE6C8C4)

@Composable
fun EditarRegistroScreen(
    jogador: RankingJogadorUi,
    onVoltar: () -> Unit,
    onSalvarAlteracoes: (novoApelido: String, novaVelocidadePadrao: String) -> Unit,
    onExcluirRegistro: () -> Unit
) {
    var apelido by remember(jogador.id, jogador.apelido) {
        mutableStateOf(jogador.apelido)
    }
    var velocidadeSelecionada by remember(jogador.id, jogador.velocidadePadrao) {
        mutableStateOf(jogador.velocidadePadrao)
    }
    var menuVelocidadeAberto by remember { mutableStateOf(false) }

    val inicialAtual = apelido
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: jogador.inicial.ifBlank { "M" }

    val infoVelocidade = VelocidadeConfigHelper.obterInfoVelocidade(velocidadeSelecionada)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoEdicao)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onVoltar,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar para o Ranking",
                    tint = Color(0xFF1E1E24),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = "Editar Registro",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A22)
                )
                Text(
                    text = "Ranking #${jogador.posicao} • ${jogador.pontos} pontos",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8A98)
                )
            }
        }

        HorizontalDivider(
            color = Color(0xFFECE8F4),
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Bloco Central de Perfil
            Box(
                modifier = Modifier.size(86.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(22.dp))
                        .background(RoxoEdicao),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicialAtual,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E3FB))
                        .border(2.dp, FundoEdicao, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = RoxoEdicao,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dados do jogador",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E24),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Atualize as informações desta pontuação.",
                fontSize = 12.sp,
                color = Color(0xFF8E8A98),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Campo 1: Apelido do Jogador
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BordaCampo)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Apelido do Jogador",
                        fontSize = 11.sp,
                        color = Color(0xFF7A7685)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = apelido,
                        onValueChange = { apelido = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E1E24)
                        ),
                        cursorBrush = SolidColor(RoxoEdicao),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 2: Velocidade / dificuldade padrão
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { menuVelocidadeAberto = true },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BordaCampo)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Velocidade / dificuldade padrão",
                            fontSize = 11.sp,
                            color = Color(0xFF7A7685)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = velocidadeSelecionada,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E1E24)
                        )
                    }
                }

                DropdownMenu(
                    expanded = menuVelocidadeAberto,
                    onDismissRequest = { menuVelocidadeAberto = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    VelocidadeConfigHelper.opcoesVelocidade.forEach { opcao ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = opcao,
                                    fontWeight = if (opcao == velocidadeSelecionada) FontWeight.Bold else FontWeight.Normal,
                                    color = if (opcao == velocidadeSelecionada) RoxoEdicao else Color(0xFF1E1E24)
                                )
                            },
                            onClick = {
                                velocidadeSelecionada = opcao
                                menuVelocidadeAberto = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Informativo Lilás (#F0ECFC)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardLilasInfo
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bolt,
                        contentDescription = null,
                        tint = RoxoEdicao,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = infoVelocidade.titulo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F3586)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = infoVelocidade.subtitulo,
                            fontSize = 12.sp,
                            color = Color(0xFF7A7685)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Botão Primário: Salvar Alterações
            Button(
                onClick = {
                    onSalvarAlteracoes(apelido, velocidadeSelecionada)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoxoEdicao,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Salvar Alterações",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Secundário: Excluir Registro
            OutlinedButton(
                onClick = onExcluirRegistro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = VermelhoExcluir
                ),
                border = BorderStroke(1.dp, BordaVermelhaSuave)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = VermelhoExcluir,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Excluir Registro",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VermelhoExcluir
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
