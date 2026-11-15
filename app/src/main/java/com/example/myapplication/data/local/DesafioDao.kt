package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DesafioDao {

    @Query("SELECT * FROM desafios_table")
    fun listarTodos(): Flow<List<DesafioLocalEntity>>

    @Query("SELECT COUNT(*) FROM desafios_table")
    suspend fun contarRegistros(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(desafio: DesafioLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(desafios: List<DesafioLocalEntity>)

    @Update
    suspend fun atualizar(desafio: DesafioLocalEntity)

    @Delete
    suspend fun deletar(desafio: DesafioLocalEntity)

    @Query("DELETE FROM desafios_table WHERE id = :id")
    suspend fun deletarPorId(id: String)
}
