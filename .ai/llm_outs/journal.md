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

2026-02-27 19:25, 마크다운 지원 및 이미지 첨부 기능 구현 완료.
- [x] Markwon 라이브러리 추가 및 어댑터 내 마크다운 렌더링 적용
- [x] Coil 라이브러리 추가 및 갤러리 이미지 선택/표시 기능 구현
- [x] Note 모델에 imageUri 필드 추가 및 Room DB 버전 업그레이드 (v2)
- [x] AddNoteActivity 및 item_note 레이아웃에 이미지 영역 추가
- [x] 프로젝트 빌드 성공 확인 및 기능 연동 완료

2026-02-28 10:15, 기능 고도화 1차: 샘플 데이터 및 설정 페이지 구현 완료.
- [x] 앱 최초 실행 시 12건의 샘플 데이터(마크다운 포함) 자동 생성 로직 추가
- [x] SettingsActivity 구현: GitHub 동기화 토글 및 다크 모드(시스템/라이트/다크) 설정 기능 추가
- [x] SharedPreferences를 사용한 설정 값 유지 및 MainActivity 동기화 로직 연동

2026-02-28 11:00, 기능 고도화 1차: 목록 디자인 개편 및 읽기 전용 뷰어 구현 완료.
- [x] MainActivity 상단 툴바 디자인 개편 (앱 이름, 설정, 정렬, 검색 버튼 배치)
- [x] ViewNoteActivity 구현: 마크다운 렌더링을 지원하는 읽기 전용 모드 및 편집 모드 전환 기능
- [x] 메모 목록 아이템 레이아웃 업데이트: 3줄 미리보기 및 마지막 수정 시간 표시 추가
- [x] 메모 정렬 기능 구현: 날짜순(최신/과거), 제목순(오름/내림차순) 정렬 다이얼로그 연동
- [x] 신규 메모 작성 시 제목 힌트 수정 및 빈 제목 처리 로직 강화
- [x] NoteDaoTest에 insertAll 테스트 케이스 추가 및 검증 완료

2026-02-28 11:30, 버그 수정 및 UX 개선: 툴바 타이틀 겹침 이슈 해결.
- [x] `themes.xml` (Light/Night)에서 `NoActionBar` 테마를 적용하여 시스템 액션바 제거
- [x] MainActivity 상단 툴바 내부에 "gitnote" 텍스트 뷰 배치하여 버튼과의 겹침 해결
- [x] AddNoteActivity 및 ViewNoteActivity에서 `app:title`을 제거하여 버튼 가독성 확보
- [x] SettingsActivity에 뒤로가기 버튼이 포함된 툴바 추가 및 일관된 디자인 적용
- [x] 모든 화면에서 상단 버튼 클릭이 원활하도록 레이아웃 최적화 및 겹침 현상 방지

2026-02-28 12:00, 버그 수정 및 UX 개선: 상태바 가림 현상 해결 및 버튼 크기 확대.
- [x] MainActivity, AddNoteActivity, ViewNoteActivity, SettingsActivity 모든 레이아웃에 `android:fitsSystemWindows="true"` 적용하여 상태바(Notification Bar)에 의한 가림 현상 해결
- [x] AddNoteActivity (작성/수정) 화면의 취소(Cancel) 및 저장(Save) 버튼 크기 확대 및 스타일 변경 (`TonalButton` 적용, `minWidth` 설정)
- [x] 상단 툴바 아이콘 및 버튼 터치 영역 확보

2026-02-28 12:30, 버그 수정 및 UX 개선: 상단 여백 수정 및 저장 버튼 디자인 최적화.
- [x] 모든 화면의 `AppBarLayout`에서 중복 적용된 `fitsSystemWindows="true"` 설정을 제거하여 상단에 불필요한 이중 여백이 생기는 버그 수정
- [x] 수정 화면의 "Save" 버튼을 `TonalButton`에서 `TextButton`으로 변경하고 텍스트를 강조하여 사용자 의도에 맞는 디자인으로 개선

2026-02-28 13:00, 기능 개선: 다중 이미지 마크다운 지원 구현 완료.
- [x] Note 엔티티에서 단일 `imageUri` 필드를 제거하고 본문 마크다운 기반 이미지 관리로 구조 개선
- [x] 갤러리에서 선택한 이미지를 앱 내부 저장소(`filesDir`)로 자동 복사하여 안정적인 파일 경로 확보
- [x] 이미지 추가 시 본문의 마지막 커서 위치에 마크다운 문법(`![image](path)`)을 자동 삽입하도록 구현
- [x] `Markwon` 라이브러리를 통해 본문 내 모든 삽입 이미지가 읽기 화면에서 정상 렌더링되도록 일원화
- [x] 모든 레이아웃 및 어댑터에서 단일 이미지 표시용 `ImageView` 제거 및 목록 디자인 간소화
- [x] 데이터베이스 버전을 3으로 업그레이드하고 엔티티 변경 사항 반영 및 테스트 케이스 업데이트

2026-02-28 13:30, 기능 고도화 1차: 샘플 데이터 보강 완료.
- [x] 샘플 데이터를 한글로 수정하고 마크다운 문법 설명 내용을 추가
- [x] 50라인 이상의 상세 마크다운 가이드 메모를 샘플 데이터에 포함하여 로드 시 자동 생성되도록 구현
- [x] 앱 최초 실행 또는 DB 초기화 시 개선된 샘플 데이터가 반영되도록 AppDatabase 수정

2026-02-28 16:10 +09:00, 커밋 작성자 정보 수정 및 작업 지침 재확인.
- [x] PROMPT.md 지침에 따라 커밋 작성자를 'Gemini CLI <gemini-cli@example.com>'으로 수정 (--amend)
- [x] 작업 완료 알림 스크립트 실행 및 추가 질문 금지 지침 확인

