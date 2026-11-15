package com.example.myapplication.data.repository

import com.example.myapplication.data.local.DesafioDao
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.data.remote.FirebaseSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DesafioRepository(
    private val desafioDao: DesafioDao,
    private val firebaseSource: FirebaseSource = FirebaseSource()
) {

    private val _desafioAtivoId = MutableStateFlow<String?>(null)
    val desafioAtivoId: StateFlow<String?> = _desafioAtivoId.asStateFlow()

    fun selecionarDesafioParaJogar(desafioId: String?) {
        _desafioAtivoId.value = desafioId
    }

    fun observarDesafios(onError: (String) -> Unit = {}): Flow<List<DesafioEntity>> {
        return try {
            desafioDao.listarTodos().catch { e ->
                onError("Erro ao observar desafios locais no Room: ${e.localizedMessage ?: "Falha desconhecida"}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            onError("Falha ao inicializar fluxo de desafios: ${e.localizedMessage ?: "Erro desconhecido"}")
            flow { emit(emptyList()) }
        }
    }

    suspend fun inicializarESincronizarDesafios(): Result<Boolean> {
        return try {
            if (desafioDao.contarRegistros() == 0) {
                desafioDao.inserirTodos(obterDesafiosIniciaisPadrao())
            }

            val sincronizadoNuvem = try {
                val remotos = firebaseSource.buscarDesafiosRemotos()
                if (remotos.isNotEmpty()) {
                    desafioDao.inserirTodos(remotos)
                } else {
                    obterDesafiosIniciaisPadrao().forEach { desafio ->
                        firebaseSource.publicarDesafioNoFirebase(desafio)
                    }
                }
                true
            } catch (_: Exception) {
                true
            }

            Result.success(sincronizadoNuvem)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao inicializar desafios no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun criarDesafio(desafio: DesafioEntity): Result<Unit> {
        return try {
            desafioDao.inserir(desafio)
            try {
                firebaseSource.publicarDesafioNoFirebase(desafio)
            } catch (_: Exception) {
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao publicar desafio: ${e.localizedMessage}", e))
        }
    }

    suspend fun atualizarDesafio(desafio: DesafioEntity): Result<Unit> {
        return try {
            desafioDao.atualizar(desafio)
            try {
                firebaseSource.atualizarDesafioNoFirebase(desafio)
            } catch (_: Exception) {
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao atualizar desafio: ${e.localizedMessage}", e))
        }
    }

    suspend fun excluirDesafio(desafio: DesafioEntity): Result<Unit> {
        return try {
            desafioDao.deletar(desafio)
            if (_desafioAtivoId.value == desafio.id) {
                _desafioAtivoId.value = null
            }
            try {
                firebaseSource.excluirDesafioNoFirebase(desafio.id)
            } catch (_: Exception) {
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao excluir desafio: ${e.localizedMessage}", e))
        }
    }

    companion object {
        fun obterDesafiosIniciaisPadrao(): List<DesafioEntity> {
            return listOf(
                DesafioEntity(
                    id = "mestre",
                    titulo = "Nível Mestre",
                    sequenciaCores = "Verde, Azul, Vermelho, Amarelo, Verde, Azul, Vermelho, Amarelo",
                    dificuldade = "Difícil",
                    recordePts = 340,
                    velocidadeMs = 600L
                ),
                DesafioEntity(
                    id = "relampago",
                    titulo = "Ritmo Relâmpago",
                    sequenciaCores = "Amarelo, Verde, Azul, Vermelho, Verde, Azul",
                    dificuldade = "Médio",
                    recordePts = 220,
                    velocidadeMs = 300L
                ),
                DesafioEntity(
                    id = "primeiros_passos",
                    titulo = "Primeiros Passos",
                    sequenciaCores = "Verde, Vermelho, Amarelo, Azul",
                    dificuldade = "Fácil",
                    recordePts = 100,
                    velocidadeMs = 600L
                )
            )
        }
    }
}
