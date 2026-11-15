package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        observarJogadorAtivo()
    }

    private fun observarJogadorAtivo() {
        viewModelScope.launch {
            rankingRepository.popularDadosIniciaisSeVazio()

            combine(
                rankingRepository.nomeJogadorEscolhido,
                rankingRepository.jogadorAtivoId,
                rankingRepository.observarRanking()
            ) { nomeEscolhido, idAtivo, listaRanking ->
                val registroAtivo = listaRanking.find { it.id == idAtivo }
                if (nomeEscolhido != null) {
                    registroAtivo?.apelido ?: nomeEscolhido
                } else {
                    null
                }
            }.collect { nomeSincronizado ->
                if (nomeSincronizado != null) {
                    _uiState.update { atual ->
                        atual.copy(
                            nomeEscolhido = nomeSincronizado,
                            textoEntrada = if (!atual.editandoNomeOuCodigo) nomeSincronizado else atual.textoEntrada
                        )
                    }
                }
            }
        }
    }

    fun atualizarTextoEntrada(novoTexto: String) {
        _uiState.update { it.copy(textoEntrada = novoTexto, errorMessage = null) }
    }

    fun habilitarEdicaoNomeOuCodigo() {
        _uiState.update {
            it.copy(
                editandoNomeOuCodigo = true,
                textoEntrada = it.nomeEscolhido ?: "",
                errorMessage = null
            )
        }
    }

    fun cancelarEdicaoNome() {
        val salvo = _uiState.value.nomeEscolhido ?: return
        _uiState.update {
            it.copy(
                editandoNomeOuCodigo = false,
                textoEntrada = salvo,
                errorMessage = null
            )
        }
    }

    fun entrarModoJogador(onNavegarJogador: (String) -> Unit, onNavegarAdmin: () -> Unit) {
        val estado = _uiState.value

        if (!estado.editandoNomeOuCodigo && !estado.nomeEscolhido.isNullOrBlank()) {
            onNavegarJogador(estado.nomeEscolhido)
            return
        }

        val entrada = estado.textoEntrada.trim()

        if (entrada.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Escolha um nome de jogador para continuar.")
            }
            return
        }

        if (entrada.equals("admin", ignoreCase = true) || entrada == "1234") {
            _uiState.update {
                it.copy(
                    isAdminAutenticado = true,
                    editandoNomeOuCodigo = estado.nomeEscolhido == null,
                    textoEntrada = estado.nomeEscolhido ?: "",
                    errorMessage = null
                )
            }
            onNavegarAdmin()
            return
        }

        viewModelScope.launch {
            val resultado = rankingRepository.confirmarOuAtualizarJogadorNoLogin(entrada)
            resultado.onSuccess { entidadeSalva ->
                _uiState.update {
                    it.copy(
                        nomeEscolhido = entidadeSalva.apelido,
                        textoEntrada = entidadeSalva.apelido,
                        editandoNomeOuCodigo = false,
                        isAdminAutenticado = false,
                        errorMessage = null
                    )
                }
                onNavegarJogador(entidadeSalva.apelido)
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message) }
            }
        }
    }

    fun entrarPainelAdmin(onNavegarAdmin: () -> Unit) {
        _uiState.update {
            it.copy(
                isAdminAutenticado = true,
                errorMessage = null
            )
        }
        onNavegarAdmin()
    }
}
