package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "desafios_table")
data class DesafioEntity(
    @PrimaryKey
    val id: String = "",
    val titulo: String = "",
    val sequenciaCores: String = "",
    val dificuldade: String = "Fácil",
    val recordePts: Int = 100,
    val velocidadeMs: Long = 600L
)
