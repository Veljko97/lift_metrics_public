package com.mars.co.liftmetrics.data.model

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity
class Training(
    @ColumnInfo val date: Date
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
}

data class CompleteTraining(
    @Embedded val training: Training,
    @Relation(
        entity = Workout::class,
        parentColumn = "uid",
        entityColumn = "trainingId"
    ) val workouts: List<CompleteWorkout>
) : Serializable

data class NewTraining(
    val date: Date,
    val workouts: Map<Exercise, MutableList<Series>>
)

data class UpdatedTraining(
    val initialTraining: CompleteTraining,
    val workouts: Map<Exercise, MutableList<Series>>,
    val deletedWorkouts: MutableList<Long>,
    val deletedSeries: List<Long>
)