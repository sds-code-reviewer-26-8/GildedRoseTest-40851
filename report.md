## 1. 테스트 결과

### Coverage

(IntelliJ Line / Branch Coverage 캡처를 여기에 첨부)

- 목표: `GildedRose` Line Coverage 90% 이상, Branch Coverage 80% 이상

### 테스트 실행 시간

(GildedRoseTest 실행 결과 캡처를 여기에 첨부)

- 목표: `GildedRoseTest` 전체 실행 시간 3초 이내
- 테스트 총 24개 (스펙 #1~#22 + 추가 경계값 2개 + #17 분할 3개)

## 2. 테스트 보고서

### 스펙과 테스트 매핑

`src/test/java/com/gildedrose/GildedRoseTest.java`는 아이템 종류별 `@Nested` 클래스로 구성했고, 모든 테스트는 Given / When / Then 구조로 작성했습니다. 테스트 하나는 스펙 하나만 검증합니다.

#### 일반 아이템

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| #1 유통기한 전에는 품질이 1 감소한다 (10, 20 → 9, 19) | normalItem_qualityDecreasesByOne_beforeSellDate |
| #2 유통기한이 1인 경우에도 품질은 1 감소한다 (1, 20 → 0, 19) | normalItem_qualityDecreasesByOne_whenSellInIsOne |
| #3 유통기한이 0이면 품질이 2 감소한다 (0, 20 → -1, 18) | normalItem_qualityDecreasesByTwo_whenSellInIsZero |
| #4 유통기한이 지난 이후에도 품질이 2 감소한다 (-1, 20 → -2, 18) | normalItem_qualityDecreasesByTwo_afterSellDate |
| #5 품질은 0 아래로 감소하지 않는다 (5, 0 → 4, 0) | normalItem_qualityNeverNegative |
| #6 품질이 1일 때 2 감소하더라도 0이 된다 (0, 1 → -1, 0) | normalItem_qualityStopsAtZero_afterSellDate |
| (추가) 유통기한이 지나고 품질이 0이어도 음수가 되지 않는다 (-1, 0 → -2, 0) | normalItem_qualityNeverNegative_afterSellDate |

#### Aged Brie

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| #7 유통기한 전에는 품질이 1 증가한다 (5, 10 → 4, 11) | agedBrie_qualityIncreasesByOne_beforeSellDate |
| #8 유통기한이 지나면 품질이 2 증가한다 (0, 10 → -1, 12) | agedBrie_qualityIncreasesByTwo_afterSellDate |
| #9 품질은 50을 초과하지 않는다 (5, 50 → 4, 50) | agedBrie_qualityNeverExceedsFifty |
| #10 품질이 49라면 2 증가해야 해도 50까지만 증가한다 (0, 49 → -1, 50) | agedBrie_qualityCappedAtFifty_afterSellDate |
| (추가) 유통기한이 지나도 품질이 50이면 증가하지 않는다 (-1, 50 → -2, 50) | agedBrie_qualityStaysFifty_afterSellDate |

#### Backstage passes

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| #11 공연까지 11일 남으면 품질이 1 증가한다 (11, 20 → 10, 21) | passes_qualityIncreasesByOne_whenElevenDaysLeft |
| #12 10일 남으면 품질이 2 증가한다 (10, 20 → 9, 22) | passes_qualityIncreasesByTwo_whenTenDaysLeft |
| #13 6일 남아도 품질이 2 증가한다 (6, 20 → 5, 22) | passes_qualityIncreasesByTwo_whenSixDaysLeft |
| #14 5일 남으면 품질이 3 증가한다 (5, 20 → 4, 23) | passes_qualityIncreasesByThree_whenFiveDaysLeft |
| #15 1일 남아도 품질이 3 증가한다 (1, 20 → 0, 23) | passes_qualityIncreasesByThree_whenOneDayLeft |
| #16 공연이 끝나면 품질이 0이 된다 (0, 20 → -1, 0) | passes_qualityDropsToZero_afterConcert |
| #17 2 증가해도 50을 초과하지 않는다 (10, 49 → 9, 50) | passes_qualityCappedAtFifty_whenTenDaysLeft |
| #17 3 증가해도 50을 초과하지 않는다 (5, 48 → 4, 50) | passes_qualityCappedAtFifty_whenFiveDaysLeft |
| #17 5일 남고 품질 49이면 50까지만 증가한다 (5, 49 → 4, 50) | passes_qualityCappedAtFifty_whenFiveDaysLeftAndQualityFortyNine |
| #18 품질이 이미 50이면 증가하지 않는다 (5, 50 → 4, 50) | passes_qualityStaysFifty |

#### Sulfuras

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| #19 유통기한과 품질이 변하지 않는다 (10, 80 → 10, 80) | sulfuras_neverChanges |
| #20 유통기한이 지난 상태에서도 변하지 않는다 (-1, 80 → -1, 80) | sulfuras_neverChanges_afterSellDate |

#### 공통 동작과 Repository 상호작용

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| #21 여러 아이템이 있으면 각각의 규칙에 따라 갱신한다 | multipleItems_eachUpdatedByOwnRule |
| #22 갱신된 아이템을 Repository에 저장하도록 요청한다 (`verify(repository).saveAll(...)`) | updateQuality_requestsSaveAllWithUpdatedItems |

### Test Double 사용

- **어떤 의존성을 대체했나요?**
  - `ItemRepository` 인터페이스 (실제 구현체는 `InMemoryItemRepository`)

- **어떤 Test Double을 사용했나요?**
  - Mockito `@Mock`으로 만든 Mock 객체 (`@ExtendWith(MockitoExtension.class)`)
  - 스펙 검증(#1~#21): `when(repository.findAll()).thenReturn(...)` **stub**으로 테스트 데이터를 공급하고 `assertEquals`로 결과를 확인
  - 상호작용 검증(#22): stub + `verify(repository).saveAll(...)`로 갱신된 아이템의 저장을 요청했는지 확인

- **사용한 이유는 무엇인가요?**
  - `InMemoryItemRepository`는 호출마다 `Thread.sleep(DELAY_MILLIS)`로 지연이 발생하는 느린 의존성이라, 그대로 쓰면 테스트 24개의 실행 시간이 크게 늘어납니다. Mock으로 대체해 3초 이내 실행을 만족합니다.
  - 테스트마다 원하는 아이템(이름, sellIn, quality)을 자유롭게 준비할 수 있어 경계값 테스트가 간단해집니다.
  - 실제 저장소 없이도 "저장을 요청했는가"라는 협력 객체와의 상호작용을 `verify`로 검증할 수 있습니다.

### 기타 변경 사항

- `build.gradle`: JUnit 5 / Mockito 의존성 설정, `JavaCompile` 인코딩을 UTF-8로 지정 (테스트의 한글 `@DisplayName` 깨짐 방지)
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`: `mock-maker-subclass` 지정
- `src/main` 코드, `Item` 클래스, `GildedRose`의 `items` 필드는 수정하지 않았습니다.
