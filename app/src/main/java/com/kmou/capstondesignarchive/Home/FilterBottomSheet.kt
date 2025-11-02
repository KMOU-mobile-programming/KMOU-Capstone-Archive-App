package com.kmou.capstondesignarchive.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kmou.capstondesignarchive.R


class FilterBottomSheet : BottomSheetDialogFragment() {

    // 2단계에서 만든 XML 레이아웃을 화면에 띄우는 역할
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    // (선택) 여기에 칩(Chip)을 클릭했을 때의 로직 등을 나중에 추가할 수 있습니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 예: chip_2025.setOnClickListener { ... }
    }


    // 이 태그는 바텀시트를 식별하기 위한 이름표입니다.
    companion object {
        const val TAG = "FilterBottomSheet"
    }
}