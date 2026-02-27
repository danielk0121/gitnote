# 작업 일지

2026-02-27 15:40, 앱의 기초 구조를 설계하고 `MainActivity` 생성을 위한 준비를 시작합니다.
- [x] todo_task.md 초기화

2026-02-27 15:55, MainActivity 생성 및 AndroidManifest.xml 등록 완료.
- [x] MainActivity.kt 파일 생성
- [x] res/layout/activity_main.xml 레이아웃 파일 생성
- [x] AndroidManifest.xml에 MainActivity 등록

2026-02-27 16:15, 기초 메모 리스트 UI 및 더미 데이터 연동 완료.
- [x] recyclerview, constraintlayout 종속성 추가
- [x] Note 모델 및 NoteAdapter 구현
- [x] item_note.xml 레이아웃 생성
- [x] MainActivity에서 RecyclerView 초기화 및 더미 데이터 연동

2026-02-27 16:30, 기능 중심 개발로 방향 전환.
- [x] GitHub API 연동을 더미(Mock)로 대체 결정
- [x] 다크 모드 및 CRUD 강화 계획 수립
- [x] requirements.md 및 todo_task.md 업데이트

2026-02-27 17:00, 메모 CRUD 및 로컬 DB, 다크 모드, 더미 동기화 구현 완료.
- [x] Room DB 연동 및 NoteEntity/DAO 정의
- [x] AddNoteActivity 구현 및 메모 작성/편집 기능 추가
- [x] 메모 롱클릭 삭제 기능 및 다이얼로그 추가
- [x] 다크 모드 대응 및 테마 최적화
- [x] 더미 GitHub 동기화 UI 및 애니메이션 처리

2026-02-27 17:35, Git 초기화 및 1작업 1커밋 원칙 적용.
- [x] Git 저장소 초기화 및 소급 커밋 완료
- [x] todo_test_task.md 기반 검증 작업 진행 중
- [x] NoteDao 단위 테스트 작성 및 KSP 플러그인 이슈 해결

2026-02-27 18:10, 메모 검색 기능 구현 및 검증 완료.
- [x] SearchView 연동 및 NoteDao 검색 쿼리 구현
- [x] NoteDaoTest 내 검색 테스트 케이스 추가
- [x] 빌드 성공 확인 및 개별 커밋 완료

2026-02-27 18:30, 다크 모드 동기화 UI 최적화 및 전체 검증 완료.
- [x] sync_background 색상 추가 (values, values-night)
- [x] activity_main.xml 내 syncLayout 배경색 테마 대응
- [x] "Syncing..." 노출 로직 및 다크 모드 테마 일관성 검증
- [x] requirements.md 및 todo_test_task.md 업데이트 완료

---
