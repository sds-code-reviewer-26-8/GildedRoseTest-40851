package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GildedRoseTest {

    private static final String VEST = "+5 Dexterity Vest";
    private static final String BRIE = "Aged Brie";
    private static final String PASSES = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    @Mock
    private ItemRepository repository;

    private static void assertItem(Item item, int expectedSellIn, int expectedQuality) {
        assertEquals(expectedSellIn, item.sellIn, "sellIn");
        assertEquals(expectedQuality, item.quality, "quality");
    }

    @Nested
    @DisplayName("일반 아이템")
    class NormalItem {

        @Test
        @DisplayName("#1 유통기한 전에는 품질이 1 감소한다")
        void normalItem_qualityDecreasesByOne_beforeSellDate() {
            // Given
            Item item = new Item(VEST, 10, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 9, 19);
        }

        @Test
        @DisplayName("#2 유통기한이 1인 경우에도 품질은 1 감소한다")
        void normalItem_qualityDecreasesByOne_whenSellInIsOne() {
            // Given
            Item item = new Item(VEST, 1, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 0, 19);
        }

        @Test
        @DisplayName("#3 유통기한이 0이면 품질이 2 감소한다")
        void normalItem_qualityDecreasesByTwo_whenSellInIsZero() {
            // Given
            Item item = new Item(VEST, 0, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 18);
        }

        @Test
        @DisplayName("#4 유통기한이 지난 이후에도 품질이 2 감소한다")
        void normalItem_qualityDecreasesByTwo_afterSellDate() {
            // Given
            Item item = new Item(VEST, -1, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -2, 18);
        }

        @Test
        @DisplayName("#5 품질은 0 아래로 감소하지 않는다")
        void normalItem_qualityNeverNegative() {
            // Given
            Item item = new Item(VEST, 5, 0);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 0);
        }

        @Test
        @DisplayName("#6 품질이 1일 때 2 감소하더라도 0이 된다")
        void normalItem_qualityStopsAtZero_afterSellDate() {
            // Given
            Item item = new Item(VEST, 0, 1);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 0);
        }

        @Test
        @DisplayName("유통기한이 지나고 품질이 0이어도 음수가 되지 않는다")
        void normalItem_qualityNeverNegative_afterSellDate() {
            // Given
            Item item = new Item(VEST, -1, 0);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -2, 0);
        }
    }

    @Nested
    @DisplayName("Aged Brie")
    class AgedBrie {

        @Test
        @DisplayName("#7 유통기한 전에는 품질이 1 증가한다")
        void agedBrie_qualityIncreasesByOne_beforeSellDate() {
            // Given
            Item item = new Item(BRIE, 5, 10);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 11);
        }

        @Test
        @DisplayName("#8 유통기한이 지나면 품질이 2 증가한다")
        void agedBrie_qualityIncreasesByTwo_afterSellDate() {
            // Given
            Item item = new Item(BRIE, 0, 10);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 12);
        }

        @Test
        @DisplayName("#9 품질은 50을 초과하지 않는다")
        void agedBrie_qualityNeverExceedsFifty() {
            // Given
            Item item = new Item(BRIE, 5, 50);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 50);
        }

        @Test
        @DisplayName("#10 품질이 49라면 2 증가해야 해도 50까지만 증가한다")
        void agedBrie_qualityCappedAtFifty_afterSellDate() {
            // Given
            Item item = new Item(BRIE, 0, 49);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 50);
        }

        @Test
        @DisplayName("유통기한이 지나도 품질이 50이면 증가하지 않는다")
        void agedBrie_qualityStaysFifty_afterSellDate() {
            // Given
            Item item = new Item(BRIE, -1, 50);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -2, 50);
        }
    }

    @Nested
    @DisplayName("Backstage passes")
    class BackstagePasses {

        @Test
        @DisplayName("#11 공연까지 11일 이상 남으면 품질이 1 증가한다")
        void passes_qualityIncreasesByOne_whenElevenDaysLeft() {
            // Given
            Item item = new Item(PASSES, 11, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 10, 21);
        }

        @Test
        @DisplayName("#12 10일 남으면 품질이 2 증가한다")
        void passes_qualityIncreasesByTwo_whenTenDaysLeft() {
            // Given
            Item item = new Item(PASSES, 10, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 9, 22);
        }

        @Test
        @DisplayName("#13 6일 남아도 품질이 2 증가한다")
        void passes_qualityIncreasesByTwo_whenSixDaysLeft() {
            // Given
            Item item = new Item(PASSES, 6, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 5, 22);
        }

        @Test
        @DisplayName("#14 5일 남으면 품질이 3 증가한다")
        void passes_qualityIncreasesByThree_whenFiveDaysLeft() {
            // Given
            Item item = new Item(PASSES, 5, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 23);
        }

        @Test
        @DisplayName("#15 1일 남아도 품질이 3 증가한다")
        void passes_qualityIncreasesByThree_whenOneDayLeft() {
            // Given
            Item item = new Item(PASSES, 1, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 0, 23);
        }

        @Test
        @DisplayName("#16 공연이 끝나면 품질이 0이 된다")
        void passes_qualityDropsToZero_afterConcert() {
            // Given
            Item item = new Item(PASSES, 0, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 0);
        }

        @Test
        @DisplayName("#17-a 품질이 2 증가해도 50을 초과하지 않는다 (10일 남음, 품질 49)")
        void passes_qualityCappedAtFifty_whenTenDaysLeft() {
            // Given
            Item item = new Item(PASSES, 10, 49);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 9, 50);
        }

        @Test
        @DisplayName("#17-b 품질이 3 증가해도 50을 초과하지 않는다 (5일 남음, 품질 48)")
        void passes_qualityCappedAtFifty_whenFiveDaysLeft() {
            // Given
            Item item = new Item(PASSES, 5, 48);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 50);
        }

        @Test
        @DisplayName("#17-c 5일 남음, 품질 49이면 50까지만 증가한다")
        void passes_qualityCappedAtFifty_whenFiveDaysLeftAndQualityFortyNine() {
            // Given
            Item item = new Item(PASSES, 5, 49);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 50);
        }

        @Test
        @DisplayName("#18 품질이 이미 50이라면 증가하지 않는다")
        void passes_qualityStaysFifty() {
            // Given
            Item item = new Item(PASSES, 5, 50);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 4, 50);
        }
    }

    @Nested
    @DisplayName("Sulfuras")
    class Sulfuras {

        @Test
        @DisplayName("#19 유통기한과 품질이 변하지 않는다")
        void sulfuras_neverChanges() {
            // Given
            Item item = new Item(SULFURAS, 10, 80);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, 10, 80);
        }

        @Test
        @DisplayName("#20 유통기한이 지난 상태에서도 변하지 않는다")
        void sulfuras_neverChanges_afterSellDate() {
            // Given
            Item item = new Item(SULFURAS, -1, 80);
            when(repository.findAll()).thenReturn(Arrays.asList(item));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(item, -1, 80);
        }
    }

    @Nested
    @DisplayName("공통 동작과 Repository 상호작용")
    class Common {

        @Test
        @DisplayName("#21 여러 아이템이 있으면 각각의 규칙에 따라 갱신한다")
        void multipleItems_eachUpdatedByOwnRule() {
            // Given
            Item vest = new Item(VEST, 10, 20);
            Item brie = new Item(BRIE, 5, 10);
            when(repository.findAll()).thenReturn(Arrays.asList(vest, brie));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            assertItem(vest, 9, 19);
            assertItem(brie, 4, 11);
        }

        @Test
        @DisplayName("#22 갱신된 아이템을 Repository에 저장하도록 요청한다")
        void updateQuality_requestsSaveAllWithUpdatedItems() {
            // Given
            Item vest = new Item(VEST, 10, 20);
            when(repository.findAll()).thenReturn(Arrays.asList(vest));

            // When
            new GildedRose(repository).updateQuality();

            // Then
            verify(repository).saveAll(Arrays.asList(vest));
        }
    }
}
