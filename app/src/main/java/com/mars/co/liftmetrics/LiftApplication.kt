package com.mars.co.liftmetrics

import android.app.Application
import com.mars.co.liftmetrics.data.AppDatabase
import com.mars.co.liftmetrics.data.repository.ExerciseRepository
import com.mars.co.liftmetrics.data.repository.TrainingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LiftApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { AppDatabase.getInstance(this, applicationScope) }
    val exerciseRepo by lazy { ExerciseRepository(db.exerciseDao()) }
    val trainingRepo by lazy { TrainingRepository(db.trainingDao()) }
}