package com.todoapp.repository

import com.todoapp.entities.ToDo
import com.todoapp.entities.ToDoDraft

interface ToDoRepository {

    fun getAllToDos():List<ToDo>

    fun getToDo(id:Int):ToDo?

    fun addToDo(draft: ToDoDraft):ToDo

    fun removeTodo(id:Int):Boolean

    fun updateTodo(id: Int,draft: ToDoDraft):Boolean

}