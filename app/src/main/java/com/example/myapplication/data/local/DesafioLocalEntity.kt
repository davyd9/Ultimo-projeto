package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

typealias DesafioEntity = DesafioLocalEntity

@Entity(tableName = "desafios_table")
data class DesafioLocalEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val sequenciaCores: String,
    val dificuldade: String,
    val recordePts: Int = 100,
    val velocidadeMs: Long = 600L,
    val sincronizado: Boolean = true
)
