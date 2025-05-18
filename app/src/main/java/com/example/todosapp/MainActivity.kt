
package com.example.todosapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.todosapp.data.local.TodoDatabase
import com.example.todosapp.data.remote.TodoApi
import com.example.todosapp.data.repository.TodoRepository
import com.example.todosapp.viewModel.TodoListViewModel
import com.example.todosapp.viewModel.TodoDetailViewModel
import androidx.compose.ui.Modifier

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.room.Room
import com.example.todosapp.ui.theme.TodosAppTheme

import kotlin.jvm.java
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room with coroutine support
        val db = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo-db"
        ).fallbackToDestructiveMigration()
            .build()

        // Initialize Retrofit with proper timeouts
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TodoApi::class.java)
        val repository = TodoRepository(apiService, db.todoDao())

        setContent {
            TodosAppTheme {
                // Single ViewModel factory for all ViewModels
                val viewModelFactory = TodoViewModelFactory(repository)

                val navController = rememberNavController()
                val listViewModel: TodoListViewModel = viewModel(factory = viewModelFactory)
                val detailViewModel: TodoDetailViewModel = viewModel(factory = viewModelFactory)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        listViewModel = listViewModel,
                        detailViewModel = detailViewModel
                    )
                }
            }
        }
    }
}

// Separate ViewModel Factory class
class TodoViewModelFactory(
    private val repository: TodoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TodoListViewModel::class.java) -> {
                TodoListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TodoDetailViewModel::class.java) -> {
                TodoDetailViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}