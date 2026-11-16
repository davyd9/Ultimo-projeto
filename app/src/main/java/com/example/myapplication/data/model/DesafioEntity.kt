package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade principal de Desafios (Entidade 1 do projeto).
 * Mapeada simultaneamente para a tabela local "desafios_table" no Room Database
 * e para os documentos da coleção remota "desafios" no Firebase Cloud Firestore.
 */
@Entity(tableName = "desafios_table")
data class DesafioEntity(
    // Identificador único do desafio (chave primária no Room e ID do documento no Firestore).
    @PrimaryKey
    val id: String = "",
    // Título do desafio exibido no Painel Admin e na tela de Desafios (ex: "Nível Mestre").
    val titulo: String = "",
    // Sequência de cores separada por vírgula cadastrada pelo Administrador (ex: "Verde, Azul, Vermelho").
    val sequenciaCores: String = "",
    // Nível de dificuldade normalizado ("Fácil", "Médio" ou "Difícil").
    val dificuldade: String = "Fácil",
    // Pontuação recorde associada ao desafio.
    val recordePts: Int = 100,
    // Velocidade em milissegundos de exibição das cores na partida.
    val velocidadeMs: Long = 600L
)
