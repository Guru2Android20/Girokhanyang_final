package com.example.girokhanyang_0.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "myDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        // 사용자 정보를 저장하는 userTBL(아이디, 닉네임, 비밀번호)
        db!!.execSQL("CREATE TABLE userTBL ( id TEXT PRIMARY KEY, nickname TEXT, password TEXT );")
        db?.apply {
            execSQL("INSERT INTO userTBL VALUES ( 'swuni1', '슈니1', '1234' );")
            execSQL("INSERT INTO userTBL VALUES ( 'swuni2', '슈니2', '5678' );")
            execSQL("INSERT INTO userTBL VALUES ( 'swuni3', '슈니3', '0000' );")
        }

        // 일기 정보를 저장하는 diaryTBL(아이디, 내용, 기분(1~5), 작성시간)
        db!!.execSQL("CREATE TABLE diaryTBL ( id TEXT, contents TEXT, mood INT, writingTime TEXT, PRIMARY KEY(id, writingTime) );")
        db?.apply {
            execSQL("INSERT INTO diaryTBL VALUES ( 'swuni1', '작성된 일기1', 1, '2023-07-28T12:34:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'swuni1', '작성된 일기2', 2, '2023-07-28T20:34:56' );")
            execSQL("INSERT INTO diaryTBL VALUES ( 'swuni1', '작성된 일기3', 3, '2023-07-29T12:34:56' );")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS userTBL")
        db!!.execSQL("DROP TABLE IF EXISTS diaryTBL")
        onCreate(db)
    }
}