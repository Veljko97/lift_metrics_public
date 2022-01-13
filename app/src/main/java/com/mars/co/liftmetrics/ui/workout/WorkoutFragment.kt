package com.mars.co.liftmetrics.ui.workout

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mars.co.liftmetrics.LiftApplication
import com.mars.co.liftmetrics.data.model.CompleteTraining
import com.mars.co.liftmetrics.databinding.FragmentWorkoutBinding
import com.mars.co.liftmetrics.databinding.NewTrainingDialogBinding
import com.mars.co.liftmetrics.ui.workout.create.TrainingActivity
import java.util.*

class WorkoutFragment : Fragment() {

    private val viewModel: WorkoutViewModel by viewModels {
        val application: LiftApplication = (context?.applicationContext) as LiftApplication
        WorkoutViewModelFactory(application.trainingRepo)
    }
    private var _binding: FragmentWorkoutBinding? = null

    private val binding get() = _binding!!
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTraining: CompleteTraining? = null
    private var workoutRecyclerViewAdapter: WorkoutRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater)
        viewModel.getByDate(selectedDate.time)
        setupStartTraining(selectedDate.time)
        setupWorkoutsObserver()
        setupCalendarView()
        setupRecycleView()
        return binding.root
    }

    private fun setupRecycleView() {
        with(binding.workoutsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            workoutRecyclerViewAdapter = WorkoutRecyclerViewAdapter(context)
            adapter = workoutRecyclerViewAdapter
        }
    }

    private fun setupWorkoutsObserver() {
        viewModel.workouts.observe(viewLifecycleOwner, {
            // Possible to add more the one training per day
            if (it.isEmpty()) {
                selectedTraining = null
                binding.workoutsRecyclerView.visibility = View.GONE
                return@observe
            }
            binding.workoutsRecyclerView.visibility = View.VISIBLE
            selectedTraining = it.first()
            workoutRecyclerViewAdapter?.updateWorkouts(selectedTraining!!.workouts)
        })
    }

    private fun setupCalendarView() {
        binding.workoutDates.maxDate = Date().time
        binding.workoutDates.setOnDateChangeListener { _, year, month, day ->
            selectedDate.set(year, month, day)
            viewModel.getByDate(selectedDate.time)
            setupStartTraining(selectedDate.time)
        }
    }

    private fun setupStartTraining(forDate: Date) {
        binding.startTraining.setOnClickListener {
            val intent = Intent(activity, TrainingActivity::class.java)
            if (selectedTraining != null) {
                intent.putExtra(TrainingActivity.TRAINING_ID_KEY, selectedTraining!!.training.uid)
                activity?.startActivity(intent)
                return@setOnClickListener
            }
            showTrainingDialog(intent)
        }
        binding.startTraining.visibility = if (DateUtils.isToday(forDate.time)) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


    private fun showTrainingDialog(intent: Intent) {
        if (context == null) {
            return
        }
        val binder = NewTrainingDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binder.root)

        val dialog = builder.create()

        val startYear = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)


        binder.copyDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    intent.putExtra(TrainingActivity.COPY_TRAINING_YEAR, year)
                    intent.putExtra(TrainingActivity.COPY_TRAINING_MONTH, monthOfYear)
                    intent.putExtra(TrainingActivity.COPY_TRAINING_DAY, dayOfMonth)
                    activity?.startActivity(intent)
                    dialog.cancel()
                }, startYear, month, day
            ).show()
        }
        binder.newTraining.setOnClickListener {
            activity?.startActivity(intent)
            dialog.cancel()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}