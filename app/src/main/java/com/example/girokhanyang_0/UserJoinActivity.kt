package com.example.girokhanyang_0

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.girokhanyang_0.database.MyDBHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class UserJoinActivity : Activity() {
    private lateinit var mFirebaseAuth : FirebaseAuth      // 파이어 베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference   // 실시간 데이터 베이스
    private lateinit var mEtEmail : EditText
    private lateinit var mEtPassword : EditText
    private lateinit var mEtPassword2 : EditText
    private lateinit var mEtNickname : EditText

    private lateinit var mBtnJoin : Button
    private lateinit var mImgBack : ImageView

    lateinit var myHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_join)

        // mFirebaseAuth = Firebase.auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        mEtEmail = findViewById(R.id.et_email)
        mEtPassword = findViewById(R.id.et_pass)
        mEtPassword2 = findViewById(R.id.et_pass2)
        mEtNickname = findViewById(R.id.et_nickname)

        mBtnJoin = findViewById(R.id.btn_join)
        mImgBack = findViewById(R.id.img_back)

        myHelper = MyDBHelper(this)

        mImgBack.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mBtnJoin.setOnClickListener {
            // 회원가입 처리 시작
            var strEmail : String = mEtEmail.text.toString()
            var strPassword : String = mEtPassword.text.toString()
            var strPassword2 : String = mEtPassword2.text.toString()
            var strNickname : String = mEtNickname.text.toString()

            if (strPassword != strPassword2) {
                // 비밀번호가 일치하지 않는 경우
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 내부DB에 저장하기
                        val sqlDB = myHelper.writableDatabase
                        sqlDB.execSQL("INSERT INTO userTBL VALUES ('${strEmail}', '${strNickname}', '${strPassword}');")
                        sqlDB.close()

                        // 로그인한 아이디 SharedPreferences로 저장하기
                        saveEmail(strEmail)

                        Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        finish()

                        val intent = Intent(this, UploadActivity::class.java)
                        startActivity(intent)
                        finish()

                        val firebaseUser: FirebaseUser? = mFirebaseAuth.currentUser
                        val account = UserAccount()
                        firebaseUser?.let {
                            account.setEmailId(it.email)
                            account.setPassword(strPassword)
                            account.setNickname(strNickname)
                            mDatabaseRef.child("UserAccount").child(it.uid).setValue(account)
                        }
                    } else {
                        Log.d("firebase", task.exception?.message.toString())
                        Toast.makeText(this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 로그인한 아이디 SharedPreferences로 저장하는 함수
    private fun saveEmail(email: String) {
        val sharedPrefs: SharedPreferences? = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs?.edit()
        editor?.putString("id", email)
        editor?.apply()
    }

}