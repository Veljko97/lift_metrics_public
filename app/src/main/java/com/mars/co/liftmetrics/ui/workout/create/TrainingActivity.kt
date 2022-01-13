package com.mars.co.liftmetrics.ui.workout.create

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mars.co.liftmetrics.LiftApplication
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.CompleteTraining
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Training
import com.mars.co.liftmetrics.databinding.ActivityTrainingBinding
import com.mars.co.liftmetrics.utilty.ExerciseFormatter
import java.util.*

class TrainingActivity : AppCompatActivity() {

    private val viewModel: TrainingViewModel by viewModels {
        val application: LiftApplication = (applicationContext) as LiftApplication
        TrainingViewModelFactory(application.trainingRepo, application.exerciseRepo)
    }

    private var trainingSoFar: CompleteTraining? = null
    private lateinit var trainingAdapter: TrainingRecyclerViewAdapter
    private var calendar = Calendar.getInstance()

    private lateinit var exercises: List<Exercise>
    private var dataChanged: Boolean = false

    private var _binding: ActivityTrainingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        handleIntentData(intent)

        setupTrainingObserver()
        setupCopiedExercisesObserver()
        setupTrainingDone()

        viewModel.getAllExercises()
        setupExercises()

        setContentView(binding.root)
    }

    private fun handleIntentData(intent: Intent) {
        if (intent.extras != null && intent.hasExtra(TRAINING_ID_KEY)) {
            val trainingId = intent.extras!!.getLong(TRAINING_ID_KEY)
            viewModel.getTrainingById(trainingId)
            return
        }
        if (intent.extras != null && intent.hasExtra(COPY_TRAINING_YEAR)) {
            val calendar = Calendar.getInstance()
            val year = intent.extras!!.getInt(COPY_TRAINING_YEAR)
            val month = intent.extras!!.getInt(COPY_TRAINING_MONTH)
            val day = intent.extras!!.getInt(COPY_TRAINING_DAY)
            calendar.set(year, month, day)
            viewModel.getExercisesForDate(calendar.time)
        }
        viewModel.noTrainingSet()

    }

    private fun setupExercises() {
        viewModel.exercises.observe(this, {
            if (it.isEmpty()) {
                return@observe
            }
            exercises = it
            setupAddExercise()
        })
    }

    private fun setupCopiedExercisesObserver() {
        viewModel.copiedExercises.observe(this, { exercises ->
            if (exercises.isEmpty()) {
                return@observe
            }
            exercises.forEach { trainingAdapter.addExercise(it) }
        })
    }

    private fun setupTrainingObserver() {
        viewModel.training.observe(this) {
            trainingSoFar = it
            setupTrainingExercise()
        }
    }

    private fun setupTrainingDone() {
        binding.trainingDone.visibility = View.GONE
        binding.trainingDone.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupAddExercise() {
        binding.addExercise.visibility = View.VISIBLE
        binding.addExercise.setOnClickListener {
            showAddExerciseDialog()
        }
    }

    private fun setupTrainingExercise() {
        with(binding.workoutsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            trainingAdapter = TrainingRecyclerViewAdapter(context, trainingSoFar?.workouts) {
                dataChanged = true
                binding.trainingDone.visibility = View.VISIBLE
            }
            adapter = trainingAdapter
        }
    }

    private fun saveChanges() {
        dataChanged = false
        val workouts = trainingAdapter.getTrainingData
        if (trainingSoFar != null) {
            val deletedWorkouts = trainingAdapter.getDeletedWorkouts
            val deletedSeries = trainingAdapter.getDeletedSeries
            viewModel.updateTraining(trainingSoFar!!, workouts, deletedWorkouts, deletedSeries)
        } else {
            viewModel.storeTraining(calendar.time, workouts)
        }
        onBackPressed()
    }

    private fun showAddExerciseDialog() {
        val builder = AlertDialog.Builder(this)
        val autoComplete = AppCompatAutoCompleteTextView(this)
        autoComplete.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                exercises.map { exercise -> ExerciseFormatter(exercise, this) }
            )
        )
        builder.setView(autoComplete)
        val dialog = builder.create()
        autoComplete.setOnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition(position)
            if (item is ExerciseFormatter) {
                trainingAdapter.addExercise(item.exercise)
            }
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        val message = TextView(this)
        message.setText(R.string.training_changed_message)
        builder.setView(message)

        builder.setPositiveButton(R.string.save) { _, _ ->
            saveChanges()
        }
        builder.setNegativeButton(R.string.discard) { dialog, _ ->
            dialog.cancel()
            dataChanged = false
            onBackPressed()
        }
        val dialog = builder.create()


        dialog.show()
    }

    override fun onBackPressed() {
        if (dataChanged) {
            showConfirmDialog()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TRAINING_ID_KEY = "trainingId"
        const val COPY_TRAINING_YEAR = "copyTrainingYear"
        const val COPY_TRAINING_MONTH = "copyTrainingMonth"
        const val COPY_TRAINING_DAY = "copyTrainingDay"
    }
}