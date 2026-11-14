package com.example.myapplication.data.logic

import com.example.myapplication.data.local.PerfilConfigEntity
import com.example.myapplication.data.local.RankingEntity

data class InfoVelocidade(
    val titulo: String,
    val subtitulo: String,
    val dificuldade: String,
    val intervaloMs: Long
)

object VelocidadeConfigHelper {

    val opcoesVelocidade = listOf(
        "Lenta • Fácil",
        "Média • Médio",
        "Rápida • Difícil"
    )

    fun obterInfoVelocidade(velocidadePadrao: String): InfoVelocidade {
        return when {
            velocidadePadrao.contains("Rápida", ignoreCase = true) ||
                velocidadePadrao.contains("Difícil", ignoreCase = true) -> InfoVelocidade(
                titulo = "Velocidade rápida",
                subtitulo = "Intervalo de 0,6s entre as cores",
                dificuldade = "Difícil",
                intervaloMs = 600L
            )
            velocidadePadrao.contains("Média", ignoreCase = true) ||
                velocidadePadrao.contains("Médio", ignoreCase = true) -> InfoVelocidade(
                titulo = "Velocidade média",
                subtitulo = "Intervalo de 1,0s entre as cores",
                dificuldade = "Médio",
                intervaloMs = 1000L
            )
            else -> InfoVelocidade(
                titulo = "Velocidade média",
                subtitulo = "Intervalo de 1,2s entre as cores",
                dificuldade = "Fácil",
                intervaloMs = 1200L
            )
        }
    }

    fun extrairDificuldade(velocidadePadrao: String): String {
        return obterInfoVelocidade(velocidadePadrao).dificuldade
    }

    fun obterRegistrosIniciais(): List<RankingEntity> {
        return listOf(
            RankingEntity(
                apelido = "Lucas M.",
                dificuldade = "Difícil",
                velocidadePadrao = "Rápida • Difícil",
                pontos = 980
            ),
            RankingEntity(
                apelido = "Ana Clara",
                dificuldade = "Difícil",
                velocidadePadrao = "Rápida • Difícil",
                pontos = 840
            ),
            RankingEntity(
                apelido = "Rafael S.",
                dificuldade = "Fácil",
                velocidadePadrao = "Lenta • Fácil",
                pontos = 720
            ),
            RankingEntity(
                apelido = "Marina",
                dificuldade = "Fácil",
                velocidadePadrao = "Lenta • Fácil",
                pontos = 650
            ),
            RankingEntity(
                apelido = "João P.",
                dificuldade = "Difícil",
                velocidadePadrao = "Rápida • Difícil",
                pontos = 520
            )
        )
    }

    fun obterPerfilPadraoInicial(): PerfilConfigEntity {
        return PerfilConfigEntity(
            id = 1,
            apelidoPadrao = "Marina",
            velocidadePadrao = "Lenta • Fácil",
            dificuldadePadrao = "Fácil",
            intervaloMs = 1200L
        )
    }
}
