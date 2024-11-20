package uz.foursquare.todoapp.ui.todolist.components


import com.google.gson.annotations.SerializedName

data class ToDoItemRequest(
    @SerializedName("element")
    val element: TodoItem
)