package com.example.draughtsid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.draughtsid.databinding.ActivityMainBinding
import com.example.draughtsid.databinding.ActivityMetadataBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MetadataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMetadataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var pdn: String
        binding = ActivityMetadataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ContinueButton.setOnClickListener {
            val startPosition = intent.getStringExtra("startPosition")
            val game = intent.getStringExtra("game")
            pdn = toPDN(startPosition!!, game!!, binding.WhiteName.text.toString(),
                binding.WhiteSurname.text.toString(), binding.BlackName.text.toString(),
                binding.BlackSurname.text.toString(), binding.Event.text.toString(),
                binding.Round.text.toString(), binding.Result.text.toString()
            )
            val intent = Intent(this@MetadataActivity, GameResutlActivity::class.java)
            intent.putExtra("pdn", pdn)
            intent.putExtra("game", game)
            startActivityForResult(intent, 5)
        }
    }

    fun toPDN(startPosition: String, game: String, whiteName: String, whiteSurname: String,
              blackName: String, blackSurname: String, event: String, round: String, result: String): String {
        return "[White \"$whiteName $whiteSurname\"]\n" +
                "[White \"$blackName $blackSurname\"]\n" +
                "[Event \"$event\"]\n" +
                "[Round \"$round\"]\n" +
                "[Site \"\"]\n" +
                "[Date \"${
                    LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("MM dd yyyy, HH:mm:ss"))
                }\"]\n" +
                "[Result \"$result\"]\n" +
                "[GameType \"25\"]\n" +
                "[FEN \"$startPosition\"]\n" +
                "\n" +
                "$game $result"
    }
}

/*
[White ""]
[Black ""]
[Event ""]
[Round ""]
[Site ""]
[Date ""]
[Result "2-0"]
[GameType "25"]
[FEN "W:WK26:B8,9,10,11,19"]

1. d2xh6xf8xc5xa7 f6-g5 2. a7-b8 g5-h4 3. b8-h2 h4-g3 4. h2xe5
2-0
 */