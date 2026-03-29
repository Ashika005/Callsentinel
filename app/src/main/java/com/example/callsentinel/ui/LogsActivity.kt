package com.example.callsentinel.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.data.AppDatabase
import com.example.callsentinel.databinding.ActivityLogsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding
    private lateinit var adapter: LogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LogsAdapter()
        binding.rvLogs.layoutManager = LinearLayoutManager(this)
        binding.rvLogs.adapter = adapter

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.callLogDao().getAllCallLogs().collectLatest { logs ->
                if (logs.isEmpty()) {
                    binding.tvNoLogs.visibility = View.VISIBLE
                    binding.rvLogs.visibility = View.GONE
                } else {
                    binding.tvNoLogs.visibility = View.GONE
                    binding.rvLogs.visibility = View.VISIBLE
                    adapter.submitList(logs)
                }
            }
        }
    }
}
