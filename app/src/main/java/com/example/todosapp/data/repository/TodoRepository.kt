package com.example.todosapp.data.repository

import com. example. todosapp.data. local. TodoDao
import com. example. todosapp. data.local. TodoEntity
import com. example. todosapp. data.model. Todo
import com.example.todosapp.data.remote.TodoApi



class TodoRepository(
    private val api: TodoApi,
    private val dao: TodoDao
) {
    suspend fun getTodos(): List<Todo> {
        val cached = dao.getAllTodos()
        if (cached.isNotEmpty()) {
            refreshTodos()
            return cached.map { it.toDomain() }
        } else {
            val remote = api.getTodos()
            dao.insertTodos(remote.map { it.toEntity() })
            return remote
        }
    }

    suspend fun getTodoById(id: Int): Todo? {
        return dao.getTodoById(id)?.toDomain()
    }

    private suspend fun refreshTodos() {
        try {
            val remote = api.getTodos()
            dao.insertTodos(remote.map { it.toEntity() })
        } catch (_: Exception) {}
    }
}

// Extension functions for mapping
fun TodoEntity.toDomain() = Todo(userId, id, title, completed)
fun Todo.toEntity() = TodoEntity(id, userId, title, completed)