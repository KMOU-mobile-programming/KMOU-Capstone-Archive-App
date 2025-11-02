package com.kmou.capstondesignarchive.Profile

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kmou.capstondesignarchive.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnWork: Button
    private lateinit var btnInfo: Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 🔹 버튼 초기화 (제네릭 필수)
        btnWork = findViewById<Button>(R.id.btnWork)
        btnInfo = findViewById<Button>(R.id.btnInfo)
        btnSave = findViewById<Button>(R.id.btnSave)

        // 🔹 앱 진입 시 기본은 "정보" 탭
        selectTab(btnInfo)
        replaceFragment(ProfileInfoFragment())

        // 🔹 탭 클릭 리스너
        btnWork.setOnClickListener {
            selectTab(btnWork)
            replaceFragment(ProfileWorkFragment())
        }

        btnInfo.setOnClickListener {
            selectTab(btnInfo)
            replaceFragment(ProfileInfoFragment())
        }

        btnSave.setOnClickListener {
            selectTab(btnSave)
            replaceFragment(ProfileSaveFragment())
        }
    }

    // 🔹 프래그먼트 전환 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.profileFragmentContainer, fragment)
            .commit()
    }

    // 🔹 탭 색상 및 배경 전환 함수
    private fun selectTab(selected: Button) {
        val selectedBg = ContextCompat.getDrawable(this, R.drawable.tab_selected_bg)
        val unselectedBg = ContextCompat.getDrawable(this, R.drawable.tab_unselected_bg)

        val white = ContextCompat.getColor(this, android.R.color.white)
        val black = ContextCompat.getColor(this, android.R.color.black)

        val buttons = listOf(btnWork, btnInfo, btnSave)
        for (btn in buttons) {
            if (btn == selected) {
                btn.background = selectedBg
                btn.setTextColor(white)
            } else {
                btn.background = unselectedBg
                btn.setTextColor(black)
            }
        }
    }
}
