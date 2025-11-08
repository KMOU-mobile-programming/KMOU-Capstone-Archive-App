### 한국해양대학교 캡스톤디자인아카이브 앱 프로젝트  
### 📱 KMOU 모바일프로그래밍 과제

---

### ⚙️ Firebase 설정

> **중요:** `app/` 경로에 `google-services.json` 파일을 추가해야 Firebase가 정상 작동합니다.

- **DB 유형:** Cloud Firestore  
- **Firebase Console:** [[https://console.firebase.google.com/project/kmou-capstondesignarchive](https://console.firebase.google.com/u/0/project/kmou-capstonedesignarchive/overview)](https://console.firebase.google.com/project/kmou-capstondesignarchive)  
- **Collection:** `projects`

**예시 구조:**

<pre>
projects (collection)
├─ [문서ID1]
│  ├─ title: "자율주행 시뮬레이션 시스템"
│  ├─ team: "Team 신"
│  ├─ department: "인공지능공학부"
│  ├─ summary: "딥러닝 기반 차량 인식 알고리즘을 활용한 시뮬레이션 프로젝트"
│  └─ createdAt: 1756863600000
</pre>

---

### 🔍 Search 기능 업데이트

##### 📁 관련 파일 구조

<pre>
app/src/main/java/com/kmou/capstondesignarchive/Search/
 ├─ SearchActivity.kt       // Firestore 기반 검색 화면
 ├─ SearchAdapter.kt        // 검색 결과 카드 어댑터
 └─ Project.kt              // Firestore 프로젝트 데이터 모델

 app/src/main/java/com/kmou/capstondesignarchive/Home/
 ├─ HomeActivity.kt → 메인 홈화면 (Firestore 데이터 연동)
 └─ DetailActivity.kt → 프로젝트 상세 보기 화면

app/src/main/res/layout/
 ├─ activity_search.xml     // 검색 화면 UI
 ├─ Search/item_project.xml // 검색 결과 카드 UI
 └─ activity_detail.xml     // 프로젝트 상세 보기 UI

app/src/main/res/drawable/
 └─ card_background.xml     // 카드 배경 디자인
</pre>

---

#### 🧩 기능 요약

| 기능 | 설명 |
|------|------|
| **프로젝트 검색** | Firestore에서 프로젝트 제목, 팀명, 학부(Department) 기반 검색 |
| **실시간 필터링** | EditText 입력 시 자동 필터링 |
| **프로젝트 상세보기** | 클릭 시 세부 정보 페이지로 이동 |
| **Firebase 연동** | Firestore 컬렉션(`projects`)과 연동 |
