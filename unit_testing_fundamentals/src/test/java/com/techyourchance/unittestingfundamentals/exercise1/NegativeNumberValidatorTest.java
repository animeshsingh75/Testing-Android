package com.techyourchance.unittestingfundamentals.exercise1;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

public class NegativeNumberValidatorTest {

    NegativeNumberValidator SUT;
    @Before
    public void setup(){
        SUT=new NegativeNumberValidator();
    }

    @Test
    public void test1(){
        boolean result=SUT.isNegative(-6);
        MatcherAssert.assertThat(result,is(true));
    }

    @Test
    public void test2(){
        boolean result=SUT.isNegative(0);
        MatcherAssert.assertThat(result,is(false));
    }

    @Test
    public void test3(){
        boolean result=SUT.isNegative(6);
        MatcherAssert.assertThat(result,is(false));
    }
}