package com.mars.co.liftmetrics.data.dao

import androidx.room.*
import com.mars.co.liftmetrics.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.NoSuchElementException

@Dao
interface TrainingDao {
    @Transaction
    @Query("SELECT * FROM training")
    suspend fun getAll(): List<CompleteTraining>

    @Transaction
    @Query("SELECT * FROM training WHERE :date LIKE date")
    fun getByDate(date: Date): Flow<List<CompleteTraining>>

    @Transaction
    @Query("SELECT * FROM training WHERE :trainingId == uid")
    suspend fun getTrainingById(trainingId: Long): CompleteTraining

    @Transaction
    @Query("SELECT * FROM Workout")
    suspend fun getAllSeries(): List<CompleteWorkout>

    @Transaction
    @Query("SELECT *,MAX(weight) FROM series WHERE seriesId IN (SELECT ref.seriesId FROM workoutseriescrossref ref WHERE ref.workoutId IN (SELECT workoutId FROM workout WHERE exerciseId == :exerciseId))")
    suspend fun getPersonalRecords(exerciseId: Long): Series

    @Transaction
    @Query("SELECT * FROM workout WHERE exerciseId == :exerciseId ORDER BY recordDate")
    suspend fun getWorkoutHistory(exerciseId: Long): List<CompleteWorkout>

    @Transaction
    @Query("SELECT * FROM workout WHERE recordDate == :date")
    suspend fun getExercisesByDate(date: Date): List<CompleteWorkout>

    @Insert
    suspend fun insertTraining(training: Training): Long

    @Query("DELETE FROM training WHERE uid == :id")
    suspend fun deleteTrainingById(id: Long)

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("DELETE FROM workout WHERE workoutId IN (:workouts)")
    suspend fun deleteWorkoutByIds(workouts: List<Long>)

    @Insert
    suspend fun insertSeries(series: Series): Long

    @Insert
    suspend fun insertAllSeries(series: List<Series>): List<Long>

    @Update
    suspend fun updateSeries(series: List<Series>)

    @Delete
    suspend fun deleteSeries(series: List<Series>)

    @Query("DELETE FROM series WHERE seriesId IN (:series)")
    suspend fun deleteSeriesByIds(series: List<Long>)

    @Insert
    suspend fun insertCrossRef(crossRef: WorkoutSeriesCrossRef): Long

    @Query("DELETE FROM workoutseriescrossref WHERE workoutId IN (:workoutIds)")
    suspend fun deleteCrossRefByWorkouts(workoutIds: List<Long>)

    @Insert
    suspend fun insertAll(vararg trainings: Training)

    @Transaction
    suspend fun insertNewTraining(newTraining: NewTraining) {
        val trainingId = insertTraining(Training(newTraining.date))
        var hasWorkout = false
        for (workout in newTraining.workouts.entries) {
            if (workout.value.isEmpty()) {
                continue
            }
            hasWorkout = true
            val workoutId = insertWorkout(Workout(workout.key.uid, trainingId, newTraining.date))
            val series = insertAllSeries(workout.value)
            series.forEach {
                insertCrossRef(WorkoutSeriesCrossRef(workoutId, it))
            }
        }
        if (!hasWorkout) {
            deleteTrainingById(trainingId)
        }
    }

    @Transaction
    suspend fun updateTraining(updatedTraining: UpdatedTraining) {
        val trainingId = updatedTraining.initialTraining.training.uid
        for (workout in updatedTraining.workouts.entries) {
            val workoutId: Long = try {
                updatedTraining.initialTraining.workouts.first {
                    workout.key.uid == it.exercise.uid
                }.workout.workoutId
            } catch (e: NoSuchElementException) {
                insertWorkout(
                    Workout(
                        workout.key.uid,
                        trainingId,
                        updatedTraining.initialTraining.training.date
                    )
                )
            }
            if (workout.value.isEmpty()) {
                updatedTraining.deletedWorkouts.add(workoutId)
                continue
            }
            updateSeries(workout.value.filter {
                it.seriesId != null
            })
            val series = insertAllSeries(
                workout.value.filter {
                    it.seriesId == null
                }
            )
            series.forEach {
                insertCrossRef(WorkoutSeriesCrossRef(workoutId, it))
            }
        }
        handleDeletes(updatedTraining.deletedWorkouts, updatedTraining.deletedSeries)
    }

    // Used only for testing to have an ini
    suspend fun handleDeletes(deletedWorkouts: List<Long>, deletedSeries: List<Long>) {
        deleteCrossRefByWorkouts(deletedWorkouts)
        deleteSeriesByIds(deletedSeries)
        deleteWorkoutByIds(deletedWorkouts)
    }

    companion object {
        suspend fun initData(dao: TrainingDao) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -22)
            for (i in 0..20) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val trainingId = dao.insertTraining(Training(calendar.time))
                for (j in 1..2) {
                    val workoutId =
                        dao.insertWorkout(Workout(j.toLong(), trainingId, calendar.time))
                    val seriesIds = dao.insertAllSeries(
                        listOf(
                            Series(12, 30.0 + i),
                            Series(10, 35.0 + i),
                            Series(10, 40.0 + i),
                            Series(8, 45.0 + i)
                        )
                    )
                    for (id in seriesIds) {
                        dao.insertCrossRef(WorkoutSeriesCrossRef(workoutId, id))
                    }
                }
            }
        }
    }
}