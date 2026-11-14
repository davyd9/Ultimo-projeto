package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RoxoRanking = Color(0xFF5145AA)
private val FundoRanking = Color(0xFFFCF9FF)
private val LilasFiltroSelecionado = Color(0xFFE8E3FB)
private val CremeTrofeu = Color(0xFFFFF6D6)
private val DouradoTrofeu = Color(0xFFB78103)
private val DouradoPosicao = Color(0xFF9C7A23)
private val CinzaPosicao = Color(0xFF7A7585)
private val VermelhoLixeira = Color(0xFFB83B2A)

@Composable
fun RankingScreen(
    uiState: RankingUiState,
    onFiltroChange: (String) -> Unit,
    onAlternarOrdenacao: () -> Unit,
    onEditarJogador: (RankingJogadorUi) -> Unit,
    onExcluirJogador: (RankingJogadorUi) -> Unit,
    onAbaSelecionada: (String) -> Unit = {}
) {
    val filtros = listOf("Todos", "Fácil", "Difícil")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoRanking)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            // Cabeçalho
            RankingTopHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // Linha de Filtros e Ordenação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filtros.forEach { filtro ->
                        FiltroChipItem(
                            label = filtro,
                            selecionado = uiState.filtroAtivo == filtro,
                            onClick = { onFiltroChange(filtro) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAlternarOrdenacao() }
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Ordenar por pontos",
                        tint = RoxoRanking,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pontos",
                        color = RoxoRanking,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Exibição de erro caso ocorra falha capturada pelo try-catch no Repository
            uiState.errorMessage?.let { erro ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFDECEC)
                ) {
                    Text(
                        text = erro,
                        color = VermelhoLixeira,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Lista Dinâmica Obrigatória (LazyColumn)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = uiState.jogadores,
                    key = { jogador -> jogador.id }
                ) { jogador ->
                    RankingRowItem(
                        jogador = jogador,
                        onEditar = { onEditarJogador(jogador) },
                        onExcluir = { onExcluirJogador(jogador) }
                    )
                    HorizontalDivider(
                        color = Color(0xFFECE8F4),
                        thickness = 1.dp
                    )
                }
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(
                    Color(0xFF8989F1),
                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                )
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RankingMenuItem("♧", "Jogar", "Ranking") { onAbaSelecionada("Jogar") }
            RankingMenuItem("ϟ", "Desafios", "Ranking") { onAbaSelecionada("Desafios") }
            RankingMenuItem("♜", "Ranking", "Ranking") { onAbaSelecionada("Ranking") }
            RankingMenuItem("⚙", "Opções", "Ranking") { onAbaSelecionada("Opções") }
        }
    }
}

@Composable
private fun RankingTopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MELHORES JOGADORES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = RoxoRanking
            )
            Text(
                text = "Ranking",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A22)
            )
        }

        Surface(
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(14.dp),
            color = CremeTrofeu
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = "Troféu",
                    tint = DouradoTrofeu,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun FiltroChipItem(
    label: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selecionado) LilasFiltroSelecionado else Color.White,
        border = if (selecionado) null else BorderStroke(1.dp, Color(0xFFD9D4E4))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selecionado) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = RoxoRanking,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selecionado) RoxoRanking else Color(0xFF4A4656)
            )
        }
    }
}

@Composable
private fun RankingRowItem(
    jogador: RankingJogadorUi,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    val corPosicao = if (jogador.posicao <= 3) DouradoPosicao else CinzaPosicao

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${jogador.posicao}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = corPosicao,
            modifier = Modifier.width(22.dp)
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE8FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = jogador.inicial,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = RoxoRanking
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = jogador.apelido,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E24)
            )
            Text(
                text = jogador.dificuldade,
                fontSize = 12.sp,
                color = Color(0xFF8E8A98)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${jogador.pontos}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RoxoRanking,
                modifier = Modifier.alignByBaseline()
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "pts",
                fontSize = 11.sp,
                color = Color(0xFF8E8A98),
                modifier = Modifier.alignByBaseline()
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            onClick = onEditar,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar ${jogador.apelido}",
                tint = Color(0xFF5A5665),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(2.dp))

        IconButton(
            onClick = onExcluir,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Excluir ${jogador.apelido}",
                tint = VermelhoLixeira,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun RankingMenuItem(
    icone: String,
    titulo: String,
    selecionado: String,
    onClick: () -> Unit
) {
    val ativo = selecionado == titulo

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(66.dp)
                .height(38.dp)
                .background(
                    if (ativo) Color(0xFFE3DAFF) else Color.Transparent,
                    RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icone,
                fontSize = 23.sp,
                color = if (ativo) RoxoRanking else Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = titulo,
            fontSize = 11.sp,
            fontWeight = if (ativo) FontWeight.Bold else FontWeight.Normal,
            color = if (ativo) Color(0xFF302966) else Color.White
        )
    }
}
