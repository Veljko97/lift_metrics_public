package com.mars.co.liftmetrics.ui.workout.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.CompleteWorkout
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Series
import com.mars.co.liftmetrics.databinding.TrainingItemBinding
import com.mars.co.liftmetrics.utilty.ExerciseFormatter

class TrainingRecyclerViewAdapter(
    private val context: Context,
    workouts: List<CompleteWorkout>?,
    private val changeHappened: () -> Unit
) : RecyclerView.Adapter<TrainingRecyclerViewAdapter.TrainingViewHolder>() {

    private var selectedPosition: Int? = null
    private val workout: MutableMap<Exercise, MutableList<Series>> = mutableMapOf()
    private val workoutIds: MutableMap<Exercise, Long> = mutableMapOf()
    private val exercises: MutableList<Exercise> = mutableListOf()

    // Only ones that are not saved in local db can be deleted
    private val deletedWorkouts: MutableList<Long> = mutableListOf()
    private val deletedSeries: MutableList<Long> = mutableListOf()

    init {
        workouts?.forEach {
            exercises.add(0,it.exercise)
            workoutIds[it.exercise] = it.workout.workoutId
            workout[it.exercise] = it.series.toMutableList()
        }
    }

    fun addExercise(exercise: Exercise) {
        changeHappened()
        if (workout.containsKey(exercise)) {
            selectedPosition = exercises.indexOf(exercise)
            notifyItemChanged(selectedPosition!!)
            return
        }
        exercises.add(0, exercise)
        workout[exercise] = mutableListOf()
        selectedPosition = 0
        notifyItemInserted(0)
        notifyItemChanged(1)
    }

    val getTrainingData get(): Map<Exercise, MutableList<Series>> = workout
    val getDeletedWorkouts get(): MutableList<Long> = deletedWorkouts
    val getDeletedSeries get(): MutableList<Long> = deletedSeries

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding = TrainingItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val item = exercises[position]
        val series = workout[item]!!
        val exerciseFormatter = ExerciseFormatter(item, context)
        holder.exerciseMore.setOnClickListener {
            selectedPosition = if (selectedPosition == position) null else position
            handleDetails(holder, selectedPosition == position)
        }
        holder.exerciseName.text = exerciseFormatter.name
        handleDetails(holder, selectedPosition == position)
        with(holder.seriesRecyclerView) {
            holder.trainSeriesAdapter = TrainSeriesRecyclerViewAdapter(
                context, series, changeHappened
            ) {
                deletedSeries.add(it)
            }
            layoutManager = LinearLayoutManager(context)
            adapter = holder.trainSeriesAdapter
        }
    }

    private fun handleDetails(
        holder: TrainingRecyclerViewAdapter.TrainingViewHolder,
        show: Boolean
    ) {
        if (show) {
            holder.detailsLayout.visibility = View.VISIBLE
            Glide.with(context)
                .load(R.drawable.ic_keyboard_arrow_up_black_24dp)
                .into(holder.showDetails)

        } else {
            holder.detailsLayout.visibility = View.GONE
            Glide.with(context)
                .load(R.drawable.ic_keyboard_arrow_down_black_24dp)
                .into(holder.showDetails)
        }
    }

    private fun deleteWorkout(exercise: Exercise) {
        val workoutId = workoutIds[exercise] ?: return
        deletedWorkouts.add(workoutId)
        val seriesToDelete = mutableListOf<Long>()
        workout[exercise]!!.forEach {
            if (it.seriesId != null) {
                seriesToDelete.add(it.seriesId!!)
            }
        }
        deletedSeries.addAll(seriesToDelete)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }


    inner class TrainingViewHolder(binding: TrainingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val exerciseName: TextView = binding.exerciseName
        val showDetails: ImageView = binding.showDetails
        val detailsLayout: LinearLayout = binding.exerciseDetails
        val exerciseMore: LinearLayout = binding.exerciseMore
        val seriesRecyclerView: RecyclerView = binding.seriesDone
        lateinit var trainSeriesAdapter: TrainSeriesRecyclerViewAdapter
    }
}