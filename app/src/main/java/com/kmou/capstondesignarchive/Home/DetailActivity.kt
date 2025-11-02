package com.kmou.capstondesignarchive.Home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kmou.capstondesignarchive.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_detail.xml 레이아웃을 이 액티비티의 화면으로 설정
        setContentView(R.layout.activity_detail)

        // HomeActivity의 ContentAdapter에서 전달받은 정보
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("subtitle")

        // ✅ (수정) activity_detail.xml의 실제 ID로 변경
        val titleTextView = findViewById<TextView>(R.id.projectTitle)
        val subtitleTextView = findViewById<TextView>(R.id.teamName)

        // TextView에 전달받은 텍스트 설정
        titleTextView.text = title
        subtitleTextView.text = subtitle
    }
}
