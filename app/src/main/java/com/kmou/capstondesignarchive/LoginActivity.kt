package com.kmou.capstondesignarchive

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot // ✅ 1. 헬퍼 함수용 DataSnapshot 임포트
import com.kmou.capstondesignarchive.Home.HomeActivity
class LoginActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val editTextId = findViewById<EditText>(R.id.editTextId)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // 뒤로가기 버튼 클릭 시
        btnBack.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        // 로그인 버튼 클릭 시
        btnSignIn.setOnClickListener {
            // ✅ 3. 'studentId' -> 'id'로 변수명 변경 (더 범용적)
            val id = editTextId.text.toString().trim()
            val password = editTextPassword.text.toString().trim()


            if (id.isEmpty() || password.isEmpty()) {
                // ✅ 4. '학번' -> 'ID'로 문구 수정
                Toast.makeText(this, "ID와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ 5. 로그인 로직 전체 수정 (학생 -> 교수 -> 기업 순차 검색)
            // 1. 학생(students) 노드에서 검색
            dbRef.child("students").child(id).get().addOnSuccessListener { studentSnapshot ->
                if (studentSnapshot.exists()) {
                    // 학생 ID가 존재하면 비밀번호 확인
                    checkPasswordAndLogin(studentSnapshot, password, id)
                } else {
                    // 2. 학생 노드에 없으면 교수(professors) 노드에서 검색
                    dbRef.child("professors").child(id).get().addOnSuccessListener { professorSnapshot ->
                        if (professorSnapshot.exists()) {
                            // 교수 ID가 존재하면 비밀번호 확인
                            checkPasswordAndLogin(professorSnapshot, password, id)
                        } else {
                            // 3. 교수 노드에도 없으면 기업(companies) 노드에서 검색
                            dbRef.child("companies").child(id).get().addOnSuccessListener { companySnapshot ->
                                if (companySnapshot.exists()) {
                                    // 기업 ID가 존재하면 비밀번호 확인
                                    checkPasswordAndLogin(companySnapshot, password, id)
                                } else {
                                    // 4. 모든 노드에 ID가 없음
                                    Toast.makeText(this, "존재하지 않는 ID입니다", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                // 기업 노드 검색 실패
                                Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        // 교수 노드 검색 실패
                        Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                // 학생 노드 검색 실패
                Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 버튼 클릭 시
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        Log.d("FirebaseTest", "DB 연결 상태: ${FirebaseDatabase.getInstance().reference}")
    }


    // ✅ 6. 중복 로직을 처리할 헬퍼 함수 추가
    /**
     * DataSnapshot에서 비밀번호를 확인하고 로그인을 처리하는 함수
     * @param snapshot Firebase에서 가져온 DataSnapshot (학생, 교수, 기업 중 하나)
     * @param inputPassword 사용자가 입력한 비밀번호
     * @param userId 사용자 ID (HomeActivity로 전달할 값)
     */
    private fun checkPasswordAndLogin(snapshot: DataSnapshot, inputPassword: String, userId: String) {
        val storedPassword = snapshot.child("password").value.toString()

        // 'name' (학생/교수) 또는 'companyName' (기업) 필드에서 이름을 가져옴
        val name = snapshot.child("name").value?.toString()
            ?: snapshot.child("companyName").value?.toString()
            ?: "사용자" // 둘 다 없으면 '사용자'

        if (storedPassword == inputPassword) {
            // 비밀번호 일치 -> 로그인 성공
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userName", name)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        } else {
            // 비밀번호 불일치
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }
}