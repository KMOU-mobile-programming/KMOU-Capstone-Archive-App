KMOU 모바일프로그래밍 - 한국해양대 캡스톤디자인아카이브 앱 프로젝트

app/ 경로에 google-services.json 파일 추가해야 합니다

====================================================================
Search 기능 관련 업데이트 사항
1. 📁 Search 관련 파일
**app/src/main/java/com/kmou/capstondesignarchive/Search/**
SearchActivity.kt → 검색 화면 (Firestore 기반)
SearchAdapter.kt → 검색 결과 카드 어댑터
Project.kt → Firestore 프로젝트 데이터 모델
**res/layout/**
activity\_search.xml → 검색 화면 UI
Search/item\_project.xml → 검색 결과 카드 UI
activity\_detail.xml → 프로젝트 상세 보기 UI
**drawable/**
card\_background.xml (카드 배경)

#### 🔥 Firebase 
1. DB 유형: Cloud Firestore 
2. Firebase Console URL: [https://console.firebase.google.com/project/kmou-capstonedesignarchive](https://console.firebase.google.com/project/kmou-capstonedesignarchive)
3. Collection: `projects`
    Firestore에서 모든 프로젝트 데이터는 `projects` 컬렉션에 저장
    예시 구조:
    📂 projects (collection)
    ├─ 📄 \[문서ID1]
    │ ├─ title: "자율주행 시뮬레이션 시스템"
    │ ├─ team: "Team 신"
    │ ├─ department: "인공지능공학부"
    │ ├─ summary: "딥러닝 기반 차량 인식 알고리즘을 활용한 시뮬레이션 프로젝트"
    │ ├─ createdAt: 1756863600000
