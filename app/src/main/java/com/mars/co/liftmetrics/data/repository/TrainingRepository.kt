package com.mars.co.liftmetrics.data.repository

import com.mars.co.liftmetrics.data.dao.TrainingDao
import com.mars.co.liftmetrics.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class TrainingRepository(private val dao: TrainingDao) {

    suspend fun getAll(): List<CompleteTraining> {
        return dao.getAll()
    }

    fun getByDate(date: Date): Flow<List<CompleteTraining>> {
        return dao.getByDate(date)
    }

    suspend fun newTraining(newTraining: NewTraining) {
        dao.insertNewTraining(newTraining)
    }

    suspend fun updateTraining(updatedTraining: UpdatedTraining) {
        dao.updateTraining(updatedTraining)
    }

    suspend fun getById(trainingId: Long): CompleteTraining {
        return dao.getTrainingById(trainingId)
    }

    suspend fun getWorkoutsForDate(date: Date): List<CompleteWorkout> {
        return dao.getExercisesByDate(date)
    }

    suspend fun getPersonalRecords(exerciseId: Long): Series {
        return dao.getPersonalRecords(exerciseId)
    }

    suspend fun getWorkoutHistory(exerciseId: Long): List<CompleteWorkout> {
        return dao.getWorkoutHistory(exerciseId)
    }
}