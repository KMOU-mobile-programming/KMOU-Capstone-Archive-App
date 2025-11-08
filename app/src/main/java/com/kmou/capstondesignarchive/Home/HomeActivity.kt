package com.kmou.capstondesignarchive.Home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.kmou.capstondesignarchive.LoginActivity
import com.kmou.capstondesignarchive.Profile.ProfileActivity
import com.kmou.capstondesignarchive.R

// ✅ Firestore 데이터 구조와 맞춘 데이터 클래스
data class ProjectItem(
    val title: String = "",
    val team: String = "",
    val department: String = "",
    val summary: String = ""
)

class HomeActivity : AppCompatActivity() {

    private var userId: String? = null

    // ✅ Firestore, RecyclerView 관련 변수
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContentAdapter
    private val contentList = mutableListOf<ContentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        handleIntent(intent)

        // ✅ Firestore 초기화
        db = FirebaseFirestore.getInstance()

        // ✅ RecyclerView 초기화
        recyclerView = findViewById(R.id.content_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ContentAdapter(contentList)
        recyclerView.adapter = adapter

        // ✅ Firestore에서 데이터 불러오기
        loadProjectsFromFirestore()

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


    // ✅ Firestore에서 프로젝트 데이터 불러오기
    private fun loadProjectsFromFirestore() {
        db.collection("projects")
            .get()
            .addOnSuccessListener { result ->
                contentList.clear()
                for (doc in result) {
                    val title = doc.getString("title") ?: "제목 없음"
                    val team = doc.getString("team") ?: "팀 미정"
                    val department = doc.getString("department") ?: "학부/전공"
                    val summary = doc.getString("summary") ?: "내용 없음"

                    // 기존 ContentItem 구조에 맞게 변환
                    contentList.add(ContentItem(R.drawable.dummy_image, title, team))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupTopBarListeners() {
        val searchIcon: ImageView = findViewById(R.id.icon_search)
        val filterIcon: ImageView = findViewById(R.id.icon_filter)

        searchIcon.setOnClickListener {
            val intent = Intent(this, com.kmou.capstondesignarchive.Search.SearchActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                R.id.nav_search -> {
                    val intent = Intent(this, com.kmou.capstondesignarchive.Search.SearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
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