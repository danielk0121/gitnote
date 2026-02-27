# 작업 및 검증 목록

## 1. 기초 인프라 및 UI
- [x] MainActivity.kt 및 기초 UI 구현
- [x] [검증] 앱 실행 시 MainActivity가 정상적으로 표시되는지 확인

## 2. 로컬 저장소 (Room)
- [x] Room Persistence Library 추가 및 환경 설정
- [x] [검증] 프로젝트 빌드 및 Room 관련 클래스 생성 확인
- [x] NoteEntity 및 DAO 구현
- [x] [검증] NoteDao 단위 테스트 작성 및 실행 (Insert, Read, Delete)

## 3. 메모 작성 및 편집 기능
- [x] AddNoteActivity 생성 및 레이아웃 구현
- [x] [검증] FAB 클릭 시 AddNoteActivity로 이동 확인
- [x] MainActivity <-> AddNoteActivity 데이터 연동
- [ ] [검증] 메모 작성 후 저장 시 MainActivity 리스트에 반영되는지 확인
- [x] 메모 편집 기능 구현
- [ ] [검증] 기존 메모 클릭 시 편집 화면에 데이터가 로드되고 수정되는지 확인

## 4. 메모 관리 기능
- [x] 메모 삭제 기능 추가 (롱클릭 다이얼로그)
- [ ] [검증] 메모 삭제 시 DB 및 UI에서 제거되는지 확인
- [ ] 메모 검색 기능 구현 (SearchView)
- [ ] [검증] 검색어 입력 시 해당 제목/내용을 포함한 메모만 필터링되는지 확인

## 5. UI/UX 고도화
- [x] 더미 GitHub 동기화 UI 구현
- [ ] [검증] 데이터 로드 시 "Syncing..." 레이아웃이 표시되었다가 사라지는지 확인
- [x] 다크 모드 테마 최적화
- [ ] [검증] 시스템 다크 모드 전환 시 배경 및 텍스트 색상이 적절히 변하는지 확인
