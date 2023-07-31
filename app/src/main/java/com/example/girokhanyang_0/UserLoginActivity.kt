package com.example.girokhanyang_0

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserLoginActivity : AppCompatActivity() {
    private lateinit var mFirebaseAuth : FirebaseAuth       // 파이어 베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference   // 실시간 데이터 베이스
    private lateinit var mEtLoginEmail : EditText
    private lateinit var mEtLoginPassword : EditText
    private lateinit var mBtnLogin : Button
    private lateinit var mImgBack : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        mEtLoginEmail = findViewById(R.id.et_loginEmail)
        mEtLoginPassword = findViewById(R.id.et_loginPassword)
        mBtnLogin = findViewById(R.id.btn_login)
        mImgBack = findViewById(R.id.imgBack)

        mImgBack.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mBtnLogin.setOnClickListener {

            var strEmail : String = mEtLoginEmail.text.toString()
            var strPassword : String = mEtLoginPassword.text.toString()

            // Firebase Auth 진행
            mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공

                    // 로그인한 아이디 SharedPreferences로 저장하기
                    val sharedPrefs: SharedPreferences? = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs?.edit()
                    editor?.putString("id", strEmail)
                    editor?.apply()

                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "아이디 또는 비밀번호를 잘못 입력했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}