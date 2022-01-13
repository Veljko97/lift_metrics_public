package com.mars.co.liftmetrics.ui.exercises.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.view.ViewGroup
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Muscle
import com.mars.co.liftmetrics.data.model.MusclesConverter
import com.mars.co.liftmetrics.databinding.SpinnerCheckItemBinding

data class CheckableMuscle(
    var isChecked: Boolean,
    val muscle: Muscle
)

class MuscleAdapter(
    parentContext: Context,
    resource: Int,
    private val items: List<CheckableMuscle>,
) : ArrayAdapter<CheckableMuscle>(parentContext, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }


    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
        return getCustomDropDown(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = getBinding(convertView, parent)
        binding.checkbox.visibility = View.GONE
        binding.title.text = context.getString(R.string.select_muscle_grups)
        return binding.root
    }

    private fun getCustomDropDown(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val binding = getBinding(convertView, parent)

        val item = items[position]

        binding.title.text = context.getString(MusclesConverter.toStringRes(item.muscle))
        binding.checkbox.isChecked = item.isChecked

        binding.root.setOnClickListener {
            item.isChecked = !item.isChecked
            binding.checkbox.isChecked = item.isChecked
        }

        return binding.root
    }

    private fun getBinding(
        convertView: View?,
        parent: ViewGroup?
    ): SpinnerCheckItemBinding {
        if (convertView == null) {
            return SpinnerCheckItemBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        }
        return SpinnerCheckItemBinding.bind(convertView)
    }

    fun getSelectedMuscles(): Set<Muscle> {
        val selectedMuscles = mutableSetOf<Muscle>()
        items.forEach {
            if (it.isChecked) {
                selectedMuscles.add(it.muscle)
            }
        }
        return selectedMuscles
    }
}