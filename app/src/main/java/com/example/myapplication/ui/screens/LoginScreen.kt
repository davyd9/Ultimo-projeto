package com.example.myapplication.ui.screens

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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.viewmodel.LoginUiState

private val RoxoLogin = Color(0xFF5145AA)
private val FundoLogin = Color(0xFFFCF9FF)
private val TextoTitulo = Color(0xFF282254)
private val BordaInputLogin = Color(0xFF8A8594)
private val VermelhoErroLogin = Color(0xFFB83B2A)

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onTextoChange: (String) -> Unit,
    onHabilitarEdicao: () -> Unit,
    onClicarJogar: () -> Unit,
    onClicarAdmin: () -> Unit
) {
    val nomeJaConfirmado = !uiState.nomeEscolhido.isNullOrBlank() && !uiState.editandoNomeOuCodigo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoLogin)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAD48B))
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6A4A4))
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier.size(124.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3EFFB))
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .border(1.dp, Color(0xFFE4DDF4), CircleShape)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 18.dp, y = 18.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF58A77C))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-18).dp, y = 18.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD15D5D))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 18.dp, y = (-18).dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0BA51))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-18).dp, y = (-18).dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5383CE))
            )

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(RoxoLogin),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SportsEsports,
                    contentDescription = "Logo Simon Master",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Simon Master",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextoTitulo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Observe, memorize e repita a sequência.",
            fontSize = 13.sp,
            color = Color(0xFF7A7685),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(
                width = 1.dp,
                color = if (uiState.errorMessage != null) VermelhoErroLogin else BordaInputLogin
            )
        ) {
            if (nomeJaConfirmado) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jogador selecionado",
                            fontSize = 11.sp,
                            color = Color(0xFF7A7685)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.nomeEscolhido ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoTitulo
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEDE8FA),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onHabilitarEdicao() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Editar nome ou inserir Código Admin",
                                tint = RoxoLogin,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Editar / Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoxoLogin
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Nome do Jogador ou Código Admin",
                        fontSize = 11.sp,
                        color = Color(0xFF7A7685)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (uiState.textoEntrada.isEmpty()) {
                            Text(
                                text = "Digite para continuar",
                                fontSize = 15.sp,
                                color = Color(0xFF9E9AA8)
                            )
                        }
                        BasicTextField(
                            value = uiState.textoEntrada,
                            onValueChange = onTextoChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1E24)
                            ),
                            cursorBrush = SolidColor(RoxoLogin),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        uiState.errorMessage?.let { erro ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = erro,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = VermelhoErroLogin,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClicarJogar,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoxoLogin,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Jogar (Modo Jogador)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onClicarAdmin,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF3E3486)
            ),
            border = BorderStroke(1.dp, Color(0xFFDFDAE8))
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = Color(0xFF3E3486),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Painel Administrador",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E3486)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Treine sua memória, uma cor por vez.",
            fontSize = 11.sp,
            color = Color(0xFF9E9AA8),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
