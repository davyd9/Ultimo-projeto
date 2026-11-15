package com.example.myapplication.ui.viewmodel

data class DesafioItemUi(
    val id: String,
    val titulo: String,
    val descricao: String,
    val dificuldade: String,
    val recordePts: Int,
    val tamanhoSequencia: Int,
    val velocidadeMs: Long
)

data class DesafiosUiState(
    val desafios: List<DesafioItemUi> = emptyList(),
    val energia: Int = 5,
    val conectadoNuvem: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
