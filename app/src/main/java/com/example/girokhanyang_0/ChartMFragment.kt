package com.example.girokhanyang_0

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.girokhanyang_0.database.MyDBHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class ChartMFragment : Fragment() {

    lateinit var monthTextView: TextView // 달을 보여주는 TextView
    lateinit var prevBtn: ImageButton // 이전 달로 이동하는 버튼
    lateinit var nextBtn: ImageButton // 다음 달로 이동하는 버튼

    lateinit var monthPicker: NumberPicker // 달을 선택하는 NumberPicker
    private var mSelectedMonth: Int = 0 // 선택된 달을 저장하는 변수
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

    lateinit var wBtn: Button // 주별 차트 버튼

    lateinit var myHelper: MyDBHelper

    lateinit var mDiaryCountTextView: TextView // 설정된 달의 일기 총 개수를 보여주는 TextView
    lateinit var topMoodImageView: ImageView // 설정된 달에 가장 많이 느낀 기분을 보여주는 ImageView

    // 주별 버튼 클릭 이벤트 처리를 위한 인터페이스
    interface OnChartWButtonClickListener {
        fun onChartWButtonClicked()
    }

    // 이벤트를 처리할 리스너 객체를 담을 변수
    private lateinit var listener: OnChartWButtonClickListener

    // 프래그먼트가 액티비티에 추가될 때 호출되는 메서드
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // OnChartWButtonClickListener 인터페이스 구현 여부를 확인하고 리스너 할당
        if (context is OnChartWButtonClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnChartWButtonClickListener")
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
        return inflater.inflate(com.example.girokhanyang_0.R.layout.fragment_chart_m, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 변수 초기화
        monthTextView = view.findViewById(com.example.girokhanyang_0.R.id.monthTextView)
        prevBtn = view.findViewById(com.example.girokhanyang_0.R.id.prevBtn)
        nextBtn = view.findViewById(com.example.girokhanyang_0.R.id.nextBtn)

        wBtn = view.findViewById(R.id.wBtn)

        myHelper = MyDBHelper(requireContext())

        mDiaryCountTextView = view.findViewById(R.id.mDiaryCountTextView)
        topMoodImageView = view.findViewById(R.id.topMoodImageView)

        // NumberPicker 초기화
        monthPicker = NumberPicker(requireContext())
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = calendar.get(Calendar.MONTH) + 1 // 현재 달 설정

        // NumberPicker 레이아웃 설정
        val numberPickerLayout = LinearLayout(requireContext())
        numberPickerLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        numberPickerLayout.gravity = Gravity.CENTER
        numberPickerLayout.addView(monthPicker)

        // NumberPicker를 보여주는 AlertDialog 생성
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("월 선택")
            .setView(numberPickerLayout)
            .setPositiveButton("확인") { _, _ ->
                val selectedMonth = monthPicker.value
                calendar.set(Calendar.MONTH, selectedMonth - 1)
                updateMonthTextView()
            }
            .setNegativeButton("취소", null)
            .create()

        // monthTextView 클릭 이벤트: AlertDialog 보여주기
        monthTextView.setOnClickListener {
            alertDialog.show()
        }

        // prevBtn 클릭 이벤트: 이전 달로 이동
        prevBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthTextView()
            updateChartData(calendar.get(Calendar.MONTH) + 1)
        }

        // nextBtn 클릭 이벤트: 다음 달로 이동
        nextBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthTextView()
            updateChartData(calendar.get(Calendar.MONTH) + 1)
        }

        // 초기 monthTextView 업데이트
        updateMonthTextView()

        // 현재 달로 초기화
        mSelectedMonth = calendar.get(Calendar.MONTH) + 1

        // 초기 차트 업데이트
        updateChartData(mSelectedMonth)

        // NumberPicker 값이 변경될 때마다 차트 업데이트
        monthPicker.setOnValueChangedListener { _, _, newVal ->
            mSelectedMonth = newVal
            updateMonthTextView()
            updateChartData(newVal) // 선택한 달을 인수로 전달
        }

        // 달별 일기 개수, 가장 많이 느낀 기분 값 가져오기
        val (mDiaryCount, topMood) = showTotalChart(mSelectedMonth)

        // 달별 일기 개수 보여주기
        mDiaryCountTextView.text = mDiaryCount.toString()

        // 달별 가장 많이 느낀 기분 보여주기
        when (topMood) {
            1 -> topMoodImageView.setImageResource(R.drawable.ic_mood1)
            2 -> topMoodImageView.setImageResource(R.drawable.ic_mood2)
            3 -> topMoodImageView.setImageResource(R.drawable.ic_mood3)
            4 -> topMoodImageView.setImageResource(R.drawable.ic_mood4)
            5 -> topMoodImageView.setImageResource(R.drawable.ic_mood5)
            else -> {
                // 기본 이미지(일기가 없는 경우)
                topMoodImageView.setImageResource(R.drawable.ic_mood1_unselected)
            }
        }

        // 주별 버튼 클릭 이벤트: 주별 프래그먼트로 이동
        wBtn.setOnClickListener {
            listener.onChartWButtonClicked()
        }
    }

    // monthTextView를 업데이트 하는 함수
    private fun updateMonthTextView() {
        val simpleDateFormat = SimpleDateFormat("yyyy.MM", Locale.getDefault())
        monthTextView.text = simpleDateFormat.format(calendar.time)
    }

    // 종합 통계를 보여주는 함수
    private fun showTotalChart(selectedMonth: Int): Pair<Int, Int?> {
        val db = myHelper.readableDatabase
        var mDiaryCount = 0
        var topMood: Int? = null

        // selectedMonth에 따른 시작일과 종료일 계산하기
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, selectedMonth - 1) // 선택한 달로 설정
            set(Calendar.DAY_OF_MONTH, 1) // 해당 달의 1일로 설정
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startDate = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault()).format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // 해당 달의 마지막 날로 설정
        val endDate = SimpleDateFormat("yyyy-MM-dd'T'23:59:59", Locale.getDefault()).format(calendar.time)

        // diaryTBL에서 해당 달의 총 일기 수 가져오기
        val query = "SELECT COUNT(*) FROM diaryTBL WHERE writingTime >= '$startDate' AND writingTime <= '$endDate'"
        val diaryCountCursor: Cursor = db.rawQuery(query, null)
        if (diaryCountCursor.moveToFirst()) {
            mDiaryCount = diaryCountCursor.getInt(0)
        }
        diaryCountCursor.close()

        // diaryTBL에서 해당 달의 가장 많이 나온 기분 값 가져오기(값이 동일하다면 내림차순)
        val moodQuery =
            "SELECT mood FROM diaryTBL WHERE writingTime >= '$startDate' AND writingTime <= '$endDate' GROUP BY mood ORDER BY COUNT(*) ASC LIMIT 1"
        val mostFrequentMoodCursor: Cursor = db.rawQuery(moodQuery, null)
        if (mostFrequentMoodCursor.moveToFirst()) {
            topMood = mostFrequentMoodCursor.getInt(0)
        }
        mostFrequentMoodCursor.close()

        return Pair(mDiaryCount, topMood)
    }

    // 종합 통계 데이터를 업데이트하는 함수
    private fun updateChartData(selectedMonth: Int) {
        val (mDiaryCount, topMood) = showTotalChart(selectedMonth)

        mDiaryCountTextView.text = mDiaryCount.toString()

        when (topMood) {
            1 -> topMoodImageView.setImageResource(R.drawable.ic_mood2)
            2 -> topMoodImageView.setImageResource(R.drawable.ic_mood2)
            3 -> topMoodImageView.setImageResource(R.drawable.ic_mood3)
            4 -> topMoodImageView.setImageResource(R.drawable.ic_mood4)
            5 -> topMoodImageView.setImageResource(R.drawable.ic_mood5)
            else -> {
                topMoodImageView.setImageResource(R.drawable.ic_mood1_unselected)
            }
        }
    }
}