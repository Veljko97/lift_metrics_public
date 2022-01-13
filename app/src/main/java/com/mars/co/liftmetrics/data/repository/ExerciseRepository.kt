package com.mars.co.liftmetrics.data.repository

import androidx.lifecycle.LiveData
import com.mars.co.liftmetrics.data.dao.ExerciseDao
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Muscle
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    fun allExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAll()
    }

    fun getByMuscle(muscle: Muscle): Flow<List<Exercise>> {
        return exerciseDao.getByMuscle(muscle)
    }

    suspend fun getUsedExercises(): List<Exercise> {
        return exerciseDao.getUsedExercises()
    }

    suspend fun storeExercise(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }
}