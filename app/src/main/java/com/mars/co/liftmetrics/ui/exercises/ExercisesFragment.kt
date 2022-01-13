package com.mars.co.liftmetrics.ui.exercises

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mars.co.liftmetrics.LiftApplication
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Muscle
import com.mars.co.liftmetrics.data.model.MusclesConverter
import com.mars.co.liftmetrics.databinding.FragmentExercisesBinding
import com.mars.co.liftmetrics.ui.exercises.create.CreateExerciseActivity

class ExercisesFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val viewModel: ExercisesViewModel by viewModels {
        val application: LiftApplication = (context?.applicationContext) as LiftApplication
        ExercisesViewModelFactory(application.exerciseRepo)
    }
    private var _binding: FragmentExercisesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        viewModel.getAllExercises()
        binding.addExercise.setOnClickListener {
            val intent = Intent(context, CreateExerciseActivity::class.java)
            startActivity(intent)
        }
        setupExercisesObserver()
        setupMuscleSpinner()
        return binding.root
    }

    private fun setupExercisesObserver() {
        viewModel.allExercises.observe(viewLifecycleOwner, {
            // Set the adapter
            with(binding.list) {
                if (adapter != null && adapter is ExercisesRecyclerViewAdapter) {
                    (adapter as ExercisesRecyclerViewAdapter).updateData(it)
                    return@with
                }
                layoutManager = LinearLayoutManager(context)
                adapter = ExercisesRecyclerViewAdapter(context, it)
            }
        })
    }

    private fun setupMuscleSpinner() {

        val values = mutableListOf(
            getString(R.string.all),
        )
        values.addAll(Muscle.values().map {
            getString(MusclesConverter.toStringRes(it))
        })
        with(binding.selectMuscle) {
            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                values
            )
            this.onItemSelectedListener = this@ExercisesFragment
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0){
            viewModel.getAllExercises()
            return
        }
        // Account for added. ALL
        viewModel.getByMuscle(Muscle.values()[position - 1])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}