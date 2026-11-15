package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.DesafioRepository
import com.example.myapplication.data.repository.RankingRepository

class AppViewModelFactory(
    private val rankingRepository: RankingRepository,
    private val desafioRepository: DesafioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(rankingRepository) as T
            }
            modelClass.isAssignableFrom(SimonViewModel::class.java) -> {
                SimonViewModel(rankingRepository, desafioRepository) as T
            }
            modelClass.isAssignableFrom(DesafiosViewModel::class.java) -> {
                DesafiosViewModel(desafioRepository) as T
            }
            modelClass.isAssignableFrom(RankingViewModel::class.java) -> {
                RankingViewModel(rankingRepository) as T
            }
            modelClass.isAssignableFrom(EditarRegistroViewModel::class.java) -> {
                EditarRegistroViewModel(rankingRepository) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(desafioRepository) as T
            }
            else -> throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
