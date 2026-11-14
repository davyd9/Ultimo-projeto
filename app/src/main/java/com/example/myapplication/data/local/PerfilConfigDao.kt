package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilConfigDao {

    @Query("SELECT * FROM perfil_config_table")
    fun observarPerfis(): Flow<List<PerfilConfigEntity>>

    @Query("SELECT COUNT(*) FROM perfil_config_table")
    suspend fun contarPerfis(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarPerfil(perfil: PerfilConfigEntity)

    @Update
    suspend fun atualizarPerfil(perfil: PerfilConfigEntity)

    @Delete
    suspend fun deletarPerfil(perfil: PerfilConfigEntity)
}
