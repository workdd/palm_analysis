package com.example.palmanalysis.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.palmanalysis.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESULT_TEXT = "extra_result_text"
    }

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 결과 텍스트 가져오기
        val resultText = intent.getStringExtra(EXTRA_RESULT_TEXT) ?: "결과를 불러올 수 없습니다."
        
        // 결과 표시
        binding.tvResult.text = resultText
    }
}
