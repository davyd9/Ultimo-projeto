package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {

    @Query("SELECT * FROM ranking_table")
    fun listarTodos(): Flow<List<RankingEntity>>

    @Query("SELECT COUNT(*) FROM ranking_table")
    suspend fun contarRegistros(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: RankingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(registros: List<RankingEntity>)

    @Update
    suspend fun atualizar(registro: RankingEntity)

    @Delete
    suspend fun deletar(registro: RankingEntity)
}
