package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Roxo = Color(0xFF5145AA)
private val Fundo = Color(0xFFFCF9FF)
private val Texto = Color(0xFF302966)

@Composable
fun LoginScreen(
    onJogar: (String) -> Unit,
    onAdmin: () -> Unit
) {
    var nome by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(90.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_simon),
            contentDescription = "Logo Simon Master",
            modifier = Modifier.size(170.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Simon Master",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Texto
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Observe, memorize e repita a sequência.",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nome do Jogador ou Código Admin")
            },
            placeholder = {
                Text("Digite para continuar")
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Roxo,
                focusedLabelColor = Roxo,
                cursorColor = Roxo
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                onJogar(nome.trim().ifBlank { "Jogador" })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Roxo,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "▷   Jogar (Modo Jogador)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onAdmin,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Roxo
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFFD5CDE0)
            )
        ) {
            Text(
                text = "♙   Painel Administrador",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Roxo
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Treine sua memória, uma cor por vez.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
