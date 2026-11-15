package com.example.myapplication.ui.viewmodel

import com.example.myapplication.data.model.DesafioEntity

data class SimonUiState(
    val nomeJogador: String = "Marina",
    val pontos: Int = 120,
    val rodada: Int = 5,
    val corAcesa: Int = -1,
    val reproduzindo: Boolean = false,
    val aguardandoJogador: Boolean = false,
    val jogoFinalizado: Boolean = false,
    val mensagemStatus: String = "Pronto para uma nova rodada?",
    val modoAtualId: String? = null,
    val modoAtualTitulo: String = "Primeiros Passos",
    val modoAtualDificuldade: String = "Fácil",
    val modoAtualDetalhe: String = "4 cores na sequência",
    val velocidadeIntervaloMs: Long = 600L,
    val modosDisponiveis: List<DesafioEntity> = emptyList(),
    val errorMessage: String? = null
)
