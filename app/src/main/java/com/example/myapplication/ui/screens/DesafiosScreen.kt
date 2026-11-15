package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.viewmodel.DesafioItemUi
import com.example.myapplication.ui.viewmodel.DesafiosUiState

private val RoxoDesafios = Color(0xFF5145AA)
private val FundoDesafios = Color(0xFFFCF9FF)

@Composable
fun DesafiosScreen(
    uiState: DesafiosUiState,
    onJogarDesafio: (DesafioItemUi) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoDesafios)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 22.dp, bottom = 20.dp)
    ) {
        item {
            DesafiosTopHeader(energia = uiState.energia)
        }

        item {
            CloudStatusCard(isConnected = uiState.conectadoNuvem)
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
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1E24)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Supere seus limites",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8A98)
                    )
                }
                Text(
                    text = "${uiState.desafios.size} níveis",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8A98)
                )
            }
        }

        items(
            items = uiState.desafios,
            key = { it.id }
        ) { desafio ->
            DesafioCardItem(
                desafio = desafio,
                onClick = { onJogarDesafio(desafio) }
            )
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
                fontSize = 10.sp,
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
            color = Color(0xFFEDE8FA)
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
    val bgColor = if (isConnected) Color(0xFFE6F4EC) else Color(0xFFFDECEC)
    val accentColor = if (isConnected) Color(0xFF287D4E) else Color(0xFFB83232)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudDone,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isConnected) "Conectado à nuvem" else "Modo local",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1F5135)
                )
                Text(
                    text = if (isConnected) "Desafios atualizados agora" else "Sincronizado localmente",
                    fontSize = 11.sp,
                    color = Color(0xFF5A7A68)
                )
            }

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
            )
        }
    }
}

@Composable
private fun DesafioCardItem(
    desafio: DesafioItemUi,
    onClick: () -> Unit
) {
    val (containerColor, contentColor, icone) = when (desafio.dificuldade.lowercase()) {
        "difícil", "dificil" -> Triple(
            Color(0xFFEDE8FA),
            RoxoDesafios,
            Icons.Outlined.EmojiEvents
        )
        "médio", "medio" -> Triple(
            Color(0xFFF6E6D5),
            Color(0xFF8C5319),
            Icons.Outlined.Bolt
        )
        else -> Triple(
            Color(0xFFE0F2E8),
            Color(0xFF247A4D),
            Icons.Outlined.Bolt
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFECE7F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(78.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = desafio.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E1E24)
                )
                Text(
                    text = desafio.descricao,
                    fontSize = 12.sp,
                    color = Color(0xFF7A7685)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = containerColor
                    ) {
                        Text(
                            text = desafio.dificuldade,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }

                    Text(
                        text = "Recorde: ${desafio.recordePts} pts",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8A98)
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
                modifier = Modifier.defaultMinSize(minWidth = 84.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "JOGAR DESAFIO",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
