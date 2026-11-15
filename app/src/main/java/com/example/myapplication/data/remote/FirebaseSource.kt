package com.example.myapplication.data.remote

import com.example.myapplication.data.model.DesafioEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseSource {

    companion object {
        const val COLECAO_DESAFIOS = "desafios"
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun buscarDesafiosRemotos(): List<DesafioEntity> = withContext(Dispatchers.IO) {
        val snapshot = Tasks.await(firestore.collection(COLECAO_DESAFIOS).get())
        snapshot.documents.mapNotNull { doc ->
            val id = doc.id.ifBlank { return@mapNotNull null }
            val titulo = doc.getString("titulo") ?: return@mapNotNull null
            val sequenciaCores = doc.getString("sequenciaCores") ?: "Verde, Azul, Vermelho"
            val dificuldade = doc.getString("dificuldade") ?: "Fácil"
            val recordePts = doc.getLong("recordePts")?.toInt() ?: 100
            val velocidadeMs = doc.getLong("velocidadeMs") ?: 600L

            DesafioEntity(
                id = id,
                titulo = titulo,
                sequenciaCores = sequenciaCores,
                dificuldade = dificuldade,
                recordePts = recordePts,
                velocidadeMs = velocidadeMs
            )
        }
    }

    suspend fun publicarDesafioNoFirebase(desafio: DesafioEntity) = withContext(Dispatchers.IO) {
        val dados = mapOf(
            "id" to desafio.id,
            "titulo" to desafio.titulo,
            "sequenciaCores" to desafio.sequenciaCores,
            "dificuldade" to desafio.dificuldade,
            "recordePts" to desafio.recordePts,
            "velocidadeMs" to desafio.velocidadeMs
        )
        Tasks.await(
            firestore.collection(COLECAO_DESAFIOS)
                .document(desafio.id)
                .set(dados)
        )
    }

    suspend fun atualizarDesafioNoFirebase(desafio: DesafioEntity) = withContext(Dispatchers.IO) {
        publicarDesafioNoFirebase(desafio)
    }

    suspend fun excluirDesafioNoFirebase(id: String) = withContext(Dispatchers.IO) {
        Tasks.await(
            firestore.collection(COLECAO_DESAFIOS)
                .document(id)
                .delete()
        )
    }
}
