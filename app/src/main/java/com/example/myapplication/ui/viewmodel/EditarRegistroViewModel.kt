package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.RankingEntity
import com.example.myapplication.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditarRegistroViewModel(
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarRegistroUiState())
    val uiState: StateFlow<EditarRegistroUiState> = _uiState.asStateFlow()

    fun carregarJogador(jogadorId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            rankingRepository.popularDadosIniciaisSeVazio()

            val todos = rankingRepository.observarRanking().firstOrNull() ?: emptyList()
            val ordenados = todos.sortedByDescending { it.pontos }

            val jogadorAtivo = rankingRepository.obterJogadorAtivoOuPadrao().getOrNull()
            val encontrado = if (jogadorId > 0) {
                ordenados.find { it.id == jogadorId }
            } else {
                (if (jogadorAtivo != null) ordenados.find { it.id == jogadorAtivo.id } else null)
                    ?: ordenados.find { it.apelido.equals("Marina", ignoreCase = true) }
                    ?: ordenados.firstOrNull()
            }

            if (encontrado != null) {
                val pos = ordenados.indexOfFirst { it.id == encontrado.id } + 1
                val (tituloInfo, subInfo, dif) = obterInfoVelocidade(encontrado.velocidadePadrao)
                _uiState.update {
                    it.copy(
                        id = encontrado.id,
                        posicao = if (pos > 0) pos else 4,
                        apelido = encontrado.apelido,
                        velocidadePadrao = encontrado.velocidadePadrao,
                        dificuldade = dif,
                        pontos = encontrado.pontos,
                        tituloVelocidadeInfo = tituloInfo,
                        subtituloVelocidadeInfo = subInfo,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun atualizarApelido(novoApelido: String) {
        _uiState.update { it.copy(apelido = novoApelido, errorMessage = null) }
    }

    fun atualizarVelocidadePadrao(novaVelocidade: String) {
        val (tituloInfo, subInfo, novaDif) = obterInfoVelocidade(novaVelocidade)
        _uiState.update {
            it.copy(
                velocidadePadrao = novaVelocidade,
                dificuldade = novaDif,
                tituloVelocidadeInfo = tituloInfo,
                subtituloVelocidadeInfo = subInfo,
                errorMessage = null
            )
        }
    }

    fun salvarAlteracoes(onConcluido: () -> Unit) {
        viewModelScope.launch {
            val estado = _uiState.value
            val apelidoLimpo = estado.apelido.trim().ifBlank { "Marina" }
            val (_, _, dificuldadeExtraida) = obterInfoVelocidade(estado.velocidadePadrao)

            val entidade = RankingEntity(
                id = estado.id,
                apelido = apelidoLimpo,
                dificuldade = dificuldadeExtraida,
                velocidadePadrao = estado.velocidadePadrao,
                pontos = estado.pontos
            )

            val resultado = if (estado.id == 0) {
                rankingRepository.inserirRegistro(entidade)
            } else {
                rankingRepository.atualizarRegistro(entidade)
            }

            resultado.onSuccess {
                onConcluido()
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    fun excluirRegistroAtual(onConcluido: () -> Unit) {
        viewModelScope.launch {
            val estado = _uiState.value
            if (estado.id == 0) {
                onConcluido()
                return@launch
            }
            val entidade = RankingEntity(
                id = estado.id,
                apelido = estado.apelido,
                dificuldade = estado.dificuldade,
                velocidadePadrao = estado.velocidadePadrao,
                pontos = estado.pontos
            )
            val resultado = rankingRepository.deletarRegistro(entidade)
            resultado.onSuccess {
                onConcluido()
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    companion object {
        val opcoesVelocidade = listOf(
            "Lenta • Fácil",
            "Média • Médio",
            "Rápida • Difícil"
        )

        fun obterInfoVelocidade(velocidadePadrao: String): Triple<String, String, String> {
            return when {
                velocidadePadrao.contains("Rápida", ignoreCase = true) ||
                    velocidadePadrao.contains("Difícil", ignoreCase = true) -> Triple(
                    "Velocidade rápida",
                    "Intervalo de 0,6s entre as cores",
                    "Difícil"
                )
                velocidadePadrao.contains("Média", ignoreCase = true) ||
                    velocidadePadrao.contains("Médio", ignoreCase = true) -> Triple(
                    "Velocidade média",
                    "Intervalo de 1,0s entre as cores",
                    "Médio"
                )
                else -> Triple(
                    "Velocidade média",
                    "Intervalo de 1,2s entre as cores",
                    "Fácil"
                )
            }
        }
    }
}
