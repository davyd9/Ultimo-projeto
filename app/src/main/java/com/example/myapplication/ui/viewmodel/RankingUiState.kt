package com.example.myapplication.ui.viewmodel

import com.example.myapplication.data.model.RankingEntity

data class RankingJogadorUi(
    val id: Int,
    val posicao: Int,
    val inicial: String,
    val apelido: String,
    val dificuldade: String,
    val velocidadePadrao: String,
    val pontos: Int
) {
    fun toEntity(): RankingEntity {
        return RankingEntity(
            id = id,
            apelido = apelido,
            dificuldade = dificuldade,
            velocidadePadrao = velocidadePadrao,
            pontos = pontos
        )
    }
}

data class RankingUiState(
    val jogadores: List<RankingJogadorUi> = emptyList(),
    val filtroAtivo: String = "Todos",
    val ordenacaoDecrescente: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
