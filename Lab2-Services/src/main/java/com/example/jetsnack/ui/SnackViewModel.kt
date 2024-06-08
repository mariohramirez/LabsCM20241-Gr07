
package com.example.jetsnack.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetsnack.data.SnackRepository
import com.example.jetsnack.model.Snack
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

//Se crea la interface que servira para saber en que estado de carga estan los datos
sealed interface SnackUiState {
    data class Success(
        val snacks: List<Snack>
    ): SnackUiState
    object Error : SnackUiState
    object Loading : SnackUiState
}

//Se encargara de manejar la carga de los datos
class SnackViewModel(private val snackRepository: SnackRepository): ViewModel() {
    //NOs servira para rastrear el estado de la carga de los datos
    var snackUiState: SnackUiState by mutableStateOf(SnackUiState.Loading)
        private set

    init {
        //Se obtienen los datos
        getData()
    }

    fun getData(){
        Log.i("Prueba", "Obteniendo datos")
        //En el alcance del viewModel
        viewModelScope.launch {
            //Inicia en un estado de carga
            snackUiState = SnackUiState.Loading
            try {
                Log.i("Prueba", "Cargando datos")
                //El estadio pasa a ser exitoso
                snackUiState = SnackUiState.Success(snackRepository.getSnacks())
                Log.i("Prueba", "Exito cargando datos")
                Log.i("Prueba", snackUiState.toString())
            } catch (e: IOException) {
                Log.i("Prueba", "IOException...")
                Log.i("Prueba", e.message.toString())
                snackUiState = SnackUiState.Error
            } catch (e: HttpException) {
                Log.i("Prueba", "HttpException")
                Log.i("Prueba", e.message.toString())
                snackUiState = SnackUiState.Error
            }
            Log.i("Prueba", "Finalizada la carga de datos")
        }
    }

    //Se obtienen los snacks
    fun getSnacks(): List<Snack> {
        return when (snackUiState) {
            is SnackUiState.Success -> (snackUiState as SnackUiState.Success).snacks
            else -> emptyList()
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SnackApplication)
                val snackRepository = application.container.snackRepository
                SnackViewModel(snackRepository = snackRepository)
            }
        }
    }
}
