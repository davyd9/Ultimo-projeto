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

class AdminViewModel(
    private val desafioRepository: DesafioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState(isLoading = true))
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        inicializarEObservarDesafios()
    }

    private fun inicializarEObservarDesafios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val initResult = desafioRepository.inicializarESincronizarDesafios()
            initResult.onSuccess { online ->
                _uiState.update { it.copy(isOnline = online) }
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message, isLoading = false) }
            }

            desafioRepository.observarDesafios(
                onError = { msgErro ->
                    _uiState.update { it.copy(errorMessage = msgErro, isLoading = false) }
                }
            ).collect { listaBruta ->
                val processados = listaBruta
                    .filter { desafio -> desafio.titulo.isNotBlank() }
                    .sortedBy { desafio ->
                        when (desafio.dificuldade.lowercase()) {
                            "difícil", "dificil" -> 1
                            "médio", "medio" -> 2
                            else -> 3
                        }
                    }
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

    fun atualizarNomeDesafio(novoNome: String) {
        _uiState.update { it.copy(nomeDesafio = novoNome, errorMessage = null) }
    }

    fun atualizarSequenciaCores(novaSequencia: String) {
        _uiState.update { it.copy(sequenciaCores = novaSequencia, errorMessage = null) }
    }

    fun atualizarDificuldade(novaDificuldade: String) {
        _uiState.update { it.copy(dificuldade = novaDificuldade, errorMessage = null) }
    }

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

    fun excluirDesafio(desafio: DesafioEntity) {
        viewModelScope.launch {
            val resultado = desafioRepository.excluirDesafio(desafio)
            resultado.onSuccess {
                if (_uiState.value.desafioEmEdicaoId == desafio.id) {
                    cancelarEdicao()
                }
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    companion object {
        fun normalizarDificuldade(valor: String): String {
            val limpo = valor.trim().lowercase()
            return when {
                limpo.contains("dif") -> "Difícil"
                limpo.contains("méd") || limpo.contains("med") -> "Médio"
                else -> "Fácil"
            }
        }

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
