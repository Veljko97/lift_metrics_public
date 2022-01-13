package com.mars.co.liftmetrics.ui.workout

import androidx.lifecycle.*
import com.mars.co.liftmetrics.data.model.CompleteTraining
import com.mars.co.liftmetrics.data.repository.TrainingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class WorkoutViewModel(private val repo: TrainingRepository) : ViewModel() {
    private val _workouts = MutableLiveData<List<CompleteTraining>>()

    val workouts get(): LiveData<List<CompleteTraining>> = _workouts

    fun getByDate(date: Date) {
        viewModelScope.launch {
            repo.getByDate(date).collect {
                _workouts.value = it
            }
        }
    }
}

class WorkoutViewModelFactory(private val repo: TrainingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            return WorkoutViewModel(repo) as T
        }
        throw IllegalArgumentException("Unimplemented")
    }
}