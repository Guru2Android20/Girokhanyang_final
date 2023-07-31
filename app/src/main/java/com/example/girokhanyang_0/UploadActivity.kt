package com.example.girokhanyang_0

import DiaryEntry
import SqliteHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Calendar

class UploadActivity : AppCompatActivity(){
    /*private var mAuth: FirebaseAuth? = null
    private var storage: FirebaseStorage? = null
    private var database: FirebaseDatabase? = null*/

    private lateinit var addImg : ImageView

    private lateinit var imgHappyGray : ImageView
    private lateinit var imgGoodGray : ImageView
    private lateinit var imgSosoGray : ImageView
    private lateinit var imgNotgoodGray : ImageView
    private lateinit var imgBadGray : ImageView

    private lateinit var etWrite : EditText
    private lateinit var btnUpload : Button
    private lateinit var recylerview : RecyclerView
    private var imageUrl = ""

    private lateinit var btnDate : Button

    // 이미지 데이터 리스트
    var list = ArrayList<Uri>()
    val adapter = MultiImageAdapter(list, this)

    private val helper = SqliteHelper(this, "diaryEntry", 3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        /*mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()*/

        // 기분 이미지
        imgHappyGray = findViewById<ImageView>(R.id.img_happy_gray)
        imgGoodGray = findViewById<ImageView>(R.id.img_good_gray)
        imgSosoGray = findViewById<ImageView>(R.id.img_soso_gray)
        imgNotgoodGray = findViewById<ImageView>(R.id.img_notgood_gray)
        imgBadGray = findViewById<ImageView>(R.id.img_bad_gray)

        // 작성 내용
        etWrite = findViewById(R.id.et_write)

        // 이미지 불러오기 버튼
        addImg = findViewById<ImageView>(R.id.addImg)
        // 리사이클러뷰
        recylerview = findViewById<RecyclerView>(R.id.recyclerView)

        // 이미지 업로드
        btnUpload = findViewById(R.id.btnUpload)

        // 날짜
        btnDate = findViewById<Button>(R.id.btnDate)

        val c = Calendar.getInstance()  // 현재 날짜 설정
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        btnDate.text = "${year}년 ${month + 1}월 ${day}일"

        btnDate.setOnClickListener {    // 클릭하여 날짜 변경 가능

            val listener = DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val date = "${year}년 ${monthOfYear + 1}월 ${dayOfMonth}일" // 월은 0부터 시작하므로 +1을 해줍니다.
                btnDate.text = date
            }

            val dialog = DatePickerDialog(this, listener, year, month, day)
            dialog.show()
        }

        // 기분 선택
        val imageViews = listOf(
            imgHappyGray,
            imgGoodGray,
            imgSosoGray,
            imgNotgoodGray,
            imgBadGray
        )

        imageViews.forEach { imageView ->
            imageView.setOnClickListener {
                val selectedImage = when (imageView) {
                    imgHappyGray -> R.mipmap.happy_round
                    imgGoodGray -> R.mipmap.good_round
                    imgSosoGray -> R.mipmap.soso_round
                    imgNotgoodGray -> R.mipmap.notgood_round
                    imgBadGray -> R.mipmap.bad_round
                    else -> throw IllegalArgumentException("Unknown ImageView")
                }

                // 선택한 이미지 변경
                imageView.setImageResource(selectedImage)

                // 나머지 이미지들 선택 못하게 처리
                imageViews.forEach { otherImageView ->
                    if (otherImageView != imageView) {
                        val grayImage = when (otherImageView) {
                            imgHappyGray -> R.mipmap.happy_gray_round
                            imgGoodGray -> R.mipmap.good_gray_round
                            imgSosoGray -> R.mipmap.soso_gray_round
                            imgNotgoodGray -> R.mipmap.notgood_gray_round
                            imgBadGray -> R.mipmap.bad_gray_round
                            else -> throw IllegalArgumentException("Unknown ImageView")
                        }
                        otherImageView.setImageResource(grayImage)
                    }
                }
            }
        }

        // 이미지 추가
        addImg.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 200)
        }

        val layoutManager = LinearLayoutManager(this)
        recylerview.layoutManager = layoutManager
        recylerview.adapter = adapter

        // 업로드 클릭 시 데이터 삽입
        btnUpload.setOnClickListener {
            saveData()

            // 메인으로 이동
            val intent = Intent(this, NaviActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        fun init() {
            val ba = intent.getByteArrayExtra("dImage")

            if (intent.getByteArrayExtra("dImage") != null) {
                val bitmap = BitmapFactory.decodeByteArray(ba, 0, ba!!.size)
                addImg.setImageBitmap(bitmap)
            }
        }
    }

    // 이미지 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data?.clipData != null) { // 사진 여러개 선택한 경우
            val count = data.clipData!!.itemCount
            if (count > 10) {
                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
                return
            }
            for (i in 0 until count) {
                val imageUri = data.clipData!!.getItemAt(i).uri
                list.add(imageUri)
            }

        } else { // 단일 선택
            data?.data?.let { uri ->
                val imageUri : Uri? = data?.data
                if (imageUri != null) {
                    list.add(imageUri)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun drawbleToByteArray(drawable: Drawable?) : ByteArray? {
        val bitmapDrawable = drawable as BitmapDrawable?
        val bitmap = bitmapDrawable?.bitmap
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        return byteArray
    }

    private fun saveData() {
        val dDate = btnDate.text.toString()
        val dMood = addImg.drawable
        val dContent = etWrite.text.toString()
        val dImage = addImg.drawable

        val diaryEntry = DiaryEntry(null, dDate, drawbleToByteArray(dMood), dContent, drawbleToByteArray(dImage))
        helper.insertData(diaryEntry)

        Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateData() {
        val dDate = btnDate.text.toString()
        val dMood = addImg.drawable
        val dContent = etWrite.text.toString()
        val dImage = addImg.drawable
        val id = intent.getLongExtra("id", -1)

        val diaryEntry = DiaryEntry(id, dDate, drawbleToByteArray(dMood), dContent, drawbleToByteArray(dImage))
        helper.updateData(diaryEntry)

        Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

}













