package com.waes.palazares.kalah.utils;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class OffsetsTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerWhenLeftIsNull() {
        Offsets.getOffsetsMessage(null, new byte[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationWhenRightIsEmpty() {
        Offsets.getOffsetsMessage(new byte[0], new byte[1]);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerWhenRightIsNull() {
        Offsets.getOffsetsMessage(new byte[1], null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationWhenLeftIsEmpty() {
        Offsets.getOffsetsMessage(new byte[1], new byte[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationWhenDifferentSize() {
        Offsets.getOffsetsMessage(new byte[1], new byte[2]);
    }

    @Test
    @Parameters(method = "parametersToTestOffsets")
    public void shouldReturnResultWhenArraysAreSpecified(String left, String right, String result) {
        //given
        byte[] leftContent = left.getBytes();
        byte[] rightContent = right.getBytes();
        //when
        String offsetsMessage = Offsets.getOffsetsMessage(leftContent, rightContent);
        //then
        assertEquals(result, offsetsMessage);
    }

    private Object[] parametersToTestOffsets() {
        return new Object[]{
                new Object[]{"equals", "equals", "Arrays are equals"},
                new Object[]{"12as34as56", "12er34er56", "Offsets [(index,length),..] : [(2, 2),(6, 2)]"},
                new Object[]{"12as34as56as", "12er34er56er", "Offsets [(index,length),..] : [(2, 2),(6, 2),(10, 2)]"},
                new Object[]{"e1", "s1", "Offsets [(index,length),..] : [(0, 1)]"},
                new Object[]{"e1e", "s1s", "Offsets [(index,length),..] : [(0, 1),(2, 1)]"},
                new Object[]{"1e", "1s", "Offsets [(index,length),..] : [(1, 1)]"},
                new Object[]{"1ee", "1ss", "Offsets [(index,length),..] : [(1, 2)]"},
                new Object[]{"123456", "abcdef", "Offsets [(index,length),..] : [(0, 6)]"},
                new Object[]{"12asas56", "12erer56", "Offsets [(index,length),..] : [(2, 4)]"}
        };
    }
}