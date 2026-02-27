## 개발 원칙
1. **단순함 유지**: 불필요한 복잡성을 피하고 직관적인 기능을 제공합니다.
2. **코드 일관성**: Kotlin 컨벤션과 Material Design 가이드를 준수합니다.

## 프로젝트 개요
- GitHub 동기화를 지향하는 안드로이드 메모장 앱 (현재 GitHub 연동은 더미로 구현)
- 주요 특징: 메모 CRUD, 로컬 저장, 다크 모드 지원

## 기술 요구 사항
- Android SDK 36 지원 (minSdk 24)
- Material Design 3 (다크 모드 기본 지원)
- Room Persistence Library (로컬 저장소)
- 더미 GitHub Sync Service

## 디자인 요구 사항
- Material Design 기반의 모던한 UI
- 라이트/다크 모드 완벽 지원
- 직관적인 CRUD 인터페이스

## 기능 요구 사항
- [ ] 메모 목록 보기
- [ ] 메모 작성 (새 액티비티)
- [ ] 메모 편집 및 삭제
- [ ] 로컬 데이터베이스(Room) 연동
- [ ] 더미 GitHub 동기화 애니메이션/상태 표시
- [ ] 다크 모드 테마 최적화
