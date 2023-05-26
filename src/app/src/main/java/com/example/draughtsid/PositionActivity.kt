package com.example.draughtsid

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
class PositionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val success = intent.getBooleanExtra("success", false)
        val blackMove: View
        val whiteMove: View
        if (success){
            val str: String = intent.getStringExtra("black") as String
            val str1 = intent.getStringExtra("white")
            setContentView(R.layout.activity_position)
            blackMove = findViewById<EditText>(R.id.blackMove)
            whiteMove = findViewById<EditText>(R.id.whiteMove)
            val board = findViewById<DraughtsView>(R.id.draughts_view)
            board.setPosition(str)
            blackMove.setText(str)
            whiteMove.setText(str1)
        }
        else {
            val str: String = intent.getStringExtra("white") as String
            setContentView(R.layout.activity_position)
            setContentView(R.layout.activity_position)
            blackMove = findViewById<EditText>(R.id.blackMove)
            whiteMove = findViewById<EditText>(R.id.whiteMove)
            whiteMove.setText(str)
        }
        val clipBoardManager: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val whiteCopy = findViewById<Button>(R.id.white_copy)
        val blackCopy = findViewById<Button>(R.id.black_copy)

        whiteCopy.setOnClickListener {
            val text = whiteMove.text.toString()
            val clipData = ClipData.newPlainText("text", text)
            clipBoardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Position copy!", Toast.LENGTH_SHORT).show()
        }

        blackCopy.setOnClickListener {
            val text = blackMove.text.toString()
            val clipData = ClipData.newPlainText("text", text)
            clipBoardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Position copy!", Toast.LENGTH_SHORT).show()
        }

    }

}