package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.data.repository.DesafioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel dedicado ao Painel Administrador (AdminScreen).
 * Gerencia o CRUD completo de desafios sincronizados entre o Room Database e o Firebase Firestore,
 * aplicando programação funcional (lambdas .filter, .sortedBy e .map) sobre o Flow reativo.
 */
class AdminViewModel(
    private val desafioRepository: DesafioRepository
) : ViewModel() {

    // Estado interno mutável encapsulado e exposto publicamente como StateFlow somente-leitura.
    private val _uiState = MutableStateFlow(AdminUiState(isLoading = true))
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        inicializarEObservarDesafios()
    }

    /**
     * Inicializa a sincronização com o Firebase Firestore e observa continuamente o Flow de desafios do Room.
     */
    private fun inicializarEObservarDesafios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Sincroniza dados iniciais entre Room local e coleção "desafios" no Firestore.
            val initResult = desafioRepository.inicializarESincronizarDesafios()
            initResult.onSuccess { online ->
                _uiState.update { it.copy(isOnline = online) }
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message, isLoading = false) }
            }

            // Coleta o fluxo reativo de desafios e aplica pipeline funcional Kotlin (.filter, .sortedBy, .map).
            desafioRepository.observarDesafios(
                onError = { msgErro ->
                    _uiState.update { it.copy(errorMessage = msgErro, isLoading = false) }
                }
            ).collect { listaBruta ->
                val processados = listaBruta
                    // 1. Filtra registros válidos com título preenchido.
                    .filter { desafio -> desafio.titulo.isNotBlank() }
                    // 2. Ordena por prioridade de dificuldade (Difícil -> Médio -> Fácil).
                    .sortedBy { desafio ->
                        when (desafio.dificuldade.lowercase()) {
                            "difícil", "dificil" -> 1
                            "médio", "medio" -> 2
                            else -> 3
                        }
                    }
                    // 3. Higieniza o título e normaliza a nomenclatura da dificuldade.
                    .map { desafio ->
                        desafio.copy(
                            titulo = desafio.titulo.trim(),
                            dificuldade = normalizarDificuldade(desafio.dificuldade)
                        )
                    }

                _uiState.update {
                    it.copy(
                        desafios = processados,
                        isLoading = false
                    )
                }
            }
        }
    }

    // Atualiza o campo "Nome do Desafio" no estado do formulário.
    fun atualizarNomeDesafio(novoNome: String) {
        _uiState.update { it.copy(nomeDesafio = novoNome, errorMessage = null) }
    }

    // Atualiza o campo "Sequência de Cores" no estado do formulário.
    fun atualizarSequenciaCores(novaSequencia: String) {
        _uiState.update { it.copy(sequenciaCores = novaSequencia, errorMessage = null) }
    }

    // Atualiza o campo "Dificuldade" no estado do formulário.
    fun atualizarDificuldade(novaDificuldade: String) {
        _uiState.update { it.copy(dificuldade = novaDificuldade, errorMessage = null) }
    }

    // Carrega os dados de um desafio existente nos campos do formulário para edição.
    fun carregarParaEdicao(desafio: DesafioEntity) {
        _uiState.update {
            it.copy(
                nomeDesafio = desafio.titulo,
                sequenciaCores = desafio.sequenciaCores,
                dificuldade = desafio.dificuldade,
                desafioEmEdicaoId = desafio.id,
                errorMessage = null
            )
        }
    }

    // Limpa os campos do formulário e encerra o modo de edição.
    fun cancelarEdicao() {
        _uiState.update {
            it.copy(
                nomeDesafio = "",
                sequenciaCores = "",
                dificuldade = "",
                desafioEmEdicaoId = null,
                errorMessage = null
            )
        }
    }

    /**
     * Valida os campos do formulário e publica um novo desafio ou salva alterações de um existente
     * no Room Database e no Firebase Firestore.
     */
    fun publicarOuSalvarDesafio() {
        val estado = _uiState.value
        val nomeLimpo = estado.nomeDesafio.trim()

        val coresValidas = estado.sequenciaCores
            .split(",")
            .map { cor -> cor.trim() }
            .filter { cor -> cor.isNotEmpty() }

        if (nomeLimpo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe o nome do desafio.") }
            return
        }

        if (coresValidas.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Informe a sequência de cores separada por vírgula.") }
            return
        }

        val dificuldadeNormalizada = normalizarDificuldade(estado.dificuldade)
        val sequenciaFormatada = coresValidas.joinToString(", ")
        val idEdicao = estado.desafioEmEdicaoId
        val existente = estado.desafios.find { it.id == idEdicao }

        val recordePadrao = existente?.recordePts ?: when (dificuldadeNormalizada) {
            "Difícil" -> 340
            "Médio" -> 220
            else -> 100
        }
        val velocidadePadraoMs = existente?.velocidadeMs ?: when (dificuldadeNormalizada) {
            "Médio" -> 300L
            else -> 600L
        }

        val entidade = DesafioEntity(
            id = idEdicao ?: "desafio_${UUID.randomUUID().toString().take(8)}",
            titulo = nomeLimpo,
            sequenciaCores = sequenciaFormatada,
            dificuldade = dificuldadeNormalizada,
            recordePts = recordePadrao,
            velocidadeMs = velocidadePadraoMs
        )

        viewModelScope.launch {
            val resultado = if (idEdicao == null) {
                desafioRepository.criarDesafio(entidade)
            } else {
                desafioRepository.atualizarDesafio(entidade)
            }

            resultado.onSuccess {
                _uiState.update {
                    it.copy(
                        nomeDesafio = "",
                        sequenciaCores = "",
                        dificuldade = "",
                        desafioEmEdicaoId = null,
                        errorMessage = null
                    )
                }
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    /**
     * Exclui o desafio informado do Room Database e do Firebase Firestore,
     * acionando o callback onConcluido após a remoção para redirecionamento de rota.
     */
    fun excluirDesafio(desafio: DesafioEntity, onConcluido: () -> Unit = {}) {
        viewModelScope.launch {
            val resultado = desafioRepository.excluirDesafio(desafio)
            resultado.onSuccess {
                if (_uiState.value.desafioEmEdicaoId == desafio.id) {
                    cancelarEdicao()
                }
                onConcluido()
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    /**
     * Exclui o desafio atualmente carregado em modo de edição no formulário do Painel Admin
     * e executa o callback onConcluido para retornar à Tela Inicial de Login.
     */
    fun excluirDesafioEmEdicao(onConcluido: () -> Unit = {}) {
        val idEdicao = _uiState.value.desafioEmEdicaoId ?: return
        val desafio = _uiState.value.desafios.find { it.id == idEdicao } ?: DesafioEntity(
            id = idEdicao,
            titulo = _uiState.value.nomeDesafio,
            sequenciaCores = _uiState.value.sequenciaCores,
            dificuldade = normalizarDificuldade(_uiState.value.dificuldade)
        )
        excluirDesafio(desafio, onConcluido)
    }

    companion object {
        // Padroniza qualquer entrada textual de dificuldade para "Difícil", "Médio" ou "Fácil".
        fun normalizarDificuldade(valor: String): String {
            val limpo = valor.trim().lowercase()
            return when {
                limpo.contains("dif") -> "Difícil"
                limpo.contains("méd") || limpo.contains("med") -> "Médio"
                else -> "Fácil"
            }
        }

        // Formata a sequência de cores para exibir as 3 primeiras cores seguidas de "+N" nos cards.
        fun formatarResumoSequencia(sequenciaCores: String): String {
            val cores = sequenciaCores
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            return if (cores.size > 3) {
                val tresPrimeiras = cores.take(3).joinToString(", ")
                val restantes = cores.size - 3
                "$tresPrimeiras +$restantes"
            } else {
                cores.joinToString(", ")
            }
        }
    }
}
