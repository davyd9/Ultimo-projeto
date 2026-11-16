package com.example.myapplication.ui.viewmodel

import com.example.myapplication.data.model.DesafioEntity

data class AdminUiState(
    val nomeDesafio: String = "",
    val sequenciaCores: String = "",
    val dificuldade: String = "",
    val desafioEmEdicaoId: String? = null,
    val desafios: List<DesafioEntity> = emptyList(),
    val isOnline: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val emModoEdicao: Boolean
        get() = desafioEmEdicaoId != null
}
