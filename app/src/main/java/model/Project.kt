package com.kmou.capstondesignarchive.model

/**
 * @param content 프로젝트의 상세 내용을 순서대로 저장하는 리스트.
 *                각 항목은 Map으로, "type"과 "value" 키를 가짐.
 *                - type: "image", "text", "html"
 *                - value: 이미지 URL, 텍스트 내용, HTML 코드 등
 */
data class Project(
    val title: String = "",
    val team: String = "",
    val department: String = "",
    val content: List<Map<String, String>> = emptyList(), // summary를 대체하는 새로운 필드
    val createdAt: Long = 0L,
    val uploaderUid: String = "" // 업로드한 사용자의 UID 필드
)
