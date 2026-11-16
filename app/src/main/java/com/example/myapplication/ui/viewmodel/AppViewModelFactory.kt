package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.DesafioRepository
import com.example.myapplication.data.repository.RankingRepository

/**
 * Factory responsável pela Injeção Manual de Dependências nos 6 ViewModels do projeto.
 * Injeta o RankingRepository e o DesafioRepository conforme a necessidade de cada feature.
 */
class AppViewModelFactory(
    private val rankingRepository: RankingRepository,
    private val desafioRepository: DesafioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. ViewModel da Tela Inicial / Login (controle de perfil Jogador vs Admin)
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(rankingRepository) as T
            }
            // 2. ViewModel da Tela Principal do Jogo Simon
            modelClass.isAssignableFrom(SimonViewModel::class.java) -> {
                SimonViewModel(rankingRepository, desafioRepository) as T
            }
            // 3. ViewModel da Tela de Desafios Sincronizados da Nuvem
            modelClass.isAssignableFrom(DesafiosViewModel::class.java) -> {
                DesafiosViewModel(desafioRepository) as T
            }
            // 4. ViewModel da Tela de Ranking Local
            modelClass.isAssignableFrom(RankingViewModel::class.java) -> {
                RankingViewModel(rankingRepository) as T
            }
            // 5. ViewModel da Tela de Editar Registro / Opções
            modelClass.isAssignableFrom(EditarRegistroViewModel::class.java) -> {
                EditarRegistroViewModel(rankingRepository) as T
            }
            // 6. ViewModel do Painel Administrador (CRUD Firebase Firestore + Room)
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(desafioRepository) as T
            }
            else -> throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
