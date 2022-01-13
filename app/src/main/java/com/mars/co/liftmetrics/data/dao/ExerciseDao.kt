package com.mars.co.liftmetrics.data.dao

import androidx.room.*
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Muscle
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    fun getAll(): Flow<List<Exercise>>

    // Muscles are a string in DB and it can be bigger the one muscle
    // https://stackoverflow.com/a/60652893
    @Transaction
    @Query("SELECT * FROM exercise WHERE muscles LIKE '%' || :muscle || '%'")
    fun getByMuscle(muscle: Muscle): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE uid IN (SELECT exerciseId FROM workout)")
    suspend fun getUsedExercises(): List<Exercise>

    @Insert
    suspend fun insertAll(vararg items: Exercise)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    companion object {
        suspend fun initialData(dao: ExerciseDao) {
            dao.insertAll(
                Exercise(
                    muscles = setOf(Muscle.CHEST),
                    name = "exercise_name_barbell_bench_press",
                    description = "exercise_desc_barbell_bench_press",
                    videoLink = "https://www.youtube.com/watch?v=tuwHzzPdaGc&ab_channel=Muscle%26Strength"
                ),
                Exercise(
                    muscles = setOf(Muscle.CHEST),
                    name = "exercise_name_standing_cable_fly",
                    description = "exercise_desc_standing_cable_fly",
                    videoLink = "https://www.youtube.com/watch?v=OPYrUGZL8nU&ab_channel=Muscle%26Strength"
                ),
                Exercise(
                    muscles = setOf(Muscle.BICEPS),
                    name = "exercise_name_standing_cable_fly",
                    description = "exercise_desc_standing_cable_fly",
                    videoLink = "https://www.youtube.com/watch?v=UeleXjsE-98&ab_channel=Muscle%26Strength"
                ),
                Exercise(
                    muscles = setOf(Muscle.BICEPS),
                    name = "exercise_name_dumbbell_hammer_preacher_curl",
                    description = "exercise_desc_dumbbell_hammer_preacher_curl",
                    videoLink = "https://www.youtube.com/watch?v=ZdcFOgFi1Dg&ab_channel=Muscle%26Strength"
                ),
                Exercise(
                    muscles = setOf(Muscle.TRICEPS),
                    name = "exercise_name_ez_bar_skullcrusher",
                    description = "exercise_desc_ez_bar_skullcrusher",
                    videoLink = "https://www.youtube.com/watch?v=K6MSN4hCDM4&ab_channel=Muscle%26Strength"
                ),
                Exercise(
                    muscles = setOf(Muscle.TRICEPS),
                    name = "exercise_name_rope_tricep_extension",
                    description = "exercise_desc_rope_tricep_extension",
                    videoLink = "https://www.youtube.com/watch?v=LzwgB15UdO8&ab_channel=Muscle%26Strength"
                ),
            )
        }
    }
}
