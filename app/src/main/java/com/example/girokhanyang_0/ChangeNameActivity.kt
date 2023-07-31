package com.example.girokhanyang_0

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.girokhanyang_0.database.MyDBHelper

class ChangeNameActivity : AppCompatActivity() {

    lateinit var nameEdt: EditText // 변경할 이름을 입력하는 EditText
    lateinit var EdtBtn: Button // 변경 버튼

    lateinit var myHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)

        // 변수 초기화
        nameEdt = findViewById(R.id.nameEdt)
        EdtBtn = findViewById(R.id.edtBtn)

        myHelper = MyDBHelper(applicationContext)

        // 현재 로그인 되어 있는 아이디 가져오기(SharedPreferences)
        val sharedPrefs: SharedPreferences? = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("id", "")

        // edtBtn 클릭 이벤트: DB에 이름 업데이트
        EdtBtn.setOnClickListener {
            val sqlDB = myHelper.writableDatabase

            // nameEdt가 비어있지 않을 경우에만 업데이트
            if (nameEdt.text.isNotEmpty()) {
                val newNickname = nameEdt.text.toString()
                sqlDB.execSQL("UPDATE userTBL SET nickname='$newNickname' WHERE id='$id';")
                Toast.makeText(applicationContext, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(applicationContext, "변경할 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            sqlDB.close()
        }
    }
}