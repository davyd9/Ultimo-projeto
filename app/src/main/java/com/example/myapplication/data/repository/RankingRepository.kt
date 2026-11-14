package com.example.myapplication.data.repository

import com.example.myapplication.data.local.PerfilConfigDao
import com.example.myapplication.data.local.PerfilConfigEntity
import com.example.myapplication.data.local.RankingDao
import com.example.myapplication.data.local.RankingEntity
import com.example.myapplication.data.logic.VelocidadeConfigHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RankingRepository(
    private val rankingDao: RankingDao,
    private val perfilConfigDao: PerfilConfigDao? = null
) {

    fun observarRanking(onError: (String) -> Unit = {}): Flow<List<RankingEntity>> {
        return try {
            rankingDao.listarTodos().catch { exception ->
                onError("Erro ao carregar o ranking local: ${exception.localizedMessage ?: "Falha desconhecida"}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            onError("Erro ao inicializar leitura do banco: ${e.localizedMessage ?: "Falha desconhecida"}")
            flow { emit(emptyList()) }
        }
    }

    suspend fun popularDadosIniciaisSeVazio(): Result<Unit> {
        return try {
            val totalRegistros = rankingDao.contarRegistros()
            if (totalRegistros == 0) {
                val iniciais = VelocidadeConfigHelper.obterRegistrosIniciais()
                iniciais.forEach { registro ->
                    rankingDao.inserir(registro)
                }
            }
            if (perfilConfigDao != null && perfilConfigDao.contarPerfis() == 0) {
                perfilConfigDao.salvarPerfil(VelocidadeConfigHelper.obterPerfilPadraoInicial())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao popular dados iniciais no Room: ${e.localizedMessage}", e))
        }
    }

    suspend fun inserirRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            rankingDao.inserir(registro)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao inserir pontuação no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun atualizarRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            rankingDao.atualizar(registro)
            if (perfilConfigDao != null) {
                val info = VelocidadeConfigHelper.obterInfoVelocidade(registro.velocidadePadrao)
                perfilConfigDao.salvarPerfil(
                    PerfilConfigEntity(
                        id = 1,
                        apelidoPadrao = registro.apelido,
                        velocidadePadrao = registro.velocidadePadrao,
                        dificuldadePadrao = registro.dificuldade,
                        intervaloMs = info.intervaloMs
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao atualizar registro no banco de dados: ${e.localizedMessage}", e))
        }
    }

    suspend fun deletarRegistro(registro: RankingEntity): Result<Unit> {
        return try {
            rankingDao.deletar(registro)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao excluir registro do banco de dados: ${e.localizedMessage}", e))
        }
    }
}
