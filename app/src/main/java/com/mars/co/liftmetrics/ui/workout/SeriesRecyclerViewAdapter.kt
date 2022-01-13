package com.mars.co.liftmetrics.ui.workout

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibm.icu.text.RuleBasedNumberFormat
import com.mars.co.liftmetrics.data.model.Series
import com.mars.co.liftmetrics.databinding.SeriesItemBinding
import java.util.*

class SeriesRecyclerViewAdapter(
    private val series: List<Series>
) : RecyclerView.Adapter<SeriesRecyclerViewAdapter.SeriesViewHolder>() {

    private val ordinalFormatter = RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.ORDINAL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return SeriesViewHolder(
            SeriesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val item = series[position]
        with(holder){
            setPosition.text = ordinalFormatter.format(position + 1)
            setRepeats.text = item.repeats.toString()
            setWeight.text = item.weight.toString()
        }
    }

    override fun getItemCount(): Int {
        return series.size
    }

    inner class SeriesViewHolder(binding: SeriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val setPosition = binding.setPosition
        val setRepeats = binding.setRepeat
        val setWeight = binding.setWeight
    }
}