package com.mars.co.liftmetrics.ui.exercises

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.databinding.ExerciseDetailDialogBinding
import com.mars.co.liftmetrics.databinding.FragmentExercisesItemBinding
import com.mars.co.liftmetrics.utilty.DialogUtil
import com.mars.co.liftmetrics.utilty.ExerciseFormatter


class ExercisesRecyclerViewAdapter(
    private val context: Context,
    exercises: List<Exercise>
) : RecyclerView.Adapter<ExercisesRecyclerViewAdapter.ViewHolder>() {

    private var values: List<ExerciseDetails> = exercises.map {
        ExerciseDetails(exercise = it)
    }

    fun updateData(exercises: List<Exercise>) {
        values = exercises.map {
            ExerciseDetails(exercise = it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentExercisesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val exerciseFormatter = ExerciseFormatter(item.exercise, context)
        holder.exerciseMore.setOnClickListener {
            item.show = !item.show
            handleDetails(holder, item.show)
            exerciseFormatter.insertGraphic(holder.imageExample)
        }
        holder.exerciseName.text = exerciseFormatter.name
        holder.exerciseDescription.setOnClickListener {
            val dialogUtil = DialogUtil(context)
            dialogUtil.showExerciseDetailsDialog(exerciseFormatter)
        }
        handleDetails(holder, item.show)
        exerciseFormatter.insertGraphic(holder.imageExample)
    }

    private fun handleDetails(
        holder: ViewHolder,
        show: Boolean,
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
            Glide.with(context)
                .clear(holder.imageExample)
        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ExerciseDetails(
        val exercise: Exercise,
        var show: Boolean = false
    )

    inner class ViewHolder(binding: FragmentExercisesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val exerciseName = binding.exerciseName
        val exerciseDescription = binding.exerciseDescription
        val showDetails = binding.showDetails
        val detailsLayout = binding.exerciseDetails
        val imageExample = binding.exerciseExample
        val exerciseMore = binding.exerciseMore

    }

}