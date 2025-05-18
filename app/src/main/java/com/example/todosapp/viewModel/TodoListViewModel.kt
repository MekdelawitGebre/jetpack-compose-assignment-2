package com.example.todosapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.todosapp.data.model.Todo
import com. example. todosapp.data. repository. TodoRepository


sealed class TodoListUiState {
    object Loading : TodoListUiState()
    data class Success(val todos: List<Todo>) : TodoListUiState()
    data class Error(val message: String, val cached: List<Todo>?) : TodoListUiState()
}

class TodoListViewModel(private val repository: TodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoListUiState>(TodoListUiState.Loading)
    val uiState: StateFlow<TodoListUiState> = _uiState

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _uiState.value = TodoListUiState.Loading
            try {
                val todos = repository.getTodos()
                _uiState.value = TodoListUiState.Success(todos)
            } catch (e: Exception) {
                val cached = repository.getTodos()
                _uiState.value = TodoListUiState.Error("Failed to load todos", cached)
            }
        }
    }
}