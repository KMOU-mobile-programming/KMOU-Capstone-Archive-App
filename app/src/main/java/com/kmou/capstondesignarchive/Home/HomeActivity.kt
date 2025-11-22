package com.kmou.capstondesignarchive.Home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kmou.capstondesignarchive.LoginActivity
import com.kmou.capstondesignarchive.Profile.ProfileActivity
import com.kmou.capstondesignarchive.R
import com.kmou.capstondesignarchive.Upload.UploadActivity

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

    // ✅ 페이지네이션 관련 변수
    private var lastVisible: DocumentSnapshot? = null // 마지막으로 불러온 문서
    private var isLoading = false // 데이터 로딩 중인지 여부
    private val pageSize = 10L // 한 번에 불러올 데이터 개수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        handleIntent(intent) // ✅ Intent 처리 로직 활성화

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupTopBarListeners()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 나타날 때마다 데이터를 새로고침합니다.
        refreshProjects()
    }

    // ✅ onNewIntent 추가: 앱이 이미 실행 중일 때 로그인 정보를 받기 위함
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    // ✅ Intent에서 userId를 추출하는 함수
    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId")
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.content_recycler_view)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        adapter = ContentAdapter(contentList)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (!isLoading && totalItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1) {
                    loadProjectsFromFirestore()
                }
            }
        })
    }

    private fun refreshProjects() {
        contentList.clear()
        lastVisible = null
        adapter.notifyDataSetChanged()
        loadProjectsFromFirestore()
    }

    private fun loadProjectsFromFirestore() {
        if (isLoading) return
        isLoading = true

        var query = db.collection("projects")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastVisible != null) {
            query = query.startAfter(lastVisible!!)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    isLoading = false
                    return@addOnSuccessListener
                }
                lastVisible = documents.documents[documents.size() - 1]
                for (doc in documents) {
                    val title = doc.getString("title") ?: "제목 없음"
                    val team = doc.getString("team") ?: "팀 미정"
                    contentList.add(ContentItem(R.drawable.dummy_image, title, team))
                }
                adapter.notifyDataSetChanged()
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
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

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    recyclerView.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_search -> {
                    val intent = Intent(this, com.kmou.capstondesignarchive.Search.SearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_upload -> {
                    if (!userId.isNullOrEmpty()) { // ✅ userId 유무로 확인
                        val intent = Intent(this, UploadActivity::class.java)
                        intent.putExtra("userId", userId) // ✅ UploadActivity에도 userId 전달
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    true
                }
                R.id.nav_bookmark -> {
                    Toast.makeText(this, "북마크 클릭됨", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    if (!userId.isNullOrEmpty()) { // ✅ userId 유무로 확인
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }
}
