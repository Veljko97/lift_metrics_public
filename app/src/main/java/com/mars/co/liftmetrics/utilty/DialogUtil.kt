package com.mars.co.liftmetrics.utilty

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.databinding.ExerciseDetailDialogBinding


// User for dialog that can be used for more the instance
class DialogUtil(private val context: Context) {

    fun showExerciseDetailsDialog(
        exerciseFormatter: ExerciseFormatter
    ) {
        val binding = ExerciseDetailDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        binding.exerciseDescription.text = exerciseFormatter.description
        if(exerciseFormatter.videoLink == null) {
            binding.exerciseVideo.visibility = View.GONE
        }
        binding.exerciseVideo.text = context.getString(R.string.video)
        binding.exerciseVideo.setTextColor(Color.BLUE)
        binding.exerciseVideo.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.exerciseVideo.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(exerciseFormatter.videoLink))
            context.startActivity(browserIntent)
        }
        builder.show()
    }
}