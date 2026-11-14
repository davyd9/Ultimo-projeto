package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RoxoDesafios = Color(0xFF5145AA)
private val FundoDesafios = Color(0xFFFCF9FF)

enum class Dificuldade(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector
) {
    DIFICIL(
        label = "Difícil",
        containerColor = Color(0xFFE5DDFB),
        contentColor = RoxoDesafios,
        icon = Icons.Outlined.EmojiEvents
    ),
    MEDIO(
        label = "Médio",
        containerColor = Color(0xFFF6E3CE),
        contentColor = Color(0xFF8C5319),
        icon = Icons.Outlined.Bolt
    ),
    FACIL(
        label = "Fácil",
        containerColor = Color(0xFFD9EFE3),
        contentColor = Color(0xFF247A4D),
        icon = Icons.Outlined.Bolt
    )
}

data class DesafioItem(
    val id: String,
    val titulo: String,
    val descricao: String,
    val dificuldade: Dificuldade,
    val recordePts: Int,
    val tamanhoSequencia: Int,
    val velocidadeMs: Long
)

@Composable
fun DesafiosScreen(
    onAbaSelecionada: (String) -> Unit = {},
    onJogarDesafio: (DesafioItem) -> Unit = {}
) {
    val desafios = remember {
        listOf(
            DesafioItem(
                id = "mestre",
                titulo = "Nível Mestre",
                descricao = "8 cores na sequência",
                dificuldade = Dificuldade.DIFICIL,
                recordePts = 340,
                tamanhoSequencia = 8,
                velocidadeMs = 600L
            ),
            DesafioItem(
                id = "relampago",
                titulo = "Ritmo Relâmpago",
                descricao = "6 cores • velocidade rápida",
                dificuldade = Dificuldade.MEDIO,
                recordePts = 220,
                tamanhoSequencia = 6,
                velocidadeMs = 300L
            ),
            DesafioItem(
                id = "primeiros_passos",
                titulo = "Primeiros Passos",
                descricao = "4 cores na sequência",
                dificuldade = Dificuldade.FACIL,
                recordePts = 100,
                tamanhoSequencia = 4,
                velocidadeMs = 600L
            )
        )
    }

    var energia by remember { mutableIntStateOf(5) }
    var conectadoNuvem by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoDesafios)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 25.dp, bottom = 20.dp)
        ) {
            item {
                DesafiosTopHeader(energia = energia)
            }

            item {
                CloudStatusCard(isConnected = conectadoNuvem)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Escolha seu desafio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E24)
                        )
                        Text(
                            text = "Supere seus limites",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "${desafios.size} níveis",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            items(desafios, key = { it.id }) { desafio ->
                DesafioCard(
                    desafio = desafio,
                    onClick = { onJogarDesafio(desafio) }
                )
            }
        }

        // Menu inferior igual ao da SimonScreen
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
            DesafiosMenuItem("♧", "Jogar", "Desafios") { onAbaSelecionada("Jogar") }
            DesafiosMenuItem("ϟ", "Desafios", "Desafios") { onAbaSelecionada("Desafios") }
            DesafiosMenuItem("♜", "Ranking", "Desafios") { onAbaSelecionada("Ranking") }
            DesafiosMenuItem("⚙", "Opções", "Desafios") { onAbaSelecionada("Opções") }
        }
    }
}

@Composable
private fun DesafiosTopHeader(energia: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "JOGUE E EVOLUA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = RoxoDesafios
            )
            Text(
                text = "Desafios",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A22)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEFEAF7)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = "Energia",
                    tint = RoxoDesafios,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = energia.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RoxoDesafios
                )
            }
        }
    }
}

@Composable
private fun CloudStatusCard(isConnected: Boolean) {
    val bgColor = if (isConnected) Color(0xFFE3F5EB) else Color(0xFFFDECEC)
    val iconBoxColor = if (isConnected) Color(0xFFCDEBD9) else Color(0xFFF8D0D0)
    val accentColor = if (isConnected) Color(0xFF237A4B) else Color(0xFFB83232)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDone,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isConnected) "Conectado à nuvem" else "Modo offline",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accentColor
                )
                Text(
                    text = if (isConnected) "Desafios atualizados agora" else "Verifique sua conexão",
                    fontSize = 12.sp,
                    color = accentColor.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }
    }
}

@Composable
private fun DesafioCard(
    desafio: DesafioItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(84.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(desafio.dificuldade.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = desafio.dificuldade.icon,
                    contentDescription = null,
                    tint = desafio.dificuldade.contentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = desafio.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E1E24)
                )
                Text(
                    text = desafio.descricao,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = desafio.dificuldade.containerColor
                    ) {
                        Text(
                            text = desafio.dificuldade.label,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = desafio.dificuldade.contentColor
                        )
                    }

                    Text(
                        text = "Recorde: ${desafio.recordePts} pts",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoxoDesafios,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                modifier = Modifier.defaultMinSize(minWidth = 86.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "JOGAR\nDESAFIO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DesafiosMenuItem(
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
                color = if (ativo) RoxoDesafios else Color.White
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
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DesafiosScreenPreview() {
    DesafiosScreen()
}