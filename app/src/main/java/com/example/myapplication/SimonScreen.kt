
package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val RoxoJogo = Color(0xFF5145AA)
private val FundoJogo = Color(0xFFFCF9FF)

private val CoresSimon = listOf(
    Color(0xFF4DB184),
    Color(0xFFE15F63),
    Color(0xFFEABD4D),
    Color(0xFF5088D5)
)

private val NomesCores = listOf(
    "Verde", "Vermelho", "Amarelo", "Azul"
)

@Composable
fun SimonScreen(nomeJogador: String, onAbaSelecionada: (String) -> Unit = {}) {

    val sequencia = remember { mutableStateListOf<Int>() }
    val scope = rememberCoroutineScope()

    var pontos by remember { mutableIntStateOf(0) }
    var rodada by remember { mutableIntStateOf(1) }
    var indiceJogador by remember { mutableIntStateOf(0) }

    var corAcesa by remember { mutableIntStateOf(-1) }
    var reproduzindo by remember { mutableStateOf(false) }
    var aguardandoJogador by remember { mutableStateOf(false) }
    var jogoFinalizado by remember { mutableStateOf(false) }

    var mensagem by remember {
        mutableStateOf("Pronto para uma nova rodada?")
    }

    var abaSelecionada by remember {
        mutableStateOf("Jogar")
    }

    suspend fun reproduzirSequencia() {

        reproduzindo = true
        aguardandoJogador = false
        indiceJogador = 0

        mensagem = "Observe a sequência!"

        delay(500)

        for (cor in sequencia) {

            corAcesa = cor
            delay(600)

            corAcesa = -1
            delay(300)
        }

        reproduzindo = false
        aguardandoJogador = true

        mensagem = "Sua vez! Repita a sequência."
    }

    fun iniciarJogo() {

        scope.launch {

            sequencia.clear()

            pontos = 0
            rodada = 1
            indiceJogador = 0
            jogoFinalizado = false

            sequencia.add(Random.nextInt(4))

            reproduzirSequencia()
        }
    }

    fun selecionarCor(indice: Int) {

        if (!aguardandoJogador || reproduzindo) return

        if (sequencia[indiceJogador] != indice) {

            aguardandoJogador = false
            jogoFinalizado = true

            mensagem = "Você errou! Tente novamente."

            return
        }

        indiceJogador++

        if (indiceJogador == sequencia.size) {

            aguardandoJogador = false

            pontos += 10
            rodada++

            mensagem = "Muito bem! Próxima rodada..."

            scope.launch {

                delay(800)

                sequencia.add(Random.nextInt(4))

                reproduzirSequencia()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoJogo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo_simon),
                    contentDescription = "Logo Simon Master",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Simon",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoxoJogo
                )
            }

            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFEFEAF7),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("PONTOS", fontSize = 9.sp, color = Color.Gray)

                    Text(
                        "$pontos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoxoJogo
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("RODADA", fontSize = 9.sp, color = Color.Gray)

                    Text(
                        "$rodada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoxoJogo
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = if (jogoFinalizado) {
                "🔴  $mensagem"
            } else if (reproduzindo) {
                "🟡  $mensagem"
            } else {
                "🟢  $mensagem"
            },
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(12.dp, RoundedCornerShape(40.dp))
                .background(
                    Color(0xFFE9E5EF),
                    RoundedCornerShape(40.dp)
                )
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

                    ColorPanel(
                        nome = NomesCores[0],
                        cor = CoresSimon[0],
                        acesa = corAcesa == 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        selecionarCor(0)
                    }

                    ColorPanel(
                        nome = NomesCores[1],
                        cor = CoresSimon[1],
                        acesa = corAcesa == 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        selecionarCor(1)
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ColorPanel(
                        nome = NomesCores[2],
                        cor = CoresSimon[2],
                        acesa = corAcesa == 2,
                        modifier = Modifier.weight(1f)
                    ) {
                        selecionarCor(2)
                    }

                    ColorPanel(
                        nome = NomesCores[3],
                        cor = CoresSimon[3],
                        acesa = corAcesa == 3,
                        modifier = Modifier.weight(1f)
                    ) {
                        selecionarCor(3)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .background(RoxoJogo, CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "ϟ",
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { iniciarJogo() },
            enabled = !reproduzindo && !aguardandoJogador,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoxoJogo,
                contentColor = Color.White,
                disabledContainerColor = RoxoJogo.copy(alpha = 0.6f),
                disabledContentColor = Color.White
            )
        ) {

            Text(
                text = if (jogoFinalizado) {
                    "↻   Jogar Novamente"
                } else {
                    "▷   Iniciar Sequência"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Toque nas cores na mesma ordem apresentada.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF8989F1),
                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                )
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MenuItem("♧", "Jogar", "Jogar") {
                onAbaSelecionada("Jogar")
            }

            MenuItem("ϟ", "Desafios", "Jogar") {
                onAbaSelecionada("Desafios")
            }

            MenuItem("♜", "Ranking", "Jogar") {
                onAbaSelecionada("Ranking")
            }

            MenuItem("⚙", "Opções", "Jogar") {
                onAbaSelecionada("Opções")
            }
        }
    }
}

@Composable
private fun ColorPanel(
    nome: String,
    cor: Color,
    acesa: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                if (acesa) cor.copy(alpha = 0.55f) else cor,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {

        Text(
            text = nome,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MenuItem(
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
                color = if (ativo) RoxoJogo else Color.White
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
