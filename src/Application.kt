package com.todoapp

import com.todoapp.entities.ToDo
import com.todoapp.entities.ToDoDraft
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
            call.respond(todo)
        }

        // to get all todo list
        get("/todos") {
            call.respond(repository.getAllToDos())
        }

        // to get single todo
        get("/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "id parameter has to be a number"
                )
                return@get
            }
            val todo = repository.getToDo(id)
            if (todo == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Todo not found"
                )
            } else {
                call.respond(todo)
            }
        }

        // to update todo
        put("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            val toDoDraft = call.receive<ToDoDraft>()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter should be a number!")
                return@put

            }

            val updated = repository.updateTodo(todoId, toDoDraft)
            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "found no todo with this id $todoId")
            }
        }

        //to delete todo
        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter should be a number!")
                return@delete
            }
            val removed = repository.removeTodo(todoId)
            if (removed) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found for id $todoId")
            }
        }
    }
}


