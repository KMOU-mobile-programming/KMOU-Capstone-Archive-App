package com.kmou.capstondesignarchive.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.kmou.capstondesignarchive.R

class ProfileInfoFragment : Fragment() {

    private lateinit var tvSchool: TextView
    private lateinit var tvFaculty: TextView
    private lateinit var tvMajor: TextView
    private lateinit var tvName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile_info, container, false)

        tvSchool = view.findViewById(R.id.tvSchool)
        tvFaculty = view.findViewById(R.id.tvFaculty)
        tvMajor = view.findViewById(R.id.tvMajor)
        tvName = view.findViewById(R.id.tvName)

        // ✅ (수정) "studentId" -> "userId"로 키를 바로잡았습니다.
        val userId = activity?.intent?.getStringExtra("userId") ?: return view

        // ✅ Firebase DB에서 정보 가져오기
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child("students")
            .child(userId) // ✅ (수정) 변수명을 userId로 사용합니다.

        dbRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").value.toString()
                val faculty = snapshot.child("faculty").value.toString()
                val major = snapshot.child("major").value.toString()

                tvSchool.text = "국립한국해양대학교"
                tvName.text = name
                tvFaculty.text = faculty
                tvMajor.text = major
            } else {
                tvSchool.text = "정보를 불러올 수 없습니다."
            }
        }.addOnFailureListener {
            tvSchool.text = "DB 연결 오류: ${it.message}"
        }

        return view
    }
}
