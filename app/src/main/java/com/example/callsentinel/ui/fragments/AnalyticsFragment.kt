package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.callsentinel.R
import com.example.callsentinel.databinding.FragmentAnalyticsBinding
import com.example.callsentinel.ui.viewmodels.AnalyticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        observeViewModel()
    }

    private fun setupCharts() {
        // Safe check to ensure binding exists
        _binding?.barChart?.apply {
            description.isEnabled = false
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weeklyData.collect { data ->
                        updateBarChart(data)
                    }
                }

                launch {
                    viewModel.stats.collect { stats ->
                        _binding?.apply {
                            tvTotalScanned.text = stats.totalScanned.toString()
                            tvTotalBlocked.text = stats.totalBlocked.toString()
                            tvHighRiskCount.text = stats.highRiskCount.toString()
                            tvSpoofAttempts.text = stats.spoofAttempts.toString()
                        }
                    }
                }

                launch {
                    viewModel.riskBreakdown.collect { breakdown ->
                        updateRiskBreakdown(breakdown)
                    }
                }
            }
        }
    }

    private fun updateBarChart(data: Map<String, Int>) {
        // CRASH FIX: Return if view is destroyed or context is null
        val currentContext = context ?: return
        if (_binding == null) return

        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Suspicious Calls").apply {
            // CRASH FIX: Use ContextCompat instead of resources.getColor
            color = ContextCompat.getColor(currentContext, R.color.accent_cyan)
            valueTextColor = ContextCompat.getColor(currentContext, R.color.text_primary)
        }

        binding.barChart.apply {
            this.data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(data.keys.toList())
            invalidate()
        }
    }

    private fun updateRiskBreakdown(breakdown: AnalyticsViewModel.RiskBreakdown) {
        // CRASH FIX: Safe call on binding
        _binding?.apply {
            tvLowRiskCount.text = breakdown.low.toString()
            tvMediumRiskCount.text = breakdown.medium.toString()
            tvHighRiskCountBreakdown.text = breakdown.high.toString()
            tvCriticalRiskCount.text = breakdown.critical.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}