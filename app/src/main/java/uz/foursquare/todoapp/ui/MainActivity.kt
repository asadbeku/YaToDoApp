package uz.foursquare.todoapp.ui

import ToDoListRepository
import uz.foursquare.todoapp.ui.todolist.ToDoListViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.foursquare.todoapp.data.network.ApiService
import uz.foursquare.todoapp.data.network.Network
import uz.foursquare.todoapp.ui.note.NotesScreen
import uz.foursquare.todoapp.ui.note.NotesViewModel
import uz.foursquare.todoapp.ui.todolist.ToDoListScreen
import uz.foursquare.todoapp.utils.NotesViewModelFactory
import uz.foursquare.todoapp.utils.ToDoListViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            val viewModel: ToDoListViewModel = viewModel(
                factory = ToDoListViewModelFactory(
                    repository = ToDoListRepository(
                        Network.buildService(ApiService::class.java),
                        applicationContext
                    )
                )
            )
            val notesViewModel: NotesViewModel = viewModel(
                factory = NotesViewModelFactory(
                    repository = ToDoListRepository(
                        Network.buildService(ApiService::class.java),
                        applicationContext
                    )
                )
            )

            NavHost(navController = navController, startDestination = "todoListScreen") {
                composable("todoListScreen") {
                    ToDoListScreen(
                        viewModel = viewModel,
                        navController = navController,
                        context = applicationContext
                    )
                }

                composable("notesScreen/{taskId}") { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId")
                    NotesScreen(
                        navController = navController,
                        viewModel = notesViewModel,
                        taskId = taskId
                    )
                }
            }
        }
    }
}