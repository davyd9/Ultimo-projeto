package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.RankingEntity
import com.example.myapplication.data.logic.VelocidadeConfigHelper
import com.example.myapplication.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState(isLoading = true))
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private var cacheEntidades: List<RankingEntity> = emptyList()

    init {
        inicializarDadosEObservarRanking()
    }

    private fun inicializarDadosEObservarRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val seedResult = repository.popularDadosIniciaisSeVazio()
            seedResult.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }

            repository.observarRanking(
                onError = { mensagemErro ->
                    _uiState.update { it.copy(errorMessage = mensagemErro, isLoading = false) }
                }
            ).collect { listaEntidades ->
                cacheEntidades = listaEntidades
                aplicarFiltrosEOrdenacao()
            }
        }
    }

    private fun aplicarFiltrosEOrdenacao() {
        val estadoAtual = _uiState.value
        val filtro = estadoAtual.filtroAtivo
        val decrescente = estadoAtual.ordenacaoDecrescente

        // Ranking geral ordenado por pontos para referência de posição global
        val rankingGeralOrdenado = cacheEntidades
            .sortedByDescending { entidade -> entidade.pontos }

        var posicaoGlobalCounter = 0
        val todosMapeados = rankingGeralOrdenado.map { entidade ->
            posicaoGlobalCounter++
            val inicialNome = entidade.apelido
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "J"

            RankingJogadorUi(
                id = entidade.id,
                posicao = posicaoGlobalCounter,
                inicial = inicialNome,
                apelido = entidade.apelido,
                dificuldade = entidade.dificuldade,
                velocidadePadrao = entidade.velocidadePadrao,
                pontos = entidade.pontos
            )
        }

        // Aplicação obrigatória de .filter, .sortedByDescending / .sortedBy e .map com lambdas
        val filtrados = cacheEntidades.filter { jogador ->
            when (filtro) {
                "Fácil" -> jogador.dificuldade.equals("Fácil", ignoreCase = true)
                "Difícil" -> jogador.dificuldade.equals("Difícil", ignoreCase = true)
                else -> true
            }
        }

        val ordenados = if (decrescente) {
            filtrados.sortedByDescending { jogador -> jogador.pontos }
        } else {
            filtrados.sortedBy { jogador -> jogador.pontos }
        }

        var contadorPosicao = 0
        val listaProcessada = ordenados.map { entidade ->
            contadorPosicao++
            val inicial = entidade.apelido
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "J"

            RankingJogadorUi(
                id = entidade.id,
                posicao = contadorPosicao,
                inicial = inicial,
                apelido = entidade.apelido,
                dificuldade = entidade.dificuldade,
                velocidadePadrao = entidade.velocidadePadrao,
                pontos = entidade.pontos
            )
        }

        val selecionadoAtualizado = estadoAtual.jogadorSelecionado?.let { atual ->
            todosMapeados.find { it.id == atual.id }
        } ?: todosMapeados.find { it.apelido.equals("Marina", ignoreCase = true) }
            ?: todosMapeados.firstOrNull()

        _uiState.update {
            it.copy(
                jogadores = listaProcessada,
                todosJogadores = todosMapeados,
                jogadorSelecionado = selecionadoAtualizado,
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

    fun selecionarJogadorParaEdicao(jogador: RankingJogadorUi) {
        val noRankingGeral = _uiState.value.todosJogadores.find { it.id == jogador.id } ?: jogador
        _uiState.update { it.copy(jogadorSelecionado = noRankingGeral) }
    }

    fun selecionarJogadorPadraoOuUltimo(apelidoPadrao: String = "Marina") {
        val todos = _uiState.value.todosJogadores
        val atual = _uiState.value.jogadorSelecionado
        val escolhido = if (atual != null && todos.any { it.id == atual.id }) {
            todos.first { it.id == atual.id }
        } else {
            todos.find { it.apelido.equals(apelidoPadrao, ignoreCase = true) }
                ?: todos.firstOrNull()
                ?: RankingJogadorUi(
                    id = 0,
                    posicao = 4,
                    inicial = "M",
                    apelido = "Marina",
                    dificuldade = "Fácil",
                    velocidadePadrao = "Lenta • Fácil",
                    pontos = 650
                )
        }
        _uiState.update { it.copy(jogadorSelecionado = escolhido) }
    }

    fun adicionarPontuacao(
        apelido: String,
        pontos: Int,
        velocidadePadrao: String = "Lenta • Fácil"
    ) {
        viewModelScope.launch {
            val nomeLimpo = apelido.trim().ifBlank { "Jogador" }
            val dificuldade = VelocidadeConfigHelper.extrairDificuldade(velocidadePadrao)
            val novoRegistro = RankingEntity(
                apelido = nomeLimpo,
                dificuldade = dificuldade,
                velocidadePadrao = velocidadePadrao,
                pontos = pontos
            )
            val resultado = repository.inserirRegistro(novoRegistro)
            resultado.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    fun salvarAlteracoesJogador(
        id: Int,
        novoApelido: String,
        novaVelocidadePadrao: String,
        pontosAtuais: Int
    ) {
        viewModelScope.launch {
            val apelidoValidado = novoApelido.trim().ifBlank { "Jogador" }
            val novaDificuldade = VelocidadeConfigHelper.extrairDificuldade(novaVelocidadePadrao)

            val entidadeAtualizada = RankingEntity(
                id = id,
                apelido = apelidoValidado,
                dificuldade = novaDificuldade,
                velocidadePadrao = novaVelocidadePadrao,
                pontos = pontosAtuais
            )

            val resultado = if (id == 0) {
                repository.inserirRegistro(entidadeAtualizada)
            } else {
                repository.atualizarRegistro(entidadeAtualizada)
            }

            resultado.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    fun excluirJogador(jogador: RankingJogadorUi) {
        viewModelScope.launch {
            val resultado = repository.deletarRegistro(jogador.toEntity())
            resultado.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
