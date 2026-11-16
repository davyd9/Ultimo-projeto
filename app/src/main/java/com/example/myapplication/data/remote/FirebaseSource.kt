package com.example.myapplication.data.remote

import com.example.myapplication.data.model.DesafioEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fonte de dados remota responsável pela comunicação direta com o Cloud Firestore
 * no projeto Firebase "golden-bloom-377322".
 * Executa o CRUD completo na coleção "desafios" em thread de I/O (Dispatchers.IO).
 */
class FirebaseSource {

    companion object {
        // Nome da coleção remota no Cloud Firestore configurada nas regras (firestore.rules).
        const val COLECAO_DESAFIOS = "desafios"
    }

    // Instância Singleton do FirebaseFirestore inicializada sob demanda (lazy) via google-services.json.
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Busca todos os documentos da coleção "desafios" no Cloud Firestore
     * e converte cada DocumentSnapshot em um objeto DesafioEntity.
     */
    suspend fun buscarDesafiosRemotos(): List<DesafioEntity> = withContext(Dispatchers.IO) {
        // Aguarda a conclusão da Task de leitura da coleção "desafios" fora da Main Thread.
        val snapshot = Tasks.await(firestore.collection(COLECAO_DESAFIOS).get())
        snapshot.documents.mapNotNull { doc ->
            // Extrai e valida os campos de cada documento retornado pelo Firestore.
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

    /**
     * Publica (cria ou sobrescreve) um documento de desafio na coleção "desafios" do Firebase Firestore.
     */
    suspend fun publicarDesafioNoFirebase(desafio: DesafioEntity) = withContext(Dispatchers.IO) {
        // Mapa de campos enviados para persistência na nuvem.
        val dados = mapOf(
            "id" to desafio.id,
            "titulo" to desafio.titulo,
            "sequenciaCores" to desafio.sequenciaCores,
            "dificuldade" to desafio.dificuldade,
            "recordePts" to desafio.recordePts,
            "velocidadeMs" to desafio.velocidadeMs
        )
        // Grava o documento usando o ID do desafio como chave na coleção "desafios".
        Tasks.await(
            firestore.collection(COLECAO_DESAFIOS)
                .document(desafio.id)
                .set(dados)
        )
    }

    /**
     * Atualiza os dados de um desafio existente na coleção "desafios" do Firebase Firestore.
     */
    suspend fun atualizarDesafioNoFirebase(desafio: DesafioEntity) = withContext(Dispatchers.IO) {
        publicarDesafioNoFirebase(desafio)
    }

    /**
     * Remove permanentemente o documento de um desafio da coleção "desafios" no Firebase Firestore.
     */
    suspend fun excluirDesafioNoFirebase(id: String) = withContext(Dispatchers.IO) {
        Tasks.await(
            firestore.collection(COLECAO_DESAFIOS)
                .document(id)
                .delete()
        )
    }
}
