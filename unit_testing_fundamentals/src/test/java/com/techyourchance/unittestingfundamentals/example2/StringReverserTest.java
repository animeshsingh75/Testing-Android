package com.techyourchance.unittestingfundamentals.example2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringReverserTest {

    StringReverser SUT;

    @Before
    public void setUp() throws Exception {
        SUT=new StringReverser();
    }

    @Test
    public void reverse_emptyString_emptyReversedString() throws Exception{
        String result=SUT.reverse("");
        assertThat(result,is(""));
    }

    @Test
    public void reverse_singleCharacterString_sameStringReturned() throws Exception{
        String result=SUT.reverse("a");
        assertThat(result,is("a"));
    }

    @Test
    public void reverse_longString_reverseStringReturned() throws Exception{
        String str="Animesh Singh";
        String result=SUT.reverse(str);
        assertThat(result,is("hgniS hseminA"));
    }
}