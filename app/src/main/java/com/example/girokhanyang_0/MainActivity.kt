package com.example.girokhanyang_0

import SqliteHelper
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager

class MainActivity : AppCompatActivity() {

    private val helper = SqliteHelper(this, "diaryEntry", 3)
    //private val adapter = RecyclerAdapter()
    private val UPLOAD_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun openUploadActivity() {
            val intent = Intent(this, UploadActivity::class.java)
            startActivityForResult(intent, UPLOAD_REQUEST_CODE)
        }


        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == UPLOAD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                // UploadActivity로부터 전달된 결과 데이터를 처리합니다.
                // 예를 들어, 메시지를 전달했다면 다음과 같이 메시지를 가져올 수 있습니다:
                val message = data?.getStringExtra("MESSAGE_KEY")
                // 그리고 필요한 동작을 수행하거나 메시지를 표시할 수 있습니다.
            }
        }
        /*fun initSettings() {
            adapter.helper = helper
            recyclerDiaryEntry.adapter = adapter

            recyclerDiaryEntry.layoutManager = GridLayoutManager(this, 2)
            recyclerDiaryEntry.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

            adapter.listData = helper
            adapter.listData.addAll(helper.selectDiaryEntry())
            adapter.notifyDataSetChanged()
        }*/
    }
}