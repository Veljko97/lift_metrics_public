package com.mars.co.liftmetrics.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mars.co.liftmetrics.data.dao.ExerciseDao
import com.mars.co.liftmetrics.data.dao.TrainingDao
import com.mars.co.liftmetrics.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [
        Exercise::class,
        Training::class,
        Workout::class,
        Series::class,
        WorkoutSeriesCrossRef::class,
    ], version = 1
)
@TypeConverters(MusclesConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingDao(): TrainingDao

    private class DataCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    ExerciseDao.initialData(database.exerciseDao())
//                    TrainingDao.initData(database.trainingDao())
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )
                    .addCallback(DataCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DateConverter {

    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun toDate(value: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        //Date is from db so it will have right format
        return formatter.parse(value)!!
    }

    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun fromDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(date)
    }
}