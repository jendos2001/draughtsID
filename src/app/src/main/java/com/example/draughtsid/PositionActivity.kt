package com.example.draughtsid

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class PositionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)
        val str = intent.getStringExtra("test")
        val str1 = str + "1"
        val blackMove = findViewById<EditText>(R.id.blackMove)
        val whiteMove = findViewById<EditText>(R.id.whiteMove)
        blackMove.setText(str)
        whiteMove.setText(str1)
    }

}