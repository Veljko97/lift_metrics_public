package com.mars.co.liftmetrics.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.mars.co.liftmetrics.LiftApplication
import com.mars.co.liftmetrics.R
import com.mars.co.liftmetrics.data.model.Exercise
import com.mars.co.liftmetrics.data.model.Series
import com.mars.co.liftmetrics.databinding.FragmentStatisticsBinding
import com.mars.co.liftmetrics.utilty.ExerciseFormatter
import com.mars.co.liftmetrics.utilty.ItemSpacing
import java.util.*

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels {
        val application: LiftApplication = (context?.applicationContext) as LiftApplication
        StatisticsViewModelFactory(application.trainingRepo, application.exerciseRepo)
    }

    private lateinit var recordsAdapter: PersonalRecordsAdapter

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val usedExercises = mutableListOf<Exercise>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        viewModel.getUsedExercises()
        setupPersonalRecords()

        setupUsedExercises()
        setupPersonalRecordsObserver()
        setupExerciseHistoryObserver()
        return binding.root
    }

    private fun setupExercisesSpinner() {
        with(binding.selectableExercises) {
            val values = usedExercises.map {
                ExerciseFormatter(it, context)
            }
            val adapter =
                ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    values
                )
            this.adapter = adapter
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    viewModel.getExerciseHistory(values[position].exercise)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }


    private fun setupPersonalRecords() {
        with(binding.personalRecords) {
            layoutManager = GridLayoutManager(context, 2)
            this.addItemDecoration(ItemSpacing(2, 20, true))
            recordsAdapter = PersonalRecordsAdapter(context)
            adapter = recordsAdapter
        }
    }

    private fun setupPersonalRecordsObserver() {
        viewModel.personalRecords.observe(viewLifecycleOwner) {
            recordsAdapter.updateRecords(it)
        }
    }

    private fun setupUsedExercises() {
        viewModel.usedExercise.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                return@observe
            }
            usedExercises.addAll(it)
            setupExercisesSpinner()
            usedExercises.forEach { exercise ->
                viewModel.getPersonalRecords(exercise)
            }
        }
    }

    private fun setupExerciseHistoryObserver() {
        viewModel.exerciseHistory.observe(viewLifecycleOwner) { workouts ->
            binding.statisticsGraph.removeAllSeries()
            val max = mutableListOf<DataPoint>()
            val min = mutableListOf<DataPoint>()
            for (workout in workouts) {
                val seriesMax = workout.series.maxByOrNull {
                    it.weight
                }
                val seriesMin = workout.series.minByOrNull {
                    it.weight
                }
                if (seriesMax == null || seriesMin == null) {
                    continue
                }
                max.add(
                    DataPoint(workout.workout.recordDate, seriesMax.weight)
                )
                min.add(
                    DataPoint(workout.workout.recordDate, seriesMin.weight)
                )
            }
            val maxSeries = LineGraphSeries(max.toTypedArray())
            val minSeries = LineGraphSeries(min.toTypedArray())

            maxSeries.title = getString(R.string.series_max)
            maxSeries.color = Color.BLUE

            minSeries.title = getString(R.string.series_mIn)
            minSeries.color = Color.RED

            val maxDate = workouts.last().workout.recordDate
            val minDate = workouts[0].workout.recordDate

            // Possible update to add scalable
//            val calendar = Calendar.getInstance()
//            calendar.time = maxDate
//            calendar.add(Calendar.WEEK_OF_MONTH, -1)
//            val minDate = calendar.time
//            viewport.isScalable = true

            with(binding.statisticsGraph) {

                gridLabelRenderer.labelFormatter =
                    DateAsXAxisLabelFormatter(context)
                gridLabelRenderer.numHorizontalLabels = 3
                gridLabelRenderer.setHumanRounding(false, true)
                gridLabelRenderer.gridColor = Color.WHITE
                gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.BOTH

                legendRenderer.setFixedPosition(0, 0)
                legendRenderer.isVisible = true

                viewport.setMinX(minDate.time.toDouble())
                viewport.setMaxX(maxDate.time.toDouble())
                viewport.isXAxisBoundsManual = true


                this.addSeries(maxSeries)
                this.addSeries(minSeries)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}