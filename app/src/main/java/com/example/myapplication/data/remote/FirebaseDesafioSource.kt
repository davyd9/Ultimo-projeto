package com.example.myapplication.data.remote

import com.example.myapplication.data.local.DesafioLocalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fonte de dados remota para gerenciamento da coleção "desafios" no Firebase Firestore.
 *
 * Estrutura pronta para conexão com o Firebase Firestore (`google-services.json`).
 * Caso o SDK/arquivo `google-services.json` ainda não esteja configurado no ambiente local,
 * lança exceção capturada pelo `try-catch` do `DesafioRepository`, que mantém o aplicativo
 * operando em modo híbrido com persistência reativa no Room (`DesafioDao`).
 */
class FirebaseDesafioSource {

    companion object {
        const val COLECAO_DESAFIOS = "desafios"
    }

    private fun obterInstanciaFirestore(): Any {
        return try {
            val clazz = Class.forName("com.google.firebase.firestore.FirebaseFirestore")
            val getInstanceMethod = clazz.getMethod("getInstance")
            getInstanceMethod.invoke(null)
                ?: throw IllegalStateException("Instância do FirebaseFirestore retornou nula.")
        } catch (e: Exception) {
            throw IllegalStateException(
                "Firebase Firestore ainda não configurado localmente (adicione o google-services.json).",
                e
            )
        }
    }

    fun isFirebaseDisponivel(): Boolean {
        return try {
            obterInstanciaFirestore()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun buscarDesafiosRemotos(): List<DesafioLocalEntity> = withContext(Dispatchers.IO) {
        val firestore = obterInstanciaFirestore()
        val collectionMethod = firestore.javaClass.getMethod("collection", String::class.java)
        val collectionRef = collectionMethod.invoke(firestore, COLECAO_DESAFIOS)
        val getMethod = collectionRef.javaClass.getMethod("get")
        val task = getMethod.invoke(collectionRef)

        val tasksClass = Class.forName("com.google.android.gms.tasks.Tasks")
        val awaitMethod = tasksClass.getMethod("await", Class.forName("com.google.android.gms.tasks.Task"))
        val querySnapshot = awaitMethod.invoke(null, task)

        val getDocumentsMethod = querySnapshot.javaClass.getMethod("getDocuments")
        val documents = getDocumentsMethod.invoke(querySnapshot) as? List<*> ?: emptyList<Any>()

        documents.mapNotNull { doc ->
            if (doc == null) return@mapNotNull null
            val getId = doc.javaClass.getMethod("getId")
            val getString = doc.javaClass.getMethod("getString", String::class.java)
            val getLong = doc.javaClass.getMethod("getLong", String::class.java)

            val id = getId.invoke(doc) as? String ?: return@mapNotNull null
            val titulo = getString.invoke(doc, "titulo") as? String ?: "Desafio"
            val sequenciaCores = getString.invoke(doc, "sequenciaCores") as? String ?: "Verde, Azul, Vermelho"
            val dificuldade = getString.invoke(doc, "dificuldade") as? String ?: "Fácil"
            val recordePts = (getLong.invoke(doc, "recordePts") as? Long)?.toInt() ?: 100
            val velocidadeMs = (getLong.invoke(doc, "velocidadeMs") as? Long) ?: 600L

            DesafioLocalEntity(
                id = id,
                titulo = titulo,
                sequenciaCores = sequenciaCores,
                dificuldade = dificuldade,
                recordePts = recordePts,
                velocidadeMs = velocidadeMs,
                sincronizado = true
            )
        }
    }

    suspend fun publicarDesafioNoFirebase(desafio: DesafioLocalEntity) = withContext(Dispatchers.IO) {
        val firestore = obterInstanciaFirestore()
        val collectionMethod = firestore.javaClass.getMethod("collection", String::class.java)
        val collectionRef = collectionMethod.invoke(firestore, COLECAO_DESAFIOS)
        val documentMethod = collectionRef.javaClass.getMethod("document", String::class.java)
        val docRef = documentMethod.invoke(collectionRef, desafio.id)

        val dados = mapOf(
            "id" to desafio.id,
            "titulo" to desafio.titulo,
            "sequenciaCores" to desafio.sequenciaCores,
            "dificuldade" to desafio.dificuldade,
            "recordePts" to desafio.recordePts,
            "velocidadeMs" to desafio.velocidadeMs,
            "sincronizado" to true
        )

        val setMethod = docRef.javaClass.getMethod("set", Any::class.java)
        val task = setMethod.invoke(docRef, dados)

        val tasksClass = Class.forName("com.google.android.gms.tasks.Tasks")
        val awaitMethod = tasksClass.getMethod("await", Class.forName("com.google.android.gms.tasks.Task"))
        awaitMethod.invoke(null, task)
    }

    suspend fun atualizarDesafioNoFirebase(desafio: DesafioLocalEntity) = withContext(Dispatchers.IO) {
        publicarDesafioNoFirebase(desafio)
    }

    suspend fun excluirDesafioNoFirebase(id: String) = withContext(Dispatchers.IO) {
        val firestore = obterInstanciaFirestore()
        val collectionMethod = firestore.javaClass.getMethod("collection", String::class.java)
        val collectionRef = collectionMethod.invoke(firestore, COLECAO_DESAFIOS)
        val documentMethod = collectionRef.javaClass.getMethod("document", String::class.java)
        val docRef = documentMethod.invoke(collectionRef, id)

        val deleteMethod = docRef.javaClass.getMethod("delete")
        val task = deleteMethod.invoke(docRef)

        val tasksClass = Class.forName("com.google.android.gms.tasks.Tasks")
        val awaitMethod = tasksClass.getMethod("await", Class.forName("com.google.android.gms.tasks.Task"))
        awaitMethod.invoke(null, task)
    }
}
