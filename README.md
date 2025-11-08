KMOU 모바일프로그래밍 - 한국해양대 캡스톤디자인아카이브 앱 프로젝트

app/ 경로에 google-services.json 파일 추가해야 합니다

⚙️ Firebase 설정

중요: app/ 경로에 google-services.json 파일을 추가해야 Firebase가 정상 작동합니다.

DB 유형: Cloud Firestore

Firebase Console: https://console.firebase.google.com/project/kmou-capstonedesignarchive

Collection: projects

예시 구조:

📂 projects (collection)
 ├─ 📄 [문서ID1]
 │   ├─ title: "자율주행 시뮬레이션 시스템"
 │   ├─ team: "Team 신"
 │   ├─ department: "인공지능공학부"
 │   ├─ summary: "딥러닝 기반 차량 인식 알고리즘을 활용한 시뮬레이션 프로젝트"
 │   ├─ createdAt: 1756863600000

🔍 Search 기능 업데이트
📁 관련 파일 구조
app/src/main/java/com/kmou/capstondesignarchive/Search/
 ├─ SearchActivity.kt        // Firestore 기반 검색 화면
 ├─ SearchAdapter.kt         // 검색 결과 카드 어댑터
 └─ Project.kt               // Firestore 프로젝트 데이터 모델

app/src/main/res/layout/
 ├─ activity_search.xml      // 검색 화면 UI
 ├─ Search/item_project.xml  // 검색 결과 카드 UI
 └─ activity_detail.xml      // 프로젝트 상세 보기 UI

app/src/main/res/drawable/
 └─ card_background.xml      // 카드 배경 디자인
