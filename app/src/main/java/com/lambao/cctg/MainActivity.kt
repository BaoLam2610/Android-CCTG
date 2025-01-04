package com.lambao.cctg

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.lambao.cctg.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.viewModel = viewModel
        initSelectDateListener()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.interestPeriods.collect()
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect()
        }

        lifecycleScope.launch {
            viewModel.rateAfterSelectedDate.collect()
        }

        lifecycleScope.launch {
            viewModel.differenceDate.collect()
        }

        lifecycleScope.launch {
            viewModel.displayRate.collect()
        }
    }

    private fun initSelectDateListener() {
        binding.btnSelectDate.setOnClickListener {
            val initDate = viewModel.selectedDate.value.split("/")
            DatePickerDialog(
                this,
                this,
                initDate[2].toInt(),
                initDate[1].toInt() - 1,
                initDate[0].toInt()
            ).also {
                it.show()
            }
        }
    }

    override fun onDateSet(
        datePicker: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        viewModel.setSelectedDate(selectedDate)
    }
}