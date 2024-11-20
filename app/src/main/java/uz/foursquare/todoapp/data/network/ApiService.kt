package uz.foursquare.todoapp.data.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uz.foursquare.todoapp.ui.todolist.components.ToDoEditedResponse
import uz.foursquare.todoapp.ui.todolist.components.ToDoItemsResponse

interface ApiService {

    @GET("list")
    suspend fun getTasks(): Response<ToDoItemsResponse>

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Body task: JsonObject
    ): Response<Any>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String,
        @Body task: JsonObject
    ): Response<Any>

    @DELETE("list/{id}")
    suspend fun removeTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String
    ): Response<Any>

    @GET("list/{id}")
    suspend fun getTaskById(
        @Header("X-Last-Known-Revision") lastKnownRevision: String,
        @Path("id") id: String
    ): Response<ToDoEditedResponse>
}