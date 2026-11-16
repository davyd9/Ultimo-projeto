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

/**
 * Repositório híbrido (Offline-First) para gerenciamento dos Desafios.
 * Coordena a persistência local reativa no Room (DesafioDao) com a sincronização em nuvem
 * no Firebase Cloud Firestore (FirebaseSource), isolando exceções com blocos try-catch.
 */
class DesafioRepository(
    private val desafioDao: DesafioDao,
    private val firebaseSource: FirebaseSource = FirebaseSource()
) {

    // Armazena o ID do desafio atualmente selecionado para partida na tela do jogo Simon.
    private val _desafioAtivoId = MutableStateFlow<String?>(null)
    val desafioAtivoId: StateFlow<String?> = _desafioAtivoId.asStateFlow()

    // Define ou limpa o desafio ativo selecionado pelo jogador.
    fun selecionarDesafioParaJogar(desafioId: String?) {
        _desafioAtivoId.value = desafioId
    }

    /**
     * Expõe o fluxo contínuo (Flow) de desafios vindos do Room Database.
     * Qualquer alteração no banco dispara uma nova emissão para o AdminViewModel e DesafiosViewModel.
     */
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

    /**
     * Garante o seed inicial no banco local e sincroniza bidirecionalmente com o Firebase Firestore.
     */
    suspend fun inicializarESincronizarDesafios(): Result<Boolean> {
        return try {
            // 1. Popula o banco local Room caso ainda não existam registros.
            if (desafioDao.contarRegistros() == 0) {
                desafioDao.inserirTodos(obterDesafiosIniciaisPadrao())
            }

            // 2. Sincroniza com a coleção "desafios" do Firebase Firestore (com fallback seguro offline).
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

    /**
     * Cria um novo desafio salvando imediatamente no Room e publicando no Firebase Firestore.
     */
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

    /**
     * Atualiza um desafio existente no Room e sincroniza a alteração no Firebase Firestore.
     */
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

    /**
     * Exclui um desafio do banco local Room e remove o documento correspondente no Firebase Firestore.
     */
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
        // Lista de desafios iniciais padrão (Fácil, Médio e Difícil) para seed local e remoto.
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
