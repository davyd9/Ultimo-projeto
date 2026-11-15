package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.RankingEntity
import com.example.myapplication.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState(isLoading = true))
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private var cacheEntidades: List<RankingEntity> = emptyList()

    init {
        inicializarEObservarRanking()
    }

    private fun inicializarEObservarRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val seedResult = rankingRepository.popularDadosIniciaisSeVazio()
            seedResult.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }

            rankingRepository.observarRanking(
                onError = { msgErro ->
                    _uiState.update { it.copy(errorMessage = msgErro, isLoading = false) }
                }
            ).collect { entidades ->
                cacheEntidades = entidades
                aplicarFiltrosEOrdenacao()
            }
        }
    }

    private fun aplicarFiltrosEOrdenacao() {
        val estadoAtual = _uiState.value
        val filtro = estadoAtual.filtroAtivo
        val decrescente = estadoAtual.ordenacaoDecrescente

        val filtrados = cacheEntidades.filter { jogador ->
            val dif = jogador.dificuldade.trim()
            when (filtro) {
                "Fácil" -> dif.equals("Fácil", ignoreCase = true) || dif.equals("Facil", ignoreCase = true)
                "Médio" -> dif.equals("Médio", ignoreCase = true) || dif.equals("Medio", ignoreCase = true)
                "Difícil" -> dif.equals("Difícil", ignoreCase = true) || dif.equals("Dificil", ignoreCase = true)
                else -> true
            }
        }

        val ordenados = if (decrescente) {
            filtrados.sortedByDescending { jogador -> jogador.pontos }
        } else {
            filtrados.sortedBy { jogador -> jogador.pontos }
        }

        var posicaoCounter = 0
        val listaProcessada = ordenados.map { entidade ->
            posicaoCounter++
            val inicial = entidade.apelido
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "J"

            RankingJogadorUi(
                id = entidade.id,
                posicao = posicaoCounter,
                inicial = inicial,
                apelido = entidade.apelido,
                dificuldade = entidade.dificuldade,
                velocidadePadrao = entidade.velocidadePadrao,
                pontos = entidade.pontos
            )
        }

        _uiState.update {
            it.copy(
                jogadores = listaProcessada,
                isLoading = false
            )
        }
    }

    fun selecionarFiltro(novoFiltro: String) {
        _uiState.update { it.copy(filtroAtivo = novoFiltro) }
        aplicarFiltrosEOrdenacao()
    }

    fun alternarOrdenacaoPontos() {
        _uiState.update { it.copy(ordenacaoDecrescente = !it.ordenacaoDecrescente) }
        aplicarFiltrosEOrdenacao()
    }

    fun excluirJogador(jogador: RankingJogadorUi) {
        viewModelScope.launch {
            val resultado = rankingRepository.deletarRegistro(jogador.toEntity())
            resultado.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }
}
