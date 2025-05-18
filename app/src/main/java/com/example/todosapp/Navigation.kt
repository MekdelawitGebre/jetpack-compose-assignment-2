package com.example.todosapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todosapp.ui.screens.TodoListScreen
import com.example.todosapp.viewModel.TodoListViewModel
import com.example.todosapp.viewModel.TodoDetailViewModel
import com.example.todosapp.ui.screens.TodoDetailScreen
@Composable
fun AppNavHost(
    navController: NavHostController,
    listViewModel: TodoListViewModel,
    detailViewModel: TodoDetailViewModel
) {
    NavHost(navController, startDestination = "list") {
        composable("list") {
            TodoListScreen(
                viewModel = listViewModel,
                onTodoClick = { id ->
                    detailViewModel.loadTodo(id)
                    navController.navigate("detail/$id")
                }
            )
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            TodoDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}