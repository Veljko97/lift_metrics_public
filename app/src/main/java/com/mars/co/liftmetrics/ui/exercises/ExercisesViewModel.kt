package com.mars.co.liftmetrics.ui.exercises

import android.util.Log
import androidx.lifecycle.*
import com.mars.co.liftmetrics.data.AppDatabase
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Muscle
import com.mars.co.liftmetrics.data.repository.ExerciseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class ExercisesViewModel(private val repo: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>>().apply {
        value = arrayListOf()
    }

    val allExercises get(): LiveData<List<Exercise>> = _exercises

    fun getAllExercises() {
        viewModelScope.launch {
            repo.allExercises().collect {
                _exercises.value = it
            }
        }
    }

    fun getByMuscle(muscle: Muscle) {
        viewModelScope.launch {
            repo.getByMuscle(muscle).collect {
                _exercises.value = it
            }
        }
    }
}

class ExercisesViewModelFactory(private val repo: ExerciseRepository) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ExercisesViewModel::class.java)){
            return ExercisesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}