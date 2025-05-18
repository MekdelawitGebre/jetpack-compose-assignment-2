package com.example.todosapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.todosapp.data.model.Todo
import com.example.todosapp.data.repository.TodoRepository


sealed class TodoDetailUiState {
    object Loading : TodoDetailUiState()
    data class Success(val todo: Todo) : TodoDetailUiState()
    data class Error(val message: String) : TodoDetailUiState()
}

class TodoDetailViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoDetailUiState>(TodoDetailUiState.Loading)
    val uiState: StateFlow<TodoDetailUiState> = _uiState

    fun loadTodo(id: Int) {
        viewModelScope.launch {
            _uiState.value = TodoDetailUiState.Loading
            try {
                val todo = repository.getTodoById(id)
                if (todo != null) {
                    _uiState.value = TodoDetailUiState.Success(todo)
                } else {
                    _uiState.value = TodoDetailUiState.Error("Todo not found")
                }
            } catch (_: Exception) {
                _uiState.value = TodoDetailUiState.Error("Failed to load todo")
            }
        }
    }
}