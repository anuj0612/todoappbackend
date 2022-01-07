package com.todoapp

import com.todoapp.entities.ToDo
import com.todoapp.entities.ToDoDraft
import com.todoapp.entities.TodoResponse
import com.todoapp.repository.InMemoryToDoRepository
import com.todoapp.repository.MySQLTodoRepository
import com.todoapp.repository.ToDoRepository
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        val repository: ToDoRepository = MySQLTodoRepository()

        //for create todo
        post("/createtodo") {
            val toDoDraft = call.receive<ToDoDraft>()
            val todo = repository.addToDo(toDoDraft)
            val response = TodoResponse(true, "Your todo created", todo)
            call.respond(response)
        }

        // to get all todo list
        get("/todos") {
            val response = TodoResponse(true, "All todos", repository.getAllToDos())
            call.respond(repository.getAllToDos())
        }

        // to get single todo
        get("/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                val response = TodoResponse(false, "todo not found", {})
                call.respond(
                    HttpStatusCode.BadRequest,
                    response
                )
                return@get
            }
            val todo = repository.getToDo(id)
            if (todo == null) {
                val response = TodoResponse(false, "todo not found", {})
                call.respond(
                    HttpStatusCode.NotFound,
                    response
                )
            } else {
                val response = TodoResponse(true, "todo not found", todo)
                call.respond(response)
            }
        }

        // to update todo
        put("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            val toDoDraft = call.receive<ToDoDraft>()
            if (todoId == null) {
                val response = TodoResponse(false, "id parameter should be a number!", {})
                call.respond(HttpStatusCode.BadRequest, response)
                return@put
            }
            val updated = repository.updateTodo(todoId, toDoDraft)
            if (updated) {
                val response = TodoResponse(true, "updated", {})
                call.respond(HttpStatusCode.OK, response)
            } else {
                val response = TodoResponse(false, "found no todo with this id $todoId", {})
                call.respond(HttpStatusCode.NotFound, response)
            }
        }

        //to delete todo
        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                val response = TodoResponse(false, "id parameter should be a number!", {})
                call.respond(HttpStatusCode.BadRequest, response)
                return@delete
            }
            val removed = repository.removeTodo(todoId)
            if (removed) {
                val response = TodoResponse(true, "Deleted", {})
                call.respond(HttpStatusCode.OK, response)
            } else {
                val response = TodoResponse(false, "Todo not found for id $todoId", {})
                call.respond(HttpStatusCode.NotFound, response)
            }
        }
    }
}


