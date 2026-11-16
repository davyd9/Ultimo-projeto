package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.DesafioEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) responsável pelas operações SQL na tabela "desafios_table" (Room).
 * Trabalha em conjunto com o FirebaseSource dentro do DesafioRepository (arquitetura Offline-First).
 */
@Dao
interface DesafioDao {

    // Retorna um Flow reativo com todos os desafios locais; atualiza a UI automaticamente a cada mudança.
    @Query("SELECT * FROM desafios_table")
    fun listarTodos(): Flow<List<DesafioEntity>>

    // Conta o total de desafios armazenados localmente para verificar necessidade de seed inicial.
    @Query("SELECT COUNT(*) FROM desafios_table")
    suspend fun contarRegistros(): Int

    // Insere ou substitui um desafio individual na tabela local.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(desafio: DesafioEntity)

    // Insere ou atualiza uma lista de desafios em lote (usado ao sincronizar documentos do Firebase).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(desafios: List<DesafioEntity>)

    // Atualiza um desafio existente identificado pela sua chave primária (id).
    @Update
    suspend fun atualizar(desafio: DesafioEntity)

    // Remove o registro do desafio da tabela local do Room.
    @Delete
    suspend fun deletar(desafio: DesafioEntity)
}
