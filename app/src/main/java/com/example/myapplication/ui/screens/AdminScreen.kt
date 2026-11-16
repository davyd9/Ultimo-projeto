package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.DesafioEntity
import com.example.myapplication.ui.viewmodel.AdminUiState
import com.example.myapplication.ui.viewmodel.AdminViewModel

// Paleta de cores do Painel Administrador
private val AzulEscuroHeader = Color(0xFF211D3A)
private val ContainerCadeado = Color(0xFF322C56)
private val RoxoAdmin = Color(0xFF5145AA)
private val FundoAdmin = Color(0xFFFCF9FF)
private val VerdeOnline = Color(0xFF4ADE80)
private val LilasBadge = Color(0xFFE8E3FB)
private val LilasIconBox = Color(0xFFEDE9FE)
private val BordaInput = Color(0xFF938F99)
private val VermelhoLixeiraAdmin = Color(0xFFB83B2A)
private val BordaVermelhaSuaveAdmin = Color(0xFFE6C8C4)

/**
 * Tela 06 - Painel Administrador (AdminScreen).
 * Permite ao Administrador realizar o CRUD completo (Criar, Ler, Atualizar e Excluir)
 * de desafios sincronizados entre o Firebase Cloud Firestore e o Room Database.
 */
@Composable
fun AdminScreen(
    uiState: AdminUiState,
    onNomeChange: (String) -> Unit,
    onSequenciaChange: (String) -> Unit,
    onDificuldadeChange: (String) -> Unit,
    onPublicarOuSalvar: () -> Unit,
    onEditarDesafio: (DesafioEntity) -> Unit,
    onExcluirDesafio: (DesafioEntity) -> Unit,
    onExcluirRegistroEmEdicao: () -> Unit = {},
    onCancelarEdicao: () -> Unit,
    onVoltarLogin: () -> Unit
) {
    // Estrutura vertical principal ocupando toda a tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoAdmin)
            .navigationBarsPadding()
    ) {
        // 1. Cabeçalho Superior Escuro (Top Header Admin) com indicador Online e botão Sair
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulEscuroHeader)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bloco esquerdo: Ícone de cadeado + Título do Painel Admin
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ContainerCadeado)
                            .clickable { onVoltarLogin() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Voltar para o Login",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PAINEL ADMIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFB5AEE0)
                        )
                        Text(
                            text = "Gerenciar Desafios",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Bloco direito: Indicador de sincronização "Online" + Botão de "Sair"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF2D284B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isOnline) VerdeOnline else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Botão de Sair que desvincula a sessão atual e retorna para a Tela Inicial (Login)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF3A3264),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onVoltarLogin() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Sair do Painel Admin",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sair",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. Lista Rolável Principal (Formulário CRUD + Lista Dinâmica de Desafios)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card de Formulário ("Novo desafio" / "Editar desafio")
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFECE7F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (uiState.emModoEdicao) "Editar desafio" else "Novo desafio",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF342C6E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Publique uma sequência na nuvem",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8E8A98)
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.CloudDone,
                                contentDescription = "Nuvem",
                                tint = RoxoAdmin,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Input 1: Nome do Desafio
                        AdminInputField(
                            label = "Nome do Desafio",
                            value = uiState.nomeDesafio,
                            placeholder = "Ex: Nível Especial",
                            onValueChange = onNomeChange
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input 2: Sequência de Cores
                        AdminInputField(
                            label = "Sequência de Cores",
                            value = uiState.sequenciaCores,
                            placeholder = "Verde, Azul, Vermelho",
                            onValueChange = onSequenciaChange
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input 3: Dificuldade
                        AdminInputField(
                            label = "Dificuldade",
                            value = uiState.dificuldade,
                            placeholder = "Fácil, Médio ou Difícil",
                            onValueChange = onDificuldadeChange
                        )

                        // Exibição condicional de mensagens de erro tratadas pelo ViewModel/Repository
                        uiState.errorMessage?.let { erro ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFDECEC)
                            ) {
                                Text(
                                    text = erro,
                                    color = VermelhoLixeiraAdmin,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Botão de ação principal (Publicar novo desafio ou Salvar desafio em edição no Firebase)
                        Button(
                            onClick = onPublicarOuSalvar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoxoAdmin,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.emModoEdicao) "Salvar no Firebase" else "Publicar no Firebase",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Botões adicionais exibidos quando um registro está selecionado para edição
                        if (uiState.emModoEdicao) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Botão "Excluir Registro": remove do Room/Firebase e retorna à Tela Inicial de Login
                            OutlinedButton(
                                onClick = onExcluirRegistroEmEdicao,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = VermelhoLixeiraAdmin
                                ),
                                border = BorderStroke(1.dp, BordaVermelhaSuaveAdmin)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = null,
                                    tint = VermelhoLixeiraAdmin,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Excluir Registro",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VermelhoLixeiraAdmin
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            TextButton(
                                onClick = onCancelarEdicao,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Cancelar edição",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7A7685)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Cabeçalho da Seção "Desafios cadastrados"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Desafios cadastrados",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E1E24)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LilasBadge
                        ) {
                            Text(
                                text = "${uiState.desafios.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoxoAdmin
                            )
                        }
                    }

                    Text(
                        text = "Sincronizados",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E8B57)
                    )
                }
            }

            // 4. Listagem dinâmica (LazyColumn items) dos desafios vindos do Flow
            items(
                items = uiState.desafios,
                key = { desafio -> desafio.id }
            ) { desafio ->
                DesafioAdminCard(
                    desafio = desafio,
                    onEditar = { onEditarDesafio(desafio) },
                    onExcluir = { onExcluirDesafio(desafio) }
                )
            }
        }
    }
}

/**
 * Componente reutilizável de campo de texto estilizado do formulário administrativo.
 */
@Composable
private fun AdminInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BordaInput)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF7A7685)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = Color(0xFFA09CAB)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E1E24)
                    ),
                    cursorBrush = SolidColor(RoxoAdmin),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Card individual de desafio exibido na lista do Painel Admin com ações de Editar e Excluir.
 */
@Composable
private fun DesafioAdminCard(
    desafio: DesafioEntity,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    val resumoCores = AdminViewModel.formatarResumoSequencia(desafio.sequenciaCores)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFECE7F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LilasIconBox),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = null,
                    tint = RoxoAdmin,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = desafio.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E24)
                )

                Text(
                    text = resumoCores,
                    fontSize = 12.sp,
                    color = Color(0xFF8E8A98)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LilasIconBox
                ) {
                    Text(
                        text = desafio.dificuldade,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoxoAdmin
                    )
                }
            }

            IconButton(
                onClick = onEditar,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar ${desafio.titulo}",
                    tint = Color(0xFF5A5665),
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            IconButton(
                onClick = onExcluir,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Excluir ${desafio.titulo}",
                    tint = VermelhoLixeiraAdmin,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}
