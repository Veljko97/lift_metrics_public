package com.mars.co.liftmetrics.ui.exercises.create

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mars.co.liftmetrics.LiftApplication
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Muscle
import com.mars.co.liftmetrics.databinding.ActivityCreateExerciseBinding

class CreateExerciseActivity : AppCompatActivity() {

    private val viewModel: CreateExerciseViewModel by viewModels {
        val application: LiftApplication = (applicationContext) as LiftApplication
        CreateExerciseViewModelFactory(application.exerciseRepo)
    }

    private var imageUri: Uri? = null
    private lateinit var muscleAdapter: MuscleAdapter
    private var spinnerTextColor: Int? = null

    private var _binding: ActivityCreateExerciseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateExerciseBinding.inflate(
            layoutInflater
        )
        setupMuscleSelect()
        setupImagePicker()
        setupAddExercise()
        setContentView(binding.root)
    }

    private fun setupMuscleSelect() {
        muscleAdapter = MuscleAdapter(
            this,
            R.layout.spinner_check_item,
            Muscle.values().map {
                CheckableMuscle(false, it)
            }
        )
        binding.muscleSpinner.adapter = muscleAdapter
    }

    private fun setupImagePicker() {
        val startForImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val resultCode = it.resultCode
                val data = it.data
                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    imageUri = data?.data!!
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.exerciseImage)
                }
            }
        binding.exerciseImage.setOnClickListener {
            ImagePicker.with(this)
                .createIntent { intent ->
                    startForImage.launch(intent)
                }
        }
    }

    private fun setupAddExercise() {
        binding.addExercise.setOnClickListener {
            var hasError = false
            val exerciseName = binding.exerciseName.text
            if (exerciseName.isNullOrBlank()) {
                binding.exerciseName.error = getString(R.string.non_empty_field)
                hasError = true
            } else {
                binding.exerciseName.error = null
            }
            val exerciseDescription = binding.exerciseDescription.text
            if (exerciseDescription.isNullOrBlank()) {
                binding.exerciseDescription.error = getString(R.string.non_empty_field)
                hasError = true
            } else {
                binding.exerciseDescription.error = null
            }
            val muscles = muscleAdapter.getSelectedMuscles()
            val item = binding.muscleSpinner.selectedView
            val errorView = item.findViewById<TextView>(R.id.title)
            if (muscles.isEmpty()) {
                errorView.text = getString(R.string.select_one_muscle_group)
                //Set only first time we get to this point
                spinnerTextColor = spinnerTextColor ?: errorView.currentTextColor
                errorView.setTextColor(Color.RED)
                hasError = true
            } else {
                errorView.text = getString(R.string.select_muscle_grups)
                if(spinnerTextColor != null){
                    errorView.setTextColor(spinnerTextColor!!)
                }

            }
            if (hasError) {
                return@setOnClickListener
            }
            viewModel.storeExercise(
                muscles,
                exerciseName.toString(),
                exerciseDescription.toString(),
                imageUri?.path,
                binding.exerciseVideo.text.toString()
            )
            onBackPressed()
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}