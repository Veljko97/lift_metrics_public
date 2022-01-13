package com.mars.co.liftmetrics.ui.statistics

import androidx.lifecycle.*
import com.mars.co.liftmetrics.data.model.CompleteWorkout
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.PersonalRecord
import com.mars.co.liftmetrics.data.model.Series
import com.mars.co.liftmetrics.data.repository.ExerciseRepository
import com.mars.co.liftmetrics.data.repository.TrainingRepository
import com.mars.co.liftmetrics.ui.exercises.ExercisesViewModel
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val trainingRepo: TrainingRepository,
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val _usedExercises = MutableLiveData<List<Exercise>>()
    val usedExercise get(): LiveData<List<Exercise>> = _usedExercises

    private val _personalRecords = MutableLiveData<PersonalRecord>()
    val personalRecords get(): LiveData<PersonalRecord> = _personalRecords

    private val _exerciseHistory = MutableLiveData<List<CompleteWorkout>>()
    val exerciseHistory get(): LiveData<List<CompleteWorkout>> = _exerciseHistory

    fun getUsedExercises() {
        viewModelScope.launch {
            _usedExercises.value = exerciseRepository.getUsedExercises()
        }
    }

    fun getPersonalRecords(exercise: Exercise) {
        viewModelScope.launch {
            val topSeries = trainingRepo.getPersonalRecords(exercise.uid)
            _personalRecords.value = PersonalRecord(exercise, topSeries)
        }
    }

    fun getExerciseHistory(exercise: Exercise) {
        viewModelScope.launch {
            _exerciseHistory.value = trainingRepo.getWorkoutHistory(exercise.uid)
        }
    }
}

class StatisticsViewModelFactory(
    private val trainingRepo: TrainingRepository,
    private val exerciseRepository: ExerciseRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(trainingRepo, exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}