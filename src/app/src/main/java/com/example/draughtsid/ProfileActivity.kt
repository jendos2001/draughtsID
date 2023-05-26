package com.example.draughtsid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.draughtsid.databinding.ActivityMainBinding
import com.example.draughtsid.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_game_resutl)
        binding.pos.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivityForResult(intent, 2)
        }
        binding.game.setOnClickListener {
            val intent = Intent(this@ProfileActivity, GameActivity::class.java)
            startActivityForResult(intent, 3)
        }
    }
}