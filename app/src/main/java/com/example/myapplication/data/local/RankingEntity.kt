package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking_table")
data class RankingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val apelido: String,
    val dificuldade: String,
    val velocidadePadrao: String,
    val pontos: Int
)
