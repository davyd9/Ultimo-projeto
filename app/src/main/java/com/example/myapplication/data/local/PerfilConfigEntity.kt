package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_config_table")
data class PerfilConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val apelidoPadrao: String,
    val velocidadePadrao: String,
    val dificuldadePadrao: String,
    val intervaloMs: Long
)
