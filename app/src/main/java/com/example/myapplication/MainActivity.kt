package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.remote.FirebaseSource
import com.example.myapplication.data.repository.DesafioRepository
import com.example.myapplication.data.repository.RankingRepository
import com.example.myapplication.ui.navigation.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AppViewModelFactory

/**
 * Activity principal (Ponto de entrada da aplicação).
 * Inicializa o banco de dados Room, a fonte de dados do Firebase Firestore,
 * os repositórios e a AppViewModelFactory antes de iniciar o grafo de navegação Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                // Instância Singleton do banco local Room ("simon_master_database")
                val database = remember { AppDatabase.getInstance(applicationContext) }

                // Repositório responsável pela entidade RankingEntity no Room
                val rankingRepository = remember {
                    RankingRepository(rankingDao = database.rankingDao())
                }

                // Repositório híbrido responsável pela entidade DesafioEntity (Room + Firebase Firestore)
                val desafioRepository = remember {
                    DesafioRepository(
                        desafioDao = database.desafioDao(),
                        firebaseSource = FirebaseSource()
                    )
                }

                // Factory que injeta os repositórios nos 6 ViewModels da arquitetura MVVM
                val factory = remember {
                    AppViewModelFactory(
                        rankingRepository = rankingRepository,
                        desafioRepository = desafioRepository
                    )
                }

                // Inicializa a estrutura global de navegação do app
                AppNavigation(factory = factory)
            }
        }
    }
}
