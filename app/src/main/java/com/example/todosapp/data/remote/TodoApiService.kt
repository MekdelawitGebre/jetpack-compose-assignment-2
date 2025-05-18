package com.example.todosapp.data.remote

import com.example.todosapp.data.model.Todo
import retrofit2.http.GET

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<Todo>
}
