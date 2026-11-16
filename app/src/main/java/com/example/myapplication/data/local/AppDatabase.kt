package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.data.model.RankingEntity

/**
 * Configuração central do banco de dados local Room ("simon_master_database").
 * Reúne as 2 entidades obrigatórias do projeto: RankingEntity e DesafioEntity.
 */
@Database(
    entities = [
        RankingEntity::class, // Entidade de placar/ranking local dos jogadores
        DesafioEntity::class  // Entidade de desafios sincronizada com o Firebase Firestore
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Exposição do DAO para operações de Ranking.
    abstract fun rankingDao(): RankingDao

    // Exposição do DAO para operações de Desafios (Painel Admin e Tela de Desafios).
    abstract fun desafioDao(): DesafioDao

    companion object {
        // Garante visibilidade imediata da instância Singleton entre diferentes threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Retorna a instância única do banco Room ou cria uma nova de forma thread-safe.
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simon_master_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
