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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val database = remember { AppDatabase.getInstance(applicationContext) }
                val rankingRepository = remember {
                    RankingRepository(rankingDao = database.rankingDao())
                }
                val desafioRepository = remember {
                    DesafioRepository(
                        desafioDao = database.desafioDao(),
                        firebaseSource = FirebaseSource()
                    )
                }
                val factory = remember {
                    AppViewModelFactory(
                        rankingRepository = rankingRepository,
                        desafioRepository = desafioRepository
                    )
                }

                AppNavigation(factory = factory)
            }
        }
    }
}
