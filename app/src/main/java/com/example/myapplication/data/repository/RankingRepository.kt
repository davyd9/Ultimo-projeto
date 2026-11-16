package com.example.myapplication.data.repository

import com.example.myapplication.data.local.RankingDao
import com.example.myapplication.data.model.RankingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RankingRepository(
    private val rankingDao: RankingDao
) {

    private val _jogadorAtivoId = MutableStateFlow<Int?>(null)
    val jogadorAtivoId: StateFlow<Int?> = _jogadorAtivoId.asStateFlow()

    private val _nomeJogadorEscolhido = MutableStateFlow<String?>(null)
    val nomeJogadorEscolhido: StateFlow<String?> = _nomeJogadorEscolhido.asStateFlow()

    fun limparJogadorAtivo() {
        _jogadorAtivoId.value = null
        _nomeJogadorEscolhido.value = null
    }

    fun observarRanking(onError: (String) -> Unit = {}): Flow<List<RankingEntity>> {
        return try {
            rankingDao.listarTodos().catch { exception ->
                onError("Erro ao carregar o ranking local: ${exception.localizedMessage ?: "Falha desconhecida"}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            onError("Erro ao inicializar leitura do ranking: ${e.localizedMessage ?: "Falha desconhecida"}")
            flow { emit(emptyList()) }
        }
    }

    suspend fun popularDadosIniciaisSeVazio(): Result<Unit> {
        return try {
            if (rankingDao.contarRegistros() == 0) {
                rankingDao.inserirTodos(obterRegistrosIniciaisPadrao())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao popular dados iniciais no Room: ${e.localizedMessage}", e))
        }
    }

    suspend fun confirmarOuAtualizarJogadorNoLogin(nomeDigitado: String): Result<RankingEntity> {
        return try {
            popularDadosIniciaisSeVazio()
            val nomeLimpo = nomeDigitado.trim()

            val existenteMesmoNome = rankingDao.obterPorApelido(nomeLimpo)
            if (existenteMesmoNome != null && _nomeJogadorEscolhido.value == null) {
                _jogadorAtivoId.value = existenteMesmoNome.id
                _nomeJogadorEscolhido.value = existenteMesmoNome.apelido
                return Result.success(existenteMesmoNome)
            }

            val idAtivo = _jogadorAtivoId.value
            val registroBase = if (idAtivo != null) {
                rankingDao.obterPorId(idAtivo)
            } else {
                rankingDao.obterPorApelido("Marina")
            }

            if (registroBase != null) {
                val atualizado = registroBase.copy(apelido = nomeLimpo)
                rankingDao.atualizar(atualizado)
                _jogadorAtivoId.value = atualizado.id
                _nomeJogadorEscolhido.value = atualizado.apelido
                Result.success(atualizado)
            } else {
                val novo = RankingEntity(
                    apelido = nomeLimpo,
                    dificuldade = "Fácil",
                    velocidadePadrao = "Lenta • Fácil",
                    pontos = 650
                )
                val novoId = rankingDao.inserir(novo).toInt()
                val salvo = novo.copy(id = novoId)
                _jogadorAtivoId.value = novoId
                _nomeJogadorEscolhido.value = salvo.apelido
                Result.success(salvo)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao salvar nome do jogador: ${e.localizedMessage}", e))
        }
    }

    suspend fun obterJogadorPorId(id: Int): Result<RankingEntity?> {
        return try {
            val jogador = rankingDao.obterPorId(id)
            Result.success(jogador)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar jogador no banco: ${e.localizedMessage}", e))
        }
    }

    suspend fun obterJogadorAtivoOuPadrao(): Result<RankingEntity?> {
        return try {
            popularDadosIniciaisSeVazio()
            val idAtivo = _jogadorAtivoId.value
            val jogador = (if (idAtivo != null) rankingDao.obterPorId(idAtivo) else null)
                ?: rankingDao.obterPorApelido("Marina")
                ?: rankingDao.listarTodosSnapshot().firstOrNull()
            if (jogador != null && _jogadorAtivoId.value == null && _nomeJogadorEscolhido.value != null) {
                _jogadorAtivoId.value = jogador.id
            }
            Result.success(jogador)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar jogador ativo no banco: ${e.localizedMessage}", e))
        }
    }

    suspend fun inserirRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            val idGerado = rankingDao.inserir(registro).toInt()
            if (_jogadorAtivoId.value == null) {
                _jogadorAtivoId.value = idGerado
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao inserir pontuação no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun atualizarPontuacaoJogadorAtivo(novosPontos: Int, dificuldadePartida: String? = null): Result<Unit> {
        return try {
            val atual = obterJogadorAtivoOuPadrao().getOrNull()
            if (atual != null) {
                val pontosAtualizados = maxOf(atual.pontos, novosPontos)
                val difAtualizada = dificuldadePartida ?: atual.dificuldade
                val velAtualizada = when (difAtualizada.lowercase()) {
                    "difícil", "dificil" -> "Rápida • Difícil"
                    "médio", "medio" -> "Média • Médio"
                    else -> "Lenta • Fácil"
                }
                val registroAtualizado = atual.copy(
                    pontos = pontosAtualizados,
                    dificuldade = difAtualizada,
                    velocidadePadrao = velAtualizada
                )
                rankingDao.atualizar(registroAtualizado)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao atualizar pontuação do jogador: ${e.localizedMessage}", e))
        }
    }

    suspend fun atualizarRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            rankingDao.atualizar(registro)
            if (_jogadorAtivoId.value == null || _jogadorAtivoId.value == registro.id) {
                _jogadorAtivoId.value = registro.id
                _nomeJogadorEscolhido.value = registro.apelido
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao atualizar registro no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun deletarRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            rankingDao.deletar(registro)
            if (_jogadorAtivoId.value == registro.id || _nomeJogadorEscolhido.value.equals(registro.apelido, ignoreCase = true)) {
                limparJogadorAtivo()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao excluir registro do banco de dados: ${e.localizedMessage}", e))
        }
    }

    companion object {
        fun obterRegistrosIniciaisPadrao(): List<RankingEntity> {
            return listOf(
                RankingEntity(
                    apelido = "Lucas M.",
                    dificuldade = "Difícil",
                    velocidadePadrao = "Rápida • Difícil",
                    pontos = 980
                ),
                RankingEntity(
                    apelido = "Ana Clara",
                    dificuldade = "Difícil",
                    velocidadePadrao = "Rápida • Difícil",
                    pontos = 840
                ),
                RankingEntity(
                    apelido = "Rafael S.",
                    dificuldade = "Fácil",
                    velocidadePadrao = "Lenta • Fácil",
                    pontos = 720
                ),
                RankingEntity(
                    apelido = "Marina",
                    dificuldade = "Fácil",
                    velocidadePadrao = "Lenta • Fácil",
                    pontos = 650
                ),
                RankingEntity(
                    apelido = "João P.",
                    dificuldade = "Difícil",
                    velocidadePadrao = "Rápida • Difícil",
                    pontos = 520
                )
            )
        }
    }
}
