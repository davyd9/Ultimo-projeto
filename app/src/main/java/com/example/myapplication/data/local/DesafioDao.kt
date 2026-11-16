package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.DesafioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DesafioDao {

    @Query("SELECT * FROM desafios_table")
    fun listarTodos(): Flow<List<DesafioEntity>>

    @Query("SELECT COUNT(*) FROM desafios_table")
    suspend fun contarRegistros(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(desafio: DesafioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(desafios: List<DesafioEntity>)

    @Update
    suspend fun atualizar(desafio: DesafioEntity)

    @Delete
    suspend fun deletar(desafio: DesafioEntity)
}
