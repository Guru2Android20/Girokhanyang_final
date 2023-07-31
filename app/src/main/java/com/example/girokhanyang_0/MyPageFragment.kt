package com.example.girokhanyang_0

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.girokhanyang_0.database.MyDBHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class MyPageFragment : Fragment() {

    lateinit var userNameTextView: TextView // 사용자 이름을 보여주는 TextView
    lateinit var diaryCountTextView: TextView // 총 일기 개수를 보여주는 TextView

    lateinit var changeNameBtn: Button
    lateinit var changePwBtn: Button
    lateinit var logoutBtn: Button

    lateinit var myHelper: MyDBHelper

    private lateinit var mDatabaseRef : DatabaseReference   // 실시간 데이터 베이스

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_my_page, container, false)

        userNameTextView = v.findViewById(R.id.userNameTextView)
        diaryCountTextView = v.findViewById(R.id.diaryCountTextView)

        changeNameBtn = v.findViewById(R.id.changeNameBtn)
        changePwBtn = v.findViewById(R.id.changePwBtn)
        logoutBtn = v.findViewById(R.id.logoutBtn)

        myHelper = MyDBHelper(requireContext())

        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        // 닉네임 보여주기
        loadNickname()

        // 일기 수 보여주기
        val db = myHelper.readableDatabase

        // 현재 로그인 되어 있는 아이디 가져오기(SharedPreferences)
        val sharedPrefs: SharedPreferences? = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("id", "")

        val query = "SELECT COUNT(*) FROM diaryTBL WHERE id='$id'"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            diaryCountTextView.text = count.toString()
        }

        cursor.close()

        // 이름 변경 액티비티로 이동
        changeNameBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ChangeNameActivity::class.java)
            startActivity(intent)
        })

        // 비밀번호 변경 액티비티로 이동
        changePwBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ChangePwActivity::class.java)
            startActivity(intent)
        })

        // 로그아웃 -> RegisterActivity로 이동
        logoutBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        })

        // Inflate the layout for this fragment
        return v
    }

    // 변경한 닉네임 즉시 반영
    override fun onResume() {
        super.onResume()
        loadNickname()
    }

    // 닉네임을 보여주는 함수
    private fun loadNickname() {
        // 현재 로그인 되어 있는 아이디 가져오기(SharedPreferences)
        val sharedPrefs: SharedPreferences? = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("id", "")

        val sqlDB = myHelper.readableDatabase

        val query = "SELECT nickname FROM userTBL WHERE id='$id';"
        val cursor: Cursor = sqlDB.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("nickname")
            val nickname = cursor.getString(columnIndex)
            userNameTextView.text = nickname
        }

        cursor.close()
        sqlDB.close()
    }

}