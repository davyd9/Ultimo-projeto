package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.DesafioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DesafiosViewModel(
    private val desafioRepository: DesafioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DesafiosUiState(isLoading = true))
    val uiState: StateFlow<DesafiosUiState> = _uiState.asStateFlow()

    init {
        carregarDesafios()
    }

    fun selecionarDesafio(desafioId: String) {
        desafioRepository.selecionarDesafioParaJogar(desafioId)
    }

    private fun carregarDesafios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val syncResult = desafioRepository.inicializarESincronizarDesafios()
            syncResult.onSuccess { conectado ->
                _uiState.update { it.copy(conectadoNuvem = conectado) }
            }.onFailure { erro ->
                _uiState.update { it.copy(errorMessage = erro.message, isLoading = false) }
            }

            desafioRepository.observarDesafios(
                onError = { msgErro ->
                    _uiState.update { it.copy(errorMessage = msgErro, isLoading = false) }
                }
            ).collect { entidades ->
                val mapeados = entidades
                    .filter { it.titulo.isNotBlank() }
                    .sortedBy { desafio ->
                        when (desafio.dificuldade.lowercase()) {
                            "difícil", "dificil" -> 1
                            "médio", "medio" -> 2
                            else -> 3
                        }
                    }
                    .map { entidade ->
                        val cores = entidade.sequenciaCores
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        val qtdCores = if (cores.isNotEmpty()) cores.size else 4

                        val difNormalizada = when (entidade.dificuldade.lowercase()) {
                            "difícil", "dificil" -> "Difícil"
                            "médio", "medio" -> "Médio"
                            else -> "Fácil"
                        }

                        val descricao = if (difNormalizada == "Médio" || entidade.velocidadeMs <= 300L) {
                            "$qtdCores cores • velocidade rápida"
                        } else {
                            "$qtdCores cores na sequência"
                        }

                        DesafioItemUi(
                            id = entidade.id,
                            titulo = entidade.titulo,
                            descricao = descricao,
                            dificuldade = difNormalizada,
                            recordePts = entidade.recordePts,
                            tamanhoSequencia = qtdCores,
                            velocidadeMs = entidade.velocidadeMs
                        )
                    }

                _uiState.update {
                    it.copy(
                        desafios = mapeados,
                        isLoading = false
                    )
                }
            }
        }
    }
}
