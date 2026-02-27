# 작업 목록 및 검증 목록

## phase 1. 기초 인프라 및 UI
- [x] MainActivity.kt 및 기초 UI 구현
- [x] [검증] 앱 실행 시 MainActivity가 정상적으로 표시되는지 확인
- [x] MainActivity.kt 및 기초 UI 구현
- [x] Room Persistence Library 추가 (로컬 저장소 구축)
- [x] NoteEntity 및 DAO 구현
- [x] AddNoteActivity 생성 (메모 작성/편집 UI)
- [x] MainActivity <-> AddNoteActivity 연동 (결과 처리)
- [x] 메모 삭제 기능 추가 (리스트 롱클릭 등)
- [x] 더미 GitHub 동기화 UI 구현 (Syncing 상태 표시)
- [x] 다크 모드(Dark Mode) 테마 최적화 및 컬러셋 보강
- [x] 이미지 첨부 기능 구현 (갤러리 선택 및 DB 저장)
- [x] 마크다운(Markdown) 문서 형식 지원 및 렌더링 적용

## phase 2. 로컬 저장소 (Room)
- [x] Room Persistence Library 추가 및 환경 설정
- [x] [검증] 프로젝트 빌드 및 Room 관련 클래스 생성 확인
- [x] NoteEntity 및 DAO 구현
- [x] [검증] NoteDao 단위 테스트 작성 및 실행 (Insert, Read, Delete)

## phase 3. 메모 작성 및 편집 기능
- [x] AddNoteActivity 생성 및 레이아웃 구현
- [x] [검증] FAB 클릭 시 AddNoteActivity로 이동 확인
- [x] MainActivity <-> AddNoteActivity 데이터 연동
- [x] [검증] 메모 작성 후 저장 시 MainActivity 리스트에 반영되는지 확인 (MainActivity 기본 동작 검증)
- [x] 메모 편집 기능 구현
- [x] [검증] 기존 메모 클릭 시 편집 화면에 데이터가 로드되고 수정되는지 확인 (AddNoteActivity 데이터 로드 검증)

## phase 4. 메모 관리 기능
- [x] 메모 삭제 기능 추가 (롱클릭 다이얼로그)
- [x] [검증] 메모 삭제 시 DB 및 UI에서 제거되는지 확인 (NoteDao 삭제 테스트 완료)
- [x] 메모 검색 기능 구현 (SearchView)
- [x] [검증] 검색어 입력 시 해당 제목/내용을 포함한 메모만 필터링되는지 확인 (NoteDao 검색 테스트 완료)

## phase 5. UI/UX 고도화
- [x] 더미 GitHub 동기화 UI 구현
- [x] [검증] 데이터 로드 시 "Syncing..." 레이아웃이 표시되었다가 사라지는지 확인
- [x] 다크 모드 테마 최적화
- [x] [검증] 시스템 다크 모드 전환 시 배경 및 텍스트 색상이 적절히 변하는지 확인

## phase 6. 기능 고도화 1차
- [x] 앱 최초 샘플 데이터 12건을 추가
- [x] 설정 페이지 추가
- [x] 설정 페이지에서 github 연동 설정 기능 추가 (실제 api 연결은 하지 않음, 추후 구현)
- [x] 설정 페이지에서 다크모드 설정 내용 추가
- [ ] 목록 페이지 > 타이틀, 미리보기 3줄, 날짜 시간 표시
- [ ] 목록 페이지 > 날짜순, 제목순 정렬 (내림차순, 오름차순)
- [ ] 수정 페이지 > 취소, 저장 버튼 추가
- [ ] 신규 생성 페이지 > 제목 칸 > 힌트 > 제목 없음


