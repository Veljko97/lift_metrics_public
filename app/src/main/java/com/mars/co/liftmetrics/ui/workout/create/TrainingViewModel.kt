package com.mars.co.liftmetrics.ui.workout.create

import androidx.lifecycle.*
import com.mars.co.liftmetrics.data.model.*
import com.mars.co.liftmetrics.data.repository.ExerciseRepository
import com.mars.co.liftmetrics.data.repository.TrainingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class TrainingViewModel(
    private val trainingRepo: TrainingRepository,
    private val exerciseRepo: ExerciseRepository,
) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>>()
    private val _copiedExercises = MutableLiveData<List<Exercise>>()
    private val _training = MutableLiveData<CompleteTraining?>()

    val exercises get() : LiveData<List<Exercise>> = _exercises
    val copiedExercises get() : LiveData<List<Exercise>> = _copiedExercises
    val training get() : LiveData<CompleteTraining?> = _training

    fun getAllExercises() {
        viewModelScope.launch {
            exerciseRepo.allExercises().collect {
                _exercises.value = it
            }
        }
    }

    fun getTrainingById(trainingId: Long?) {
        viewModelScope.launch {
            if (trainingId != null) {
                _training.value = trainingRepo.getById(trainingId)
                return@launch
            }
            _training.value = null
        }
    }

    fun getExercisesForDate(date: Date) {
        viewModelScope.launch {
            val workouts = trainingRepo.getWorkoutsForDate(date)
            _copiedExercises.value = workouts.map { it.exercise }
        }
    }

    fun storeTraining(date: Date, workouts: Map<Exercise, MutableList<Series>>) {
        viewModelScope.launch {
            trainingRepo.newTraining(NewTraining(date, workouts))
        }
    }

    fun updateTraining(
        training: CompleteTraining,
        workouts: Map<Exercise, MutableList<Series>>,
        deletedWorkouts: List<Long>,
        deletedSeries: List<Long>
    ) {
        viewModelScope.launch {
            trainingRepo.updateTraining(
                UpdatedTraining(training, workouts, deletedWorkouts.toMutableList(), deletedSeries)
            )
        }
    }

    fun noTrainingSet() {
        _training.value = null
    }
}

class TrainingViewModelFactory(
    private val trainingRepo: TrainingRepository,
    private val exerciseRepo: ExerciseRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainingViewModel::class.java)) {
            return TrainingViewModel(trainingRepo, exerciseRepo) as T
        }
        throw IllegalArgumentException("Unimplemented")
    }

}