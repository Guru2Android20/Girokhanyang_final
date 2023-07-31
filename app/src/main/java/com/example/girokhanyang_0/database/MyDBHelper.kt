package com.example.girokhanyang_0.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "myDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        // 사용자 정보를 저장하는 userTBL(아이디, 닉네임, 비밀번호)
        db!!.execSQL("CREATE TABLE userTBL ( id TEXT PRIMARY KEY, nickname TEXT, password TEXT );")

        // 일기 정보를 저장하는 diaryTBL(아이디, 내용, 기분(1~5), 작성시간)
        db!!.execSQL("CREATE TABLE diaryTBL ( id TEXT, contents TEXT, mood INT, writingTime TEXT, PRIMARY KEY(id, writingTime) );")
        db?.apply {
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기1', 3, '2023-08-01T10:00:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기2', 2, '2023-07-30T21:10:41' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기3', 1, '2023-07-30T17:35:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기4', 5, '2023-07-29T13:00:35' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기5', 3, '2023-07-24T08:16:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기6', 1, '2023-07-08T16:08:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'test111@naver.com', '작성된 일기7', 2, '2023-06-30T18:24:11' );")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS userTBL")
        db!!.execSQL("DROP TABLE IF EXISTS diaryTBL")
        onCreate(db)
    }
}