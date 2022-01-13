package com.mars.co.liftmetrics.ui.workout.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ibm.icu.text.RuleBasedNumberFormat
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Series
import com.mars.co.liftmetrics.databinding.SeriesInputItemBinding
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty

class TrainSeriesRecyclerViewAdapter(
    private val context: Context,
    private val series: MutableList<Series>,
    private val changeHappened: () -> Unit,
    private val deleteSeries: (Long) -> Unit
) : RecyclerView.Adapter<TrainSeriesRecyclerViewAdapter.SeriesViewHolder>() {

    private val ordinalFormatter = RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.ORDINAL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return SeriesViewHolder(
            SeriesInputItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        var item: Series? = null
        val isConfirmed = position < series.size
        if (isConfirmed) {
            item = series[position]
        }
        with(holder) {
            handleEditing(!isConfirmed)
            val number = position % (series.size + 1)
            setPosition.text = ordinalFormatter.format(number + 1)
            inputReps.setText(item?.repeats?.toString() ?: "")
            inputWeight.setText(item?.weight?.toString() ?: "")
            confirm.visibility = if (!isConfirmed) View.VISIBLE else View.GONE
            confirm.setOnClickListener {
                val editPosition = if (isEditing) position else null
                handleConfirm(this, editPosition)
            }
            inputWeight.setOnEditorActionListener { _, action, _ ->
                var handled = false
                if (action == EditorInfo.IME_ACTION_DONE) {
                    val editPosition = if (isEditing) position else null
                    handleConfirm(this, editPosition)
                    handled = true
                }
                handled
            }

            edit.setOnClickListener {
                isEditing = true
            }

            remove.setOnClickListener {
                removeSeries(position)
            }
        }
    }

    private fun handleConfirm(
        holder: SeriesViewHolder,
        position: Int? = null
    ) {
        val repeats = holder.inputReps.text.toString().toLongOrNull()
        val weight = holder.inputWeight.text.toString().toDoubleOrNull()
        if (repeats == null || weight == null) {
            Toast.makeText(context, R.string.set_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        changeHappened()
        if (position == null) {
            series.add(Series(repeats, weight))
            notifyItemInserted(series.size - 1)
            //Input row needs to be updated
            notifyItemChanged(series.size)
            return
        }
        series[position] = series[position].copyWith(repeats, weight, null)
        notifyItemChanged(position)
    }

    private fun removeSeries(position: Int) {
        changeHappened()
        val set = series.removeAt(position)
        if (set.seriesId != null) {
            deleteSeries(set.seriesId!!)
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, series.size + 1 - position)
    }

    override fun getItemCount(): Int {
        return series.size + 1
    }

    inner class SeriesViewHolder(binding: SeriesInputItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val setPosition = binding.setPosition
        val inputReps = binding.setRepeatInput
        val inputWeight = binding.setWeightInput
        val confirm = binding.setConfirm
        val edit = binding.setEdit
        val remove = binding.setRemove
        var isEditing: Boolean by Delegates.observable(false) { _, _, newValue ->
            handleEditing(newValue)
        }

        fun handleEditing(isEditing: Boolean) {
            if (isEditing) {
                confirm.visibility = View.VISIBLE
                edit.visibility = View.GONE
                remove.visibility = View.GONE

                inputReps.isEnabled = true
                inputWeight.isEnabled = true
                inputReps.requestFocus()
                return
            }
            confirm.visibility = View.GONE
            edit.visibility = View.VISIBLE
            remove.visibility = View.VISIBLE

            inputReps.isEnabled = false
            inputWeight.isEnabled = false
        }
    }
}