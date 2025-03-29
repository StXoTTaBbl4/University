package org.xoeqvdp.tests;

import org.junit.Test;
import org.xoeqvdp.MergeSort;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MergeSortTests {
    @Test
    public void testMergeSortCallSequence() {
        int[] input = {38, 27, 43, 3, 9, 82, 10};
        MergeSort.callSequence.clear();

        MergeSort.mergeSort(input, 0, input.length-1);

        List<String> expectedSequence = List.of(
                "Split: 0-3 and 4-6",      // Разбиение массива 0-6 на две части: 0-3 и 4-6
                "Split: 0-1 and 2-3",               // Разбиение 0-3 на 0-1 и 2-3
                "Split: 0-0 and 1-1",               // Разбиение 0-1 на 0-0 и 1-1 (размер 1 => рекурсия всё)
                "Merge: 0-0 with 1-1",              // Обратное слияние 0-0 и 1-1
                "Split: 2-2 and 3-3",               // Разбиение 2-3 на 2-2 и 3-3
                "Merge: 2-2 with 3-3",              // Обратное слияние 2-2 и 3-3
                "Merge: 0-1 with 2-3",              // Обратное слияние 0-1 и 2-3
                "Split: 4-5 and 6-6",               // Разбиение 4-6 на 4-5 и 6-6
                "Split: 4-4 and 5-5",               // Разбиение 4-5 на 4-4 и 5-5
                "Merge: 4-4 with 5-5",              // Обратное слияние 4-4 и 5-5
                "Merge: 4-5 with 6-6",              // Обратное слияние 4-5 и 6-6
                "Merge: 0-3 with 4-6"               // Обратное слияние 0-3 и 4-6
        );
        assertEquals(expectedSequence, MergeSort.callSequence);
    }

    @Test
    public void testSortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        MergeSort.mergeSort(input, 0, input.length - 1);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testReverseSortedArray() {
        int[] input = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        MergeSort.mergeSort(input, 0, input.length - 1);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testUnsortedArray() {
        int[] input = {38, 27, 43, 3, 9, 82, 10};
        int[] expected = {3, 9, 10, 27, 38, 43, 82};
        MergeSort.mergeSort(input, 0, input.length - 1);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSingleElementArray() {
        int[] input = {42};
        int[] expected = {42};
        MergeSort.mergeSort(input, 0, input.length - 1);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testEmptyArray() {
        int[] input = {};
        int[] expected = {};
        MergeSort.mergeSort(input, 0, input.length - 1);
        assertArrayEquals(expected, input);
    }
}
