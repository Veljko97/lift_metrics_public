package com.mars.co.liftmetrics.data.model

import androidx.room.*
import java.util.*

@Entity
class Workout(
    @ColumnInfo val exerciseId: Long,
    @ColumnInfo val trainingId: Long,
    @ColumnInfo val recordDate: Date
) {
    @PrimaryKey(autoGenerate = true)
    var workoutId: Long = 0
}

@Entity(primaryKeys = ["workoutId", "seriesId"])
data class WorkoutSeriesCrossRef(
    val workoutId: Long,
    val seriesId: Long
)

data class CompleteWorkout(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "uid"
    ) val exercise: Exercise,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "seriesId",
        associateBy = Junction(WorkoutSeriesCrossRef::class)
    )
    val series: List<Series>
)