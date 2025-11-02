package com.kmou.capstondesignarchive

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    private lateinit var userTypeSpinner: Spinner
    private lateinit var studentLayout: LinearLayout
    private lateinit var professorLayout: LinearLayout
    private lateinit var companyLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        userTypeSpinner = findViewById(R.id.userTypeSpinner)
        studentLayout = findViewById(R.id.studentLayout)
        professorLayout = findViewById(R.id.professorLayout)
        companyLayout = findViewById(R.id.companyLayout)

        userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> showLayout("student")
                    1 -> showLayout("professor")
                    2 -> showLayout("company")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            registerUser()
        }
    }

    private fun showLayout(type: String) {
        studentLayout.visibility = if (type == "student") View.VISIBLE else View.GONE
        professorLayout.visibility = if (type == "professor") View.VISIBLE else View.GONE
        companyLayout.visibility = if (type == "company") View.VISIBLE else View.GONE
    }

    private fun registerUser() {
        val selectedType = userTypeSpinner.selectedItem.toString()

        when (selectedType) {
            "한국해양대 학생" -> {
                val id = findViewById<EditText>(R.id.editStudentId).text.toString()
                val name = findViewById<EditText>(R.id.editStudentName).text.toString()
                val pw = findViewById<EditText>(R.id.editStudentPassword).text.toString()
                val faculty = findViewById<EditText>(R.id.editStudentFaculty).text.toString()
                val major = findViewById<EditText>(R.id.editStudentMajor).text.toString()

                if (id.isEmpty() || name.isEmpty() || pw.isEmpty() || faculty.isEmpty() || major.isEmpty()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                // (기존 코드 동일 - 양호)
                dbRef.child("students").child(id).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "이미 존재하는 학번입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        val student = Student(id, name, pw, faculty, major)
                        dbRef.child("students").child(id).setValue(student)
                            .addOnSuccessListener {
                                Toast.makeText(this, "학생 회원가입 완료!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "DB 연결 오류: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

            "교수" -> {
                val id = findViewById<EditText>(R.id.editProfessorId).text.toString()
                val name = findViewById<EditText>(R.id.editProfessorName).text.toString()
                val pw = findViewById<EditText>(R.id.editProfessorPassword).text.toString()
                val faculty = findViewById<EditText>(R.id.editProfessorFaculty).text.toString()
                val major = findViewById<EditText>(R.id.editProfessorMajor).text.toString()

                if (id.isEmpty() || name.isEmpty() || pw.isEmpty() || faculty.isEmpty() || major.isEmpty()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                // ✅ 2. 교수 ID 중복 확인 로직 (추가)
                dbRef.child("professors").child(id).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        val professor = Professor(id, name, pw, faculty, major)
                        dbRef.child("professors").child(id).setValue(professor)
                            .addOnSuccessListener {
                                Toast.makeText(this, "교수 등록 완료", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "DB 연결 오류: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

            "기업" -> {
                val companyName = findViewById<EditText>(R.id.editCompanyName).text.toString()
                val id = findViewById<EditText>(R.id.editCompanyId).text.toString()
                val pw = findViewById<EditText>(R.id.editCompanyPassword).text.toString()

                if (companyName.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                // ✅ 3. 기업 ID 중복 확인 로직 (추가)
                dbRef.child("companies").child(id).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        val company = Company(companyName, id, pw)
                        dbRef.child("companies").child(id).setValue(company)
                            .addOnSuccessListener {
                                Toast.makeText(this, "기업 등록 완료", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "DB 연결 오류: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

/** 데이터 클래스 정의 **/
// (데이터 클래스는 변경 사항 없음)
data class Student(
    val studentId: String = "",
    val name: String = "",
    val password: String = "",
    val faculty: String = "",
    val major: String = ""
)

data class Professor(
    val id: String = "",
    val name: String = "",
    val password: String = "",
    val faculty: String = "",
    val major: String = ""
)

data class Company(
    val companyName: String = "",
    val id: String = "",
    val password: String = ""
)