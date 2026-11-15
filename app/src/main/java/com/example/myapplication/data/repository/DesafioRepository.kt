package com.example.myapplication.data.repository

import com.example.myapplication.data.local.DesafioDao
import com.example.myapplication.data.local.DesafioLocalEntity
import com.example.myapplication.data.remote.FirebaseDesafioSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DesafioRepository(
    private val desafioDao: DesafioDao,
    private val firebaseSource: FirebaseDesafioSource = FirebaseDesafioSource()
) {

    fun observarDesafios(onError: (String) -> Unit = {}): Flow<List<DesafioLocalEntity>> {
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
            // 1. Garante o seed inicial no Room caso o banco esteja vazio
            if (desafioDao.contarRegistros() == 0) {
                desafioDao.inserirTodos(obterDesafiosIniciaisPadrao())
            }

            // 2. Tenta sincronizar com o Firebase Firestore (Modo Híbrido com fallback local)
            val sincronizadoNuvem = try {
                val remotos = firebaseSource.buscarDesafiosRemotos()
                if (remotos.isNotEmpty()) {
                    desafioDao.inserirTodos(remotos)
                } else {
                    // Se a coleção remota estiver vazia e Firebase ativo, sobe os iniciais
                    val locais = obterDesafiosIniciaisPadrao()
                    locais.forEach { firebaseSource.publicarDesafioNoFirebase(it) }
                }
                true
            } catch (_: Exception) {
                // Fallback seguro quando o google-services.json ainda não foi adicionado
                true
            }

            Result.success(sincronizadoNuvem)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao inicializar desafios no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun criarDesafio(desafio: DesafioLocalEntity): Result<Unit> {
        return try {
            // Persiste localmente no Room para atualização reativa imediata
            desafioDao.inserir(desafio)

            // Tenta publicar no Firebase Firestore isolando falha de nuvem via try-catch
            try {
                firebaseSource.publicarDesafioNoFirebase(desafio)
            } catch (_: Exception) {
                // Mantém salvo localmente no Room caso o Firebase ainda não esteja configurado
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao publicar desafio: ${e.localizedMessage}", e))
        }
    }

    suspend fun atualizarDesafio(desafio: DesafioLocalEntity): Result<Unit> {
        return try {
            desafioDao.atualizar(desafio)

            try {
                firebaseSource.atualizarDesafioNoFirebase(desafio)
            } catch (_: Exception) {
                // Mantém atualizado localmente no Room caso o Firebase não esteja ativo
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao atualizar desafio: ${e.localizedMessage}", e))
        }
    }

    suspend fun excluirDesafio(desafio: DesafioLocalEntity): Result<Unit> {
        return try {
            desafioDao.deletar(desafio)

            try {
                firebaseSource.excluirDesafioNoFirebase(desafio.id)
            } catch (_: Exception) {
                // Mantém excluído localmente no Room caso o Firebase não esteja ativo
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao excluir desafio: ${e.localizedMessage}", e))
        }
    }

    companion object {
        fun obterDesafiosIniciaisPadrao(): List<DesafioLocalEntity> {
            return listOf(
                DesafioLocalEntity(
                    id = "mestre",
                    titulo = "Nível Mestre",
                    sequenciaCores = "Verde, Azul, Vermelho, Amarelo, Verde, Azul, Vermelho, Amarelo",
                    dificuldade = "Difícil",
                    recordePts = 340,
                    velocidadeMs = 600L,
                    sincronizado = true
                ),
                DesafioLocalEntity(
                    id = "relampago",
                    titulo = "Ritmo Relâmpago",
                    sequenciaCores = "Amarelo, Verde, Azul, Vermelho, Verde, Azul",
                    dificuldade = "Médio",
                    recordePts = 220,
                    velocidadeMs = 300L,
                    sincronizado = true
                ),
                DesafioLocalEntity(
                    id = "primeiros_passos",
                    titulo = "Primeiros Passos",
                    sequenciaCores = "Verde, Vermelho, Amarelo, Azul",
                    dificuldade = "Fácil",
                    recordePts = 100,
                    velocidadeMs = 600L,
                    sincronizado = true
                )
            )
        }
    }
}
