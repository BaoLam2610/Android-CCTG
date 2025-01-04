package com.lambao.cctg

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lambao.cctg.model.fakeInterestPeriods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel : ViewModel() {

    val amountInput = MutableStateFlow("")

    private val _interestPeriods = MutableStateFlow(fakeInterestPeriods)
    val interestPeriods get() = _interestPeriods.asStateFlow()

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate get() = _selectedDate.asStateFlow()

    private val _differenceDate = _selectedDate.map {
        getDateDifference(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val differenceDate get() = _differenceDate

    private val _rateAfterSelectedDate = combine(
        _interestPeriods,
        _differenceDate
    ) { interestPeriods, differenceDate ->
        differenceDate?.let { diff ->
            interestPeriods.find { period ->
                when {
                    period.startDay != null && period.endDay != null -> {
                        diff in period.startDay..period.endDay
                    }

                    period.startDay != null && period.endDay == null -> {
                        diff >= period.startDay
                    }

                    else -> false // Should not happen with your data structure
                }
            }?.rate
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val rateAfterSelectedDate get() = _rateAfterSelectedDate

    private val _displayRate = _rateAfterSelectedDate.map {
        it?.toString() ?: ""
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val displayRate = _displayRate

    fun setSelectedDate(date: String) {
        viewModelScope.launch {
            _selectedDate.emit(date)
        }
    }

    fun getDateDifferenceRequireApi26(toDate: String): Int? {
        try {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

//        val currentDate = LocalDate.now()
//        val currentDateStr = currentDate.format(dateFormatter)

//        val from = LocalDate.parse(currentDateStr, dateFormatter)
            val to = LocalDate.parse(toDate, dateFormatter)

            val period = Period.between(LocalDate.now(), to)
            val days = period.days
            return days

        } catch (_: Exception) {
            return null
        }
    }

    // Hàm tính số ngày giữa ngày hiện tại và ngày khác từ chuỗi
    fun getDateDifference(dateString: String): Long? {
        try {
            // Định dạng ngày (phải khớp với định dạng chuỗi đầu vào)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            dateFormat.isLenient = false // Kiểm tra nghiêm ngặt định dạng ngày

            // Chuyển chuỗi thành đối tượng Calendar
            val futureDate = Calendar.getInstance().apply {
                time = dateFormat.parse(dateString)
            }

            // Lấy ngày hiện tại
            val currentDate = Calendar.getInstance()
            currentDate.set(Calendar.HOUR_OF_DAY, 0)
            currentDate.set(Calendar.MINUTE, 0)
            currentDate.set(Calendar.SECOND, 0)
            currentDate.set(Calendar.MILLISECOND, 0)

            // Tính khoảng cách giữa hai ngày
            val diffInMillis = futureDate.timeInMillis - currentDate.timeInMillis
            return TimeUnit.MILLISECONDS.toDays(diffInMillis)
        } catch (_: Exception) {
            return null
        }

    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = Calendar.getInstance().time
        val formattedDate = dateFormat.format(currentDate)
        return formattedDate
    }

}