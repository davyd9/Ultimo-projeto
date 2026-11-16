package com.example.myapplication.ui.viewmodel

import com.example.myapplication.data.model.DesafioEntity

/**
 * Representa o estado imutável (UiState) exclusivo da tela de Administração (AdminScreen).
 * Segue o padrão Unidirectional Data Flow (UDF) na arquitetura MVVM.
 */
data class AdminUiState(
    // Valor atual do campo "Nome do Desafio" no formulário.
    val nomeDesafio: String = "",
    // Valor atual do campo "Sequência de Cores" no formulário.
    val sequenciaCores: String = "",
    // Valor atual do campo "Dificuldade" no formulário.
    val dificuldade: String = "",
    // Armazena o ID do desafio em edição; se for null, o formulário opera em modo de criação ("Novo desafio").
    val desafioEmEdicaoId: String? = null,
    // Lista de desafios cadastrados e sincronizados exibida na LazyColumn.
    val desafios: List<DesafioEntity> = emptyList(),
    // Status da conexão/sincronização com o Firebase Firestore.
    val isOnline: Boolean = true,
    // Indica carregamento ou sincronização em andamento.
    val isLoading: Boolean = false,
    // Mensagem de erro tratada para exibição visual na UI.
    val errorMessage: String? = null
) {
    // Propriedade computada que indica se um desafio existente está carregado para edição no formulário.
    val emModoEdicao: Boolean
        get() = desafioEmEdicaoId != null
}
