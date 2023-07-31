package com.example.girokhanyang_0

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import com.example.girokhanyang_0.database.MyDBHelper
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ChartWFragment : Fragment() {

    lateinit var dateTextView: TextView // 주를 보여주는 TextView
    lateinit var prevBtn: ImageButton // 이전 달로 이동하는 버튼
    lateinit var nextBtn: ImageButton // 다음 달로 이동하는 버튼

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

    lateinit var mBtn: Button // 월별 차트 버튼

    lateinit var myHelper: MyDBHelper

    private val moodImageViews: List<Int> by lazy { // 기분 ImageView 리스트
        listOf(
            R.id.mood11, R.id.mood12, R.id.mood13, R.id.mood14, R.id.mood15,
            R.id.mood21, R.id.mood22, R.id.mood23, R.id.mood24, R.id.mood25,
            R.id.mood31, R.id.mood32, R.id.mood33, R.id.mood34, R.id.mood35,
            R.id.mood41, R.id.mood42, R.id.mood43, R.id.mood44, R.id.mood45,
            R.id.mood51, R.id.mood52, R.id.mood53, R.id.mood54, R.id.mood55,
            R.id.mood61, R.id.mood62, R.id.mood63, R.id.mood64, R.id.mood65,
            R.id.mood71, R.id.mood72, R.id.mood73, R.id.mood74, R.id.mood75
        )
    }

    // 월별 버튼 클릭 이벤트 처리를 위한 인터페이스
    interface OnChartMButtonClickListener {
        fun onChartMButtonClicked()
    }

    // 이벤트를 처리할 리스너 객체를 담을 변수
    private lateinit var listener: OnChartMButtonClickListener

    // 프래그먼트가 액티비티에 추가될 때 호출되는 메서드
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // OnChartWButtonClickListener 인터페이스 구현 여부를 확인하고 리스너 할당
        if (context is OnChartMButtonClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnChartMButtonClickListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(com.example.girokhanyang_0.R.layout.fragment_chart_w, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 변수 초기화
        dateTextView = view.findViewById(com.example.girokhanyang_0.R.id.dateTextView)
        prevBtn = view.findViewById(com.example.girokhanyang_0.R.id.prevBtn)
        nextBtn = view.findViewById(com.example.girokhanyang_0.R.id.nextBtn)

        mBtn = view.findViewById(com.example.girokhanyang_0.R.id.mBtn)

        // 이번주를 default로 보여주기
        val startDate = getThisWeekStartDate()
        val endDate = getThisWeekEndDate()
        dateTextView.text = formatDateRange(startDate, endDate)

        // dateTextView 클릭 이벤트: 날짜 기간 선택하기
        dateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        // prevBtn 클릭 이벤트: 이전 주로 이동
        prevBtn.setOnClickListener {
            val previousWeekStart = getPreviousWeekStartDate()
            val previousWeekEnd = getPreviousWeekEndDate()
            dateTextView.text = formatDateRange(previousWeekStart, previousWeekEnd)

            // calendar 객체를 이전 주의 시작날짜로 업데이트
            calendar.time = previousWeekStart

            // 차트 다시 그리기
            drawChart()
        }

        // nextBtn 클릭 이벤트: 다음 주로 이동
        nextBtn.setOnClickListener {
            val nextWeekStart = getNextWeekStartDate()
            val nextWeekEnd = getNextWeekEndDate()
            dateTextView.text = formatDateRange(nextWeekStart, nextWeekEnd)

            // calendar 객체를 다음 주의 시작날짜로 업데이트
            calendar.time = nextWeekStart

            // 차트 다시 그리기
            drawChart()
        }

        // 월별 버튼 누르면 월별 프래그먼트로 이동
        mBtn.setOnClickListener {
            listener.onChartMButtonClicked()
        }

        // 차트 그리기
        myHelper = MyDBHelper(requireContext())
        drawChart()
    }


    // 이번주 시작날짜 구하는 함수
    private fun getThisWeekStartDate(): Date {
        val today = Calendar.getInstance()
        val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK)

        // 오늘이 일요일인 경우 시작날짜를 이번주 월요일로 설정
        if (currentDayOfWeek == Calendar.SUNDAY) {
            today.add(Calendar.DAY_OF_MONTH, -6) // 일요일에서 6일 전으로 이동
        } else {
            // 월요일부터 토요일까지의 차이를 계산해 시작날짜 설정
            val difference = (Calendar.MONDAY - currentDayOfWeek + 7) % 7
            today.add(Calendar.DAY_OF_MONTH, -difference)
        }

        return today.time
    }

    // 이번주 종료날짜 구하는 함수
    private fun getThisWeekEndDate(): Date {
        val today = Calendar.getInstance()
        val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK)

        // 오늘이 일요일인 경우 종료날짜를 오늘로 설정
        if (currentDayOfWeek == Calendar.SUNDAY) {
            today.add(Calendar.DAY_OF_MONTH, 0)
        } else {
            // 오늘부터 일요일까지의 차이를 계산해 종료날짜 설정
            val difference = (Calendar.SUNDAY - currentDayOfWeek + 7) % 7
            today.add(Calendar.DAY_OF_MONTH, difference)
        }

        return today.time
    }


    // 이전 주 시작날짜 구하는 함수
    private fun getPreviousWeekStartDate(): Date {
        val selectedDate = calendar.clone() as Calendar
        selectedDate.add(Calendar.DATE, -7)

        // 이번 주 시작 날짜가 월요일이 아닌 경우, 이전 주의 시작 날짜를 계산하여 설정
        while (selectedDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            selectedDate.add(Calendar.DATE, -1)
        }

        return selectedDate.time
    }

    // 이전 주 종료날짜 구하는 함수
    private fun getPreviousWeekEndDate(): Date {
        val selectedDate = calendar.clone() as Calendar
        selectedDate.add(Calendar.DATE, -1)
        selectedDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return selectedDate.time
    }

    // 다음 주의 시작날짜 구하는 함수
    private fun getNextWeekStartDate(): Date {
        val selectedDate = calendar.clone() as Calendar
        selectedDate.add(Calendar.DATE, 7)
        selectedDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return selectedDate.time
    }


    // 다음 주의 종료날짜 구하는 함수
    private fun getNextWeekEndDate(): Date {
        val selectedDate = calendar.clone() as Calendar
        selectedDate.add(Calendar.DATE, 13)
        selectedDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return selectedDate.time
    }

    // 날짜 선택화면(DatePickerDialog)을 보여주는 함수
    private fun showDatePickerDialog() {
        // 현재 날짜 가져오기
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 날짜 선택을 월요일~일요일로 제한
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)

                while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    calendar.add(Calendar.DATE, -1)
                }

                val startDate = calendar.time

                calendar.add(Calendar.DATE, 6)

                val endDate = calendar.time

                dateTextView.text = formatDateRange(startDate, endDate)

                // 차트 업데이트
                drawChart()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }

    // 선택된 날짜 범위 포맷하는 함수
    private fun formatDateRange(startDate: Date, endDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val start = dateFormat.format(startDate)
        val end = dateFormat.format(endDate)
        return "$start-$end"
    }


    // writingTime에서 요일값을 가져오는 함수
    private fun getDayOfWeek(writingTime: String): Int {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(writingTime)

        val calendar = Calendar.getInstance()
        calendar.time = date

        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    // 차트 그리는 함수
    private fun drawChart() {
        // 처음에 모든 ImageView 숨기기
        moodImageViews.forEach { view?.findViewById<ImageView>(it)?.visibility = View.INVISIBLE }

        // 현재 로그인 되어 있는 아이디 가져오기(SharedPreferences)
        val sharedPrefs: SharedPreferences? = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("id", "")

        val sqlDB = myHelper.readableDatabase

        // 일기 DB에서 기분과 작성시간 데이터 가져오기
        sqlDB.use { db ->
            var cursor: Cursor
            cursor = db.rawQuery("SELECT mood, writingTime FROM diaryTBL WHERE id='$id'", null)

            if (cursor.moveToFirst()) {
                do {
                    val mood = cursor.getInt(0)
                    val writingTime = cursor.getString(1)
                    val formattedDate = formatDate(writingTime)

                    // 작성 시간이 dateTextView 기간에 포함되는지 확인
                    if (isDateInRange(formattedDate)) {
                        val dayOfWeek = getDayOfWeek(writingTime)
                        val moodImgId = getMoodImageViewId(mood, dayOfWeek)

                        val moodImg: ImageView? = view?.findViewById(moodImgId)
                        if (moodImg?.visibility != View.VISIBLE) {
                            moodImg?.visibility = View.VISIBLE
                        }
                    }
                } while (cursor.moveToNext())
            }

            cursor.close()
        }
    }

    private fun formatDate(writingTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = inputFormat.parse(writingTime)
        return outputFormat.format(date)
    }

    // dateTextView 기간에 포함되는지 체크하는 함수
    private fun isDateInRange(formattedDate: String): Boolean {
        val dateRange = dateTextView.text.toString().split("-")
        val startDate = dateRange[0].trim()
        val endDate = dateRange[1].trim()

        return formattedDate in startDate..endDate
    }

    // 기분과 요일 값을 기준으로 ImageView를 가져오는 함수
    private fun getMoodImageViewId(mood: Int, dayOfWeek: Int): Int {
        return when (dayOfWeek) {
            Calendar.MONDAY -> when (mood) {
                1 -> R.id.mood11
                2 -> R.id.mood12
                3 -> R.id.mood13
                4 -> R.id.mood14
                5 -> R.id.mood15
                else -> 0
            }
            Calendar.TUESDAY -> when (mood) {
                1 -> R.id.mood21
                2 -> R.id.mood22
                3 -> R.id.mood23
                4 -> R.id.mood24
                5 -> R.id.mood25
                else -> 0
            }
            Calendar.WEDNESDAY -> when (mood) {
                1 -> R.id.mood31
                2 -> R.id.mood32
                3 -> R.id.mood33
                4 -> R.id.mood34
                5 -> R.id.mood35
                else -> 0
            }
            Calendar.THURSDAY -> when (mood) {
                1 -> R.id.mood41
                2 -> R.id.mood42
                3 -> R.id.mood43
                4 -> R.id.mood44
                5 -> R.id.mood45
                else -> 0
            }
            Calendar.FRIDAY -> when (mood) {
                1 -> R.id.mood51
                2 -> R.id.mood52
                3 -> R.id.mood53
                4 -> R.id.mood54
                5 -> R.id.mood55
                else -> 0
            }
            Calendar.SATURDAY -> when (mood) {
                1 -> R.id.mood61
                2 -> R.id.mood62
                3 -> R.id.mood63
                4 -> R.id.mood64
                5 -> R.id.mood65
                else -> 0
            }
            Calendar.SUNDAY -> when (mood) {
                1 -> R.id.mood71
                2 -> R.id.mood72
                3 -> R.id.mood73
                4 -> R.id.mood74
                5 -> R.id.mood75
                else -> 0
            }
            else -> 0 // 오류 발생시 0 반환
        }
    }

}