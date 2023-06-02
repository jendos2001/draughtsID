package com.example.draughtsid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.draughtsid.databinding.ActivityGameResutlBinding
import com.example.draughtsid.databinding.ActivityMainBinding
import java.io.File

class GameResutlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameResutlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameResutlBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_game_resutl)
        var game = intent.getStringExtra("game")
        var pdn = intent.getStringExtra("pdn")
        var textView = findViewById<TextView>(R.id.textView)
        textView.text = game

        binding.SaveButton.setOnClickListener {
            val file = File("game.pdn")
            file.writeText(pdn!!, Charsets.UTF_8)
            Log.d("pdn", pdn)
            Toast.makeText(this, "Save in ${File("").absolutePath}", Toast.LENGTH_SHORT).show()
        }
    }
}