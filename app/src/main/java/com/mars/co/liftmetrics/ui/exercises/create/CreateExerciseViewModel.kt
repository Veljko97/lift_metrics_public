package com.mars.co.liftmetrics.ui.exercises.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Muscle
import com.mars.co.liftmetrics.data.repository.ExerciseRepository
import com.mars.co.liftmetrics.ui.workout.create.TrainingViewModel
import kotlinx.coroutines.launch

class CreateExerciseViewModel(private val repo: ExerciseRepository) : ViewModel() {

    fun storeExercise(
        muscles: Set<Muscle>,
        name: String,
        description: String,
        imageDepiction: String?,
        videoLink: String?
    ) {
        viewModelScope.launch {
            repo.storeExercise(
                Exercise(
                    muscles,
                    name,
                    description,
                    imageDepiction,
                    videoLink
                )
            )
        }
    }
}

class CreateExerciseViewModelFactory(private val repo: ExerciseRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateExerciseViewModel::class.java)) {
            return CreateExerciseViewModel(repo) as T
        }
        throw IllegalArgumentException("Unimplemented")
    }
}