package com.mars.co.liftmetrics.ui.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mars.co.liftmetrics.data.model.PersonalRecord
import com.mars.co.liftmetrics.databinding.PersonalRecordItemBinding
import com.mars.co.liftmetrics.utilty.ExerciseFormatter

class PersonalRecordsAdapter(
    private val context: Context
) : RecyclerView.Adapter<PersonalRecordsAdapter.PersonalRecordViewHolder>() {

    private val records = mutableListOf<PersonalRecord>()

    fun updateRecords(record: PersonalRecord) {
        records.add(record)
        notifyItemInserted(records.lastIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalRecordViewHolder {
        return PersonalRecordViewHolder(
            PersonalRecordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PersonalRecordViewHolder, position: Int) {
        val record = records[position]
        val exerciseFormatter = ExerciseFormatter(record.exercise, context)
        with(holder) {
            exerciseName.text = exerciseFormatter.name
            setRepeats.text = record.series.repeats.toString()
            setWeight.text = record.series.weight.toString()
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }

    inner class PersonalRecordViewHolder(private val binding: PersonalRecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val exerciseName = binding.exerciseName
        val setRepeats = binding.setRepeat
        val setWeight = binding.setWeight
    }
}