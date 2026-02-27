package dev.danielk.gitnote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.danielk.gitnote.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Database(entities = [Note::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gitnote_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                
                // 앱 최초 실행 또는 데이터가 없을 때 샘플 데이터 삽입
                prepopulateData(context.applicationContext, instance)
                
                instance
            }
        }

        private fun prepopulateData(context: Context, database: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                val noteDao = database.noteDao()
                if (noteDao.countNotes() == 0) {
                    val now = System.currentTimeMillis()
                    val sampleNotes = listOf(
                        Note(title = "GitNote에 오신 것을 환영합니다", content = "GitNote를 이용해 주셔서 감사합니다! 이 앱은 마크다운(Markdown)을 지원하며 깃허브(GitHub)와 동기화할 수 있는 안드로이드 메모장입니다.", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "마크다운 문법 기초", content = "# 제목 1\n## 제목 2\n### 제목 3\n\n**굵은 글씨**와 *기울임꼴*을 사용할 수 있습니다.\n\n- 목록 아이템 1\n- 목록 아이템 2\n\n1. 순서 있는 목록 1\n2. 순서 있는 목록 2", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "할 일 체크리스트", content = "- [ ] 프로젝트 마무리하기\n- [x] 데이터베이스 구축 완료\n- [ ] 동기화 기능 추가", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "주간 회의록", content = "## 주간 싱크 미팅\n\n- 신규 기능 논의\n- PR 리뷰 일정 확인\n- 다음 스프린트 계획 수립", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "장보기 목록", content = "- 우유\n- 달걀\n- 빵\n- 커피 원두", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "코드 스니펫 예제", content = "```kotlin\nfun main() {\n    println(\"안녕하세요, GitNote!\")\n}\n```", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "새로운 프로젝트 아이디어", content = "1. AI 비서 통합\n2. 음성 메모 기능\n3. 클라우드 백업 자동화", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "추천 도서 목록", content = "- 클린 코드 (Clean Code)\n- 실용주의 프로그래머\n- 알고리즘 문제 해결 전략", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "일본 여행 계획", content = "### 도쿄/교토 여행\n- 도쿄 타워 방문\n- 교토 금각사 투어\n- 오사카 도톤보리 맛집 탐방", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "오늘의 일기", content = "오늘은 데이터베이스를 구현하고 샘플 데이터를 추가했습니다. 생산적인 하루였습니다.", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "팬케이크 레시피", content = "## 폭신한 팬케이크 만들기\n\n1. 밀가루, 우유, 달걀을 섞습니다.\n2. 팬을 달구고 반죽을 올립니다.\n3. 시럽과 함께 맛있게 즐깁니다.", fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now),
                        Note(title = "마크다운 상세 가이드", content = """
# 마크다운(Markdown) 완벽 가이드

이 문서는 GitNote에서 지원하는 마크다운 문법을 상세히 설명합니다. 50라인 이상의 예시를 포함하고 있습니다.

## 1. 헤더 (Headers)
# H1
## H2
### H3
#### H4
##### H5
###### H6

## 2. 강조 (Emphasis)
- **굵게**: `**텍스트**` 또는 `__텍스트__`
- *기울임*: `*텍스트*` 또는 `_텍스트_`
- ~~취소선~~: `~~텍스트~~`

## 3. 목록 (Lists)
### 순서 없는 목록
- 아이템 1
  - 서브 아이템 1.1
  - 서브 아이템 1.2
- 아이템 2

### 순서 있는 목록
1. 첫 번째
2. 두 번째
3. 세 번째

## 4. 링크와 이미지
- [Google 바로가기](https://google.com)
- 이미지 삽입: `![설명](이미지주소)`

## 5. 코드 (Code)
인라인 코드는 `println()` 처럼 사용합니다.

코드 블록:
```kotlin
fun hello() {
    val message = "Hello Markdown"
    println(message)
}
```

## 6. 인용구 (Blockquotes)
> 마크다운은 텍스트 기반의 마크업 언어로,
> 읽기 쉽고 쓰기 쉬운 것이 특징입니다.

## 7. 가로선 (Horizontal Rules)
---

## 8. 체크박스 (Task Lists)
- [x] 완료된 작업
- [ ] 진행 중인 작업

## 9. 추가 내용 (50라인 채우기용)
마크다운은 2004년 존 그루버(John Gruber)에 의해 만들어졌습니다.
웹상에서 글을 쓰는 사람들에게 HTML보다 쉬운 대안을 제공하기 위해 설계되었습니다.
현재는 GitHub, Stack Overflow, VS Code 등 수많은 플랫폼에서 표준으로 사용됩니다.

이 메모장은 Android 환경에서 Markwon 라이브러리를 사용하여 렌더링합니다.
Material Design 3 가이드를 준수하며 다크 모드에서도 가독성이 뛰어납니다.

작성된 메모는 내부 Room DB에 저장되며,
향후 GitHub 동기화 기능을 통해 여러 기기에서 공유할 수 있습니다.

### 상세 설명 계속...
라인 40을 넘어가고 있습니다.
마크다운의 장점은 다음과 같습니다:
1. 배우기 쉽다.
2. 어떤 텍스트 에디터에서도 열 수 있다.
3. 다양한 포맷(HTML, PDF, DOCX)으로 변환이 용이하다.
4. 소스 코드 관리가 쉽다.

마지막으로, GitNote를 사용해 주셔서 다시 한번 감사드립니다.
즐거운 메모 작성 되세요!
                                    """.trimIndent(), fileName = "${UUID.randomUUID()}.md", createdAt = now, updatedAt = now)
                    )
                    noteDao.insertAll(sampleNotes)

                    // 각 샘플 메모에 대해 파일 생성
                    for (note in sampleNotes) {
                        createFileForNote(context, note)
                    }
                }
            }
        }

        private fun createFileForNote(context: Context, note: Note) {
            val header = """
                ---
                title: "${note.title.replace("\"", "\\\"")}"
                created_at: ${dateFormat.format(Date(note.createdAt))}
                updated_at: ${dateFormat.format(Date(note.updatedAt))}
                ---
                
            """.trimIndent()
            
            try {
                val file = File(context.filesDir, note.fileName)
                val outputStream = FileOutputStream(file)
                outputStream.write((header + note.content).toByteArray())
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
