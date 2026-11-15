package com.example.myapplication.ui.viewmodel

data class EditarRegistroUiState(
    val id: Int = 0,
    val posicao: Int = 4,
    val apelido: String = "Marina",
    val velocidadePadrao: String = "Lenta • Fácil",
    val dificuldade: String = "Fácil",
    val pontos: Int = 650,
    val tituloVelocidadeInfo: String = "Velocidade média",
    val subtituloVelocidadeInfo: String = "Intervalo de 1,2s entre as cores",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val inicial: String
        get() = apelido.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "M"
}
