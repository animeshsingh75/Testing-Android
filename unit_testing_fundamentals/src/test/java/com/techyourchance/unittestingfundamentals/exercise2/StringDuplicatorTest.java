package com.techyourchance.unittestingfundamentals.exercise2;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

public class StringDuplicatorTest {

    StringDuplicator SUT;
    @Before
    public void setUp() throws Exception {
        SUT=new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String result=SUT.duplicate("");
        MatcherAssert.assertThat(result,is(""));
    }

    @Test
    public void duplicate_singleCharString_sameStringReturned() {
        String result=SUT.duplicate("a");
        MatcherAssert.assertThat(result,is("a"));
    }

    @Test
    public void duplicate_longString_sameStringReturned() {
        String result=SUT.duplicate("Animesh Singh");
        MatcherAssert.assertThat(result,is("Animesh Singh"));
    }
}