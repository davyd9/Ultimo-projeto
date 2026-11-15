package com.example.myapplication.ui.viewmodel

data class LoginUiState(
    val textoEntrada: String = "",
    val nomeEscolhido: String? = null,
    val editandoNomeOuCodigo: Boolean = true,
    val isAdminAutenticado: Boolean = false,
    val errorMessage: String? = null
)
