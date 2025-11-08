package com.kmou.capstondesignarchive.Home

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kmou.capstondesignarchive.R
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // ✅ XML 뷰 연결
        val projectImage = findViewById<ImageView>(R.id.projectImage)
        val titleTextView = findViewById<TextView>(R.id.projectTitle)
        val teamTextView = findViewById<TextView>(R.id.teamName)
        val descriptionTextView = findViewById<TextView>(R.id.projectDescription)
        val uploadDateTextView = findViewById<TextView>(R.id.uploadDate)
        val awardStatusTextView = findViewById<TextView>(R.id.awardStatus)
        val majorTextView = findViewById<TextView>(R.id.major)
        val viewVideoButton = findViewById<Button>(R.id.viewVideo)

        // ✅ Intent 데이터 받기
        val title = intent.getStringExtra("title")
        val team = intent.getStringExtra("team")
        val department = intent.getStringExtra("department")
        val summary = intent.getStringExtra("summary")
        val createdAt = intent.getLongExtra("createdAt", 0L)

        // ✅ 화면에 표시
        titleTextView.text = title
        teamTextView.text = "$team ($department)"
        descriptionTextView.text = summary
        majorTextView.text = "학과/전공: $department"

        // ✅ 날짜 표시 포맷
        if (createdAt > 0) {
            val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
            val dateStr = sdf.format(Date(createdAt))
            uploadDateTextView.text = "업로드 날짜: $dateStr"
        } else {
            uploadDateTextView.text = "업로드 날짜: 정보 없음"
        }

        // ✅ (선택) 수상 정보 — Firestore에서 별도 필드 연결 시 사용
        awardStatusTextView.text = "수상 실적: 없음"

        // ✅ 시연 영상 버튼 클릭 (YouTube 등 외부 링크로 연결)
        viewVideoButton.setOnClickListener {
            // 추후 Firestore에 videoUrl 추가 시 Intent로 전달해서 open 가능
            // startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
        }
    }
}
