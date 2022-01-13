package com.mars.co.liftmetrics.ui.workout

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.CompleteWorkout
import com.mars.co.liftmetrics.databinding.FragmentWorkoutItemBinding
import com.mars.co.liftmetrics.utilty.ExerciseFormatter
import com.mars.co.liftmetrics.utilty.ItemSpacing

class WorkoutRecyclerViewAdapter(
    private val context: Context,
) : RecyclerView.Adapter<WorkoutRecyclerViewAdapter.WorkoutViewHolder>() {

    private var shownWorkouts: List<ShownWorkout> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateWorkouts(
        workouts: List<CompleteWorkout>
    ) {
        shownWorkouts = workouts.map {
            ShownWorkout(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = FragmentWorkoutItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val item = shownWorkouts[position]
        val exerciseFormatter = ExerciseFormatter(item.workout.exercise, context)
        holder.exerciseMore.setOnClickListener {
            item.shown = !item.shown
            handleDetails(holder, item.shown)
        }
        holder.exerciseName.text = exerciseFormatter.name
        handleDetails(holder, item.shown)
        with(holder.seriesRecyclerView) {
            layoutManager = GridLayoutManager(context, 2)
            if (itemDecorationCount == 0) {
                this.addItemDecoration(ItemSpacing(2, 20, true))
            }
            holder.seriesAdapter = SeriesRecyclerViewAdapter(item.workout.series)
            adapter = holder.seriesAdapter
        }
    }

    private fun handleDetails(
        holder: WorkoutRecyclerViewAdapter.WorkoutViewHolder,
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

    override fun getItemCount(): Int {
        return shownWorkouts.size
    }


    inner class WorkoutViewHolder(binding: FragmentWorkoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val exerciseName: TextView = binding.exerciseName
        val showDetails: ImageView = binding.showDetails
        val detailsLayout: LinearLayout = binding.exerciseDetails
        val exerciseMore: LinearLayout = binding.exerciseMore
        val seriesRecyclerView: RecyclerView = binding.seriesDone
        lateinit var seriesAdapter: SeriesRecyclerViewAdapter
    }

    inner class ShownWorkout(
        val workout: CompleteWorkout,
        var shown: Boolean = false
    )
}