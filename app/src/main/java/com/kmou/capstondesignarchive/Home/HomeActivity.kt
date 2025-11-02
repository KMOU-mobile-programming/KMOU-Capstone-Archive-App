package com.kmou.capstondesignarchive.Home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kmou.capstondesignarchive.LoginActivity
import com.kmou.capstondesignarchive.Profile.ProfileActivity
import com.kmou.capstondesignarchive.R

class HomeActivity : AppCompatActivity() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        handleIntent(intent)

        // 1. RecyclerView 설정
        setupRecyclerView()

        // 2. 상단 바 아이콘 설정
        setupTopBarListeners()

        // 3. 하단 네비게이션 설정
        setupBottomNavigation()
    }

    // ✅ 6. HomeActivity가 이미 켜진 상태에서 로그인 성공 시(필수)
    // LoginActivity에서 FLAG_ACTIVITY_SINGLE_TOP으로 호출하면
    // onCreate() 대신 이 함수가 호출됩니다.
    // ✅ (수정됨) 파라미터에서 '?'를 제거했습니다. (Intent? -> Intent)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // ✅ (수정됨) intent가 null이 아니므로 'let' 없이 바로 호출합니다.
        handleIntent(intent)
    }

    // ✅ 7. 인텐트에서 userId를 추출하는 함수 (중복 코드 방지)
    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId")
            // (선택) 로그인 환영 메시지
            // Toast.makeText(this, "${intent.getStringExtra("userName")}님 환영합니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView() {
        // 🚨 주의: R.layout.activity_home 파일 안에
        // 'content_recycler_view'라는 ID를 가진 RecyclerView가 있는지 꼭 확인하세요!
        // ID가 없거나 다르면 여기서 앱이 튕깁니다.
        val recyclerView: RecyclerView = findViewById(R.id.content_recycler_view)

        // 예시 컨텐츠 (더미 데이터) 생성
        val contentList = listOf(
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신"),
            ContentItem(R.drawable.dummy_image, "보안", "Team DFV"),
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신"),
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신"),
            ContentItem(R.drawable.dummy_image, "AI 모델", "Team K"),
            ContentItem(R.drawable.dummy_image, "로보틱스", "Team 로봇"),
            ContentItem(R.drawable.dummy_image, "웹 서비스", "Team 웹"),
            ContentItem(R.drawable.dummy_image, "앱 개발", "Team 앱"),
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신"),
            ContentItem(R.drawable.dummy_image, "보안", "Team DFV"),
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신"),
            ContentItem(R.drawable.dummy_image, "자율 주행", "Team 신")
        )

        // 어댑터 생성 및 연결
        val adapter = ContentAdapter(contentList)
        recyclerView.adapter = adapter
    }

    private fun setupTopBarListeners() {
        // 🚨 주의: 'icon_search', 'icon_filter' ID도 XML 파일에 있는지 확인하세요.
        val searchIcon: ImageView = findViewById(R.id.icon_search)
        val filterIcon: ImageView = findViewById(R.id.icon_filter)

        searchIcon.setOnClickListener {
            Toast.makeText(this, "검색 클릭됨 (기능 구현 필요)", Toast.LENGTH_SHORT).show()
            // TODO: 검색 화면으로 이동하는 로직
        }

        filterIcon.setOnClickListener {
            val filterBottomSheet = FilterBottomSheet()
            filterBottomSheet.show(supportFragmentManager, FilterBottomSheet.TAG)
        }
    }

    // ✅ 8. 하단 네비게이션 로직 전체 수정
    private fun setupBottomNavigation() {
        // 🚨 주의: 'bottom_navigation' ID도 XML 파일에 있는지 확인하세요.
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "홈 클릭됨", Toast.LENGTH_SHORT).show()
                    true // true를 반환해야 선택된 것으로 처리됩니다.
                }
                R.id.nav_upload -> {
                    Toast.makeText(this, "업로드 클릭됨", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_bookmark -> {
                    Toast.makeText(this, "북마크 클릭됨", Toast.LENGTH_SHORT).show()
                    true
                }

                // ✅ 9. 요청하신 핵심 로직: 프로필 버튼 분기 처리
                R.id.nav_profile -> {
                    if (userId.isNullOrEmpty()) {
                        // 로그아웃 상태 (userId가 null이거나 비어있음)
                        // -> 로그인 화면으로 이동
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 로그인 상태 (userId가 있음)
                        // -> 프로필 화면으로 이동 (구 MainActivity의 기능)
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("userId", userId) // userId를 프로필 화면에 전달
                        startActivity(intent)
                    }
                    true // 이벤트를 처리했음
                }

                else -> false // 그 외의 경우는 처리 안 함
            }
        }
    }
}