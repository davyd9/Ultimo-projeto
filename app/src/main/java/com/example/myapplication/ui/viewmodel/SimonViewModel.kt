package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.data.repository.DesafioRepository
import com.example.myapplication.data.repository.RankingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class SimonViewModel(
    private val rankingRepository: RankingRepository,
    private val desafioRepository: DesafioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimonUiState())
    val uiState: StateFlow<SimonUiState> = _uiState.asStateFlow()

    private val sequenciaAtual = mutableListOf<Int>()
    private var sequenciaAlvoDesafio: List<Int> = emptyList()
    private var indiceJogador = 0

    init {
        observarDadosConectados()
    }

    private fun observarDadosConectados() {
        viewModelScope.launch {
            rankingRepository.popularDadosIniciaisSeVazio()
            desafioRepository.inicializarESincronizarDesafios()

            combine(
                rankingRepository.observarRanking(),
                rankingRepository.jogadorAtivoId,
                desafioRepository.observarDesafios(),
                desafioRepository.desafioAtivoId
            ) { listaRanking, jogadorAtivoId, listaDesafios, desafioAtivoId ->
                val jogadorAtivo = listaRanking.find { it.id == jogadorAtivoId }
                    ?: listaRanking.find { it.apelido.equals("Marina", ignoreCase = true) }
                    ?: listaRanking.firstOrNull()

                val desafiosOrdenados = listaDesafios.sortedBy { d ->
                    when (d.dificuldade.lowercase()) {
                        "difícil", "dificil" -> 1
                        "médio", "medio" -> 2
                        else -> 3
                    }
                }

                val desafioSelecionado = if (desafioAtivoId != null) {
                    desafiosOrdenados.find { it.id == desafioAtivoId }
                } else {
                    val difJogador = jogadorAtivo?.dificuldade ?: "Fácil"
                    desafiosOrdenados.find { it.dificuldade.equals(difJogador, ignoreCase = true) }
                        ?: desafiosOrdenados.firstOrNull()
                }

                Triple(jogadorAtivo, desafiosOrdenados, desafioSelecionado)
            }.collect { (jogadorAtivo, desafiosOrdenados, desafioSelecionado) ->
                val nomeJogador = jogadorAtivo?.apelido ?: "Marina"
                val difJogador = jogadorAtivo?.dificuldade ?: "Fácil"
                val velPadraoJogador = jogadorAtivo?.velocidadePadrao ?: "Lenta • Fácil"

                if (desafioSelecionado != null) {
                    val coresDesafio = converterNomesCoresParaIndices(desafioSelecionado.sequenciaCores)
                    sequenciaAlvoDesafio = coresDesafio
                    val qtd = if (coresDesafio.isNotEmpty()) coresDesafio.size else 4

                    val intervaloMs = when {
                        velPadraoJogador.contains("Rápida", ignoreCase = true) ||
                            desafioSelecionado.dificuldade.equals("Difícil", ignoreCase = true) -> 420L
                        velPadraoJogador.contains("Média", ignoreCase = true) ||
                            desafioSelecionado.dificuldade.equals("Médio", ignoreCase = true) -> 550L
                        else -> 750L
                    }

                    _uiState.update { atual ->
                        atual.copy(
                            nomeJogador = nomeJogador,
                            modoAtualId = desafioSelecionado.id,
                            modoAtualTitulo = desafioSelecionado.titulo,
                            modoAtualDificuldade = desafioSelecionado.dificuldade,
                            modoAtualDetalhe = "$qtd cores • Jogador: $nomeJogador",
                            velocidadeIntervaloMs = intervaloMs,
                            modosDisponiveis = desafiosOrdenados
                        )
                    }
                } else {
                    sequenciaAlvoDesafio = emptyList()
                    _uiState.update { atual ->
                        atual.copy(
                            nomeJogador = nomeJogador,
                            modoAtualId = null,
                            modoAtualTitulo = "Modo Padrão",
                            modoAtualDificuldade = difJogador,
                            modoAtualDetalhe = "$velPadraoJogador • Jogador: $nomeJogador",
                            modosDisponiveis = desafiosOrdenados
                        )
                    }
                }
            }
        }
    }

    fun definirNomeJogador(nome: String) {
        if (nome.isNotBlank()) {
            _uiState.update { it.copy(nomeJogador = nome) }
        }
    }

    fun selecionarModoJogo(desafio: DesafioEntity) {
        desafioRepository.selecionarDesafioParaJogar(desafio.id)
    }

    fun iniciarSequencia() {
        if (_uiState.value.reproduzindo || _uiState.value.aguardandoJogador) return

        viewModelScope.launch {
            sequenciaAtual.clear()
            indiceJogador = 0

            val primeiraCor = sequenciaAlvoDesafio.firstOrNull() ?: Random.nextInt(4)
            sequenciaAtual.add(primeiraCor)

            _uiState.update {
                it.copy(
                    pontos = 0,
                    rodada = 1,
                    jogoFinalizado = false,
                    errorMessage = null
                )
            }

            reproduzirSequenciaAtual()
        }
    }

    private suspend fun reproduzirSequenciaAtual() {
        indiceJogador = 0
        val intervalo = _uiState.value.velocidadeIntervaloMs

        _uiState.update {
            it.copy(
                reproduzindo = true,
                aguardandoJogador = false,
                mensagemStatus = "Observe a sequência!"
            )
        }

        delay(450)

        for (cor in sequenciaAtual) {
            _uiState.update { it.copy(corAcesa = cor) }
            delay(intervalo)
            _uiState.update { it.copy(corAcesa = -1) }
            delay(220)
        }

        _uiState.update {
            it.copy(
                reproduzindo = false,
                aguardandoJogador = true,
                mensagemStatus = "Sua vez! Repita a sequência."
            )
        }
    }

    fun selecionarCor(indiceCor: Int) {
        val estado = _uiState.value
        if (!estado.aguardandoJogador || estado.reproduzindo) return

        viewModelScope.launch {
            _uiState.update { it.copy(corAcesa = indiceCor) }
            delay(180)
            _uiState.update { it.copy(corAcesa = -1) }

            if (sequenciaAtual.getOrNull(indiceJogador) != indiceCor) {
                val pontosFinais = _uiState.value.pontos
                _uiState.update {
                    it.copy(
                        aguardandoJogador = false,
                        jogoFinalizado = true,
                        mensagemStatus = "Você errou! Tente novamente."
                    )
                }

                if (pontosFinais > 0) {
                    atualizarDadosAoPontuar(pontosFinais)
                }
                return@launch
            }

            indiceJogador++

            if (indiceJogador == sequenciaAtual.size) {
                val novosPontos = _uiState.value.pontos + 40
                val novaRodada = _uiState.value.rodada + 1

                _uiState.update {
                    it.copy(
                        aguardandoJogador = false,
                        pontos = novosPontos,
                        rodada = novaRodada,
                        mensagemStatus = "Muito bem! Próxima rodada..."
                    )
                }

                atualizarDadosAoPontuar(novosPontos)

                delay(650)
                val proximaCor = sequenciaAlvoDesafio.getOrNull(sequenciaAtual.size)
                    ?: Random.nextInt(4)
                sequenciaAtual.add(proximaCor)
                reproduzirSequenciaAtual()
            }
        }
    }

    private suspend fun atualizarDadosAoPontuar(pontosObtidos: Int) {
        val estado = _uiState.value
        val resRanking = rankingRepository.atualizarPontuacaoJogadorAtivo(
            novosPontos = pontosObtidos,
            dificuldadePartida = estado.modoAtualDificuldade
        )
        resRanking.onFailure { erro ->
            _uiState.update { it.copy(errorMessage = erro.message) }
        }

        val idDesafio = estado.modoAtualId
        val desafioAtual = estado.modosDisponiveis.find { it.id == idDesafio }
        if (desafioAtual != null && pontosObtidos > desafioAtual.recordePts) {
            desafioRepository.atualizarDesafio(
                desafioAtual.copy(recordePts = pontosObtidos)
            )
        }
    }

    private fun converterNomesCoresParaIndices(sequenciaTexto: String): List<Int> {
        return sequenciaTexto
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .map { nomeCor ->
                when {
                    nomeCor.contains("vermelh") -> 1
                    nomeCor.contains("amarel") -> 2
                    nomeCor.contains("azul") -> 3
                    else -> 0
                }
            }
    }
}
