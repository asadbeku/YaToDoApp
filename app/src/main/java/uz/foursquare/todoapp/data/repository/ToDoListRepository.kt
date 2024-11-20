import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.foursquare.todoapp.data.ToDoListDataSource
import uz.foursquare.todoapp.data.network.ApiService
import uz.foursquare.todoapp.ui.todolist.components.TodoItem

class ToDoListRepository(private val apiService: ApiService, context: Context) {

    private val sharedPreferences = context.getSharedPreferences("todo_list", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private var lastKnownRevision: Int
        get() = sharedPreferences.getInt("lastKnownRevision", 0)
        set(value) {
            editor.putInt("lastKnownRevision", value).apply()
        }

    private inner class NetworkDataSource : ToDoListDataSource {
        override suspend fun getTasks(): List<TodoItem> {
            val response = apiService.getTasks()
            if (response.isSuccessful) {
                val tasks = response.body()?.list ?: emptyList()
                lastKnownRevision = response.body()?.revision ?: lastKnownRevision
                return tasks
            } else {
                throw NetworkException("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        }

        override suspend fun addTask(note: TodoItem) {

            getTasks()

            val jsonObject = JsonObject().apply { add("element", Gson().toJsonTree(note)) }
            val response = apiService.addTask(lastKnownRevision.toString(), jsonObject)
            if (!response.isSuccessful) {
                throw NetworkException("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        }

        override suspend fun deleteTask(id: String) {
            Log.d("ToDoListRepository", "Deleting task with id: $id")
            getTasks()
            val response =
                apiService.removeTask(id = id, lastKnownRevision = lastKnownRevision.toString())
            if (!response.isSuccessful) {
                throw NetworkException("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        }

        override suspend fun updateTask(note: TodoItem) {
            getTasks()
            val jsonObject = JsonObject().apply { add("element", Gson().toJsonTree(note)) }
            val response = apiService.updateTask(lastKnownRevision.toString(), note.id, jsonObject)
            if (!response.isSuccessful) {
                throw NetworkException("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        }

        override suspend fun getTaskById(id: String): TodoItem? {
            val response = apiService.getTaskById(lastKnownRevision.toString(), id)
            return if (response.isSuccessful) response.body()?.element else null
        }
    }

    private inner class LocalDataSource : ToDoListDataSource {
        private val cachedTasks: MutableList<TodoItem> = mutableListOf()

        override suspend fun getTasks(): List<TodoItem> {
            if (cachedTasks.isEmpty()) {
                cachedTasks.addAll(networkDataSource.getTasks())
            }
            return cachedTasks.toList()
        }

        override suspend fun addTask(note: TodoItem) {
            networkDataSource.addTask(note)
            cachedTasks.add(note)
        }

        override suspend fun deleteTask(id: String) {
            networkDataSource.deleteTask(id)
            cachedTasks.removeIf { it.id == id }
        }

        override suspend fun updateTask(note: TodoItem) {
            networkDataSource.updateTask(note)
            val index = cachedTasks.indexOfFirst { it.id == note.id }
            if (index != -1) {
                cachedTasks[index] = note
            }
        }

        override suspend fun getTaskById(id: String): TodoItem? {
            return cachedTasks.find { it.id == id } ?: networkDataSource.getTaskById(id)
        }
    }

    private val networkDataSource = NetworkDataSource()
    private val localDataSource = LocalDataSource()

    private val dataSource: ToDoListDataSource
        get() = localDataSource // or networkDataSource

    fun getTasks(): Flow<List<TodoItem>> = flow {
        emit(dataSource.getTasks())
    }

    suspend fun addTask(note: TodoItem) {
        dataSource.addTask(note)
    }

    suspend fun deleteTask(id: String) {
        dataSource.deleteTask(id)
    }

    suspend fun updateTask(note: TodoItem) {
        dataSource.updateTask(note)
    }

    suspend fun getTaskById(id: String): TodoItem? {
        return dataSource.getTaskById(id)
    }
}

// NetworkException.kt
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)