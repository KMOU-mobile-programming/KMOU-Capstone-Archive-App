package com.kmou.capstondesignarchive.Search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kmou.capstondesignarchive.R

// ✅ Firestore의 문서 구조를 반영한 데이터 클래스
data class Project(
    val title: String = "",
    val team: String = "",
    val department: String = "",
    val summary: String = "",
    val createdAt: Long = 0L
)

class SearchActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var searchInput: EditText
    private lateinit var btnCancel: TextView

    private val projectList = mutableListOf<Project>()
    private val filteredList = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // ✅ Firestore 초기화
        db = FirebaseFirestore.getInstance()

        // ✅ 뷰 연결
        recyclerView = findViewById(R.id.recyclerViewSearch)
        searchInput = findViewById(R.id.etSearch)
        btnCancel = findViewById(R.id.btnCancel)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchAdapter(filteredList)
        recyclerView.adapter = adapter

        // ✅ Firestore에서 프로젝트 목록 불러오기
        loadProjectsFromFirestore()

        // ✅ 검색어 입력 시 필터링
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterProjects(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ✅ 취소 버튼 클릭 시 검색창 초기화
        btnCancel.setOnClickListener {
            searchInput.text.clear()
            filterProjects("")
        }
    }

    // 🔹 Firestore에서 전체 프로젝트 불러오기
    private fun loadProjectsFromFirestore() {
        db.collection("projects")
            .get()
            .addOnSuccessListener { result ->
                projectList.clear()
                for (doc in result) {
                    val project = doc.toObject(Project::class.java)
                    projectList.add(project)
                }
                filteredList.clear()
                filteredList.addAll(projectList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 검색어 필터링 (대소문자 구분 없음) - 키워드가 db에서 불러온 데이터 각 항목의 title, team, department, summary 중 하나라도 부분적으로 포함되어 있으면 결과에 표시
    private fun filterProjects(query: String) {
        val searchText = query.lowercase()
        filteredList.clear()
        if (searchText.isEmpty()) {
            filteredList.addAll(projectList)
        } else {
            filteredList.addAll(
                projectList.filter {
                    it.title.lowercase().contains(searchText)
                            || it.team.lowercase().contains(searchText)
                            || it.department.lowercase().contains(searchText)
                            || it.summary.lowercase().contains(searchText)
                }
            )
        }
        adapter.notifyDataSetChanged()
    }
}
