package com.todoapp.database

import com.todoapp.entities.ToDo
import com.todoapp.entities.ToDoDraft
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class DatabaseManager {

    // config
    private val hostname = "localhost"
    private val databaseName = "tododatabase"
    private val username = "root"
    private val password = ""

    //database
    private val todoDatabase: Database

    init {
        val jdbcUrl = "jdbc:mysql://$hostname:3306/$databaseName?user=$username&password=$password&useSSL=false"
        todoDatabase = Database.connect(jdbcUrl)
    }

    fun getAllTodos(): List<DBTodoEntity> {
        return todoDatabase.sequenceOf(DBTodoTable).toList()
    }

    fun getTodo(id: Int): DBTodoEntity? {
        return todoDatabase.sequenceOf(DBTodoTable).firstOrNull { it.id eq id }
    }

    fun addTodo(draft: ToDoDraft): ToDo {
        val insertedId = todoDatabase.insertAndGenerateKey(DBTodoTable) {
            set(DBTodoTable.title, draft.title)
            set(DBTodoTable.done, draft.done)
        } as Int
        return ToDo(insertedId, draft.title, draft.done)
    }

    fun updateTodo(id: Int, draft: ToDoDraft): Boolean {
        val updatedRows = todoDatabase.update(DBTodoTable) {
            set(DBTodoTable.title, draft.title)
            set(DBTodoTable.done, draft.done)
            where { it.id eq id }
        }
        return updatedRows > 0
    }

    fun removeTodo(id: Int): Boolean {
        val deletedRows = todoDatabase.delete(DBTodoTable) {
            it.id eq id
        }
        return deletedRows > 0
    }
}