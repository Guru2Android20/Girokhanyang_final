package com.example.girokhanyang_0

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.girokhanyang_0.database.MyDBHelper

class ChangePwActivity : AppCompatActivity() {

    lateinit var pwEdt: EditText // 현재 비밀번호 입력하는 EditText
    lateinit var newPwEdt: EditText // 변경할 비밀번호를 입력하는 EditText
    lateinit var edtBtn: Button // 변경 버튼

    lateinit var myHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pw)

        // 변수 초기화
        pwEdt = findViewById(R.id.pwEdt)
        newPwEdt = findViewById(R.id.newPwEdt)
        edtBtn = findViewById(R.id.edtBtn)

        myHelper = MyDBHelper(applicationContext)

        // 현재 로그인 되어 있는 아이디 가져오기(SharedPreferences)
        val sharedPrefs: SharedPreferences? = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("id", "")

        // edtBtn 클릭 이벤트: DB에 비밀번호 업데이트
        edtBtn.setOnClickListener {
            val sqlDB = myHelper.writableDatabase

            // pwEdt와 newPwEdt가 둘 다 비어있지 않을 경우에만 업데이트
            if (pwEdt.text.isNotEmpty()) {

                if (newPwEdt.text.isNotEmpty()) {

                    // pwEdt와 현재 비밀번호가 동일한지 확인
                    val cursor: Cursor = sqlDB.rawQuery("SELECT password FROM userTBL WHERE id='$id';", null)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex("password")
                        val savedPassword = cursor.getString(columnIndex)

                        if (savedPassword == pwEdt.text.toString()) {
                            // newPwEdt에 입력한 비밀번호로 업데이트
                            val newPw = newPwEdt.text.toString()
                            sqlDB.execSQL("UPDATE userTBL SET password='$newPw' WHERE id='$id';")
                            Toast.makeText(applicationContext, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    cursor.close()

                } else {
                    Toast.makeText(applicationContext, "변경할 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(applicationContext, "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            sqlDB.close()
        }
    }
}