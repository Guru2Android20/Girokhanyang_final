package com.example.girokhanyang_0

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.girokhanyang_0.databinding.ActivityNaviBinding

private const val TAG_MAIN = "main_fragment"
private const val TAG_CHART = "chart_w_fragment"
private const val TAG_MYPAGE = "myPage_fragment"

class NaviActivity : AppCompatActivity(), ChartWFragment.OnChartMButtonClickListener, ChartMFragment.OnChartWButtonClickListener {

    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 결합 클래스 인스턴스 생성
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_MAIN)

        // 네비게이션 버튼에 프래그먼트 연결
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainFragment -> setFragment(TAG_MAIN)
                R.id.writingFragment -> launchUploadActivity()
                R.id.chartFragment -> setFragment(TAG_CHART)
                R.id.myPageFragment -> setFragment(TAG_MYPAGE)
            }
            true
        }
    }

    // OnChartMButtonClickListener 인터페이스의 함수 구현(월별 프래그먼트로 교체)
    override fun onChartMButtonClicked() {
        val chartMFragment = ChartMFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.mainFrameLayout, chartMFragment)
        fragmentTransaction.commit()
    }

    // OnChartMButtonClickListener 인터페이스의 함수 구현(주별 프래그먼트로 교체)
    override fun onChartWButtonClicked() {
        val chartWFragment = ChartWFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.mainFrameLayout, chartWFragment)
        fragmentTransaction.commit()
    }

    // 태그에 맞는 프래그먼트를 보여주는 함수
    private fun setFragment(tag: String) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentTransaction = manager.beginTransaction()

        val fragment = createFragmentByTag(tag)

        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, tag)
        fragmentTransaction.commit()
    }


    private fun createFragmentByTag(tag: String): Fragment {
        return when (tag) {
            TAG_MAIN -> MainFragment()
            TAG_CHART -> ChartWFragment()
            TAG_MYPAGE -> MyPageFragment()
            else -> throw IllegalArgumentException("Unknown tag: $tag")
        }
    }

    // UploadActivity 실행하는 함수
    private fun launchUploadActivity() {
        val intent = Intent(this, UploadActivity::class.java)
        startActivity(intent)
    }

}