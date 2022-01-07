package com.todoapp.entities

data class ToDoDraft(
    val title: String,
    val done: Boolean
)

data class TodoResponse<T>(
    var success: Boolean,
    var msg: String,
    var data: T
)