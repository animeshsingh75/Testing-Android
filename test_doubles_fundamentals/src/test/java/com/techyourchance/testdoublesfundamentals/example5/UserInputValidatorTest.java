package com.techyourchance.testdoublesfundamentals.example5;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserInputValidatorTest {

    UserInputValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT=new UserInputValidator();
    }

    @Test
    public void isValidFullName_validFullName_trueReturned() throws Exception {
        boolean result = SUT.isValidFullName("validFullName");
        assertThat(result, is(true));
    }

    @Test
    public void isValidFullName_invalidFullName_falseReturned() throws Exception {
        boolean result = SUT.isValidFullName("");
        assertThat(result, is(false));
    }
    @Test
    public void isValidFullName_empty_falseReturned() {
        boolean result=SUT.isValidFullName("");
        MatcherAssert.assertThat(result, is(false));
    }

    @Test
    public void isValidUserName_validUserName_trueReturned() {
        boolean result=SUT.isValidUsername("Animesh Singh");
        MatcherAssert.assertThat(result, is(true));
    }

    @Test
    public void isValidUserName_empty_falseReturned() {
        boolean result=SUT.isValidUsername("");
        MatcherAssert.assertThat(result, is(false));
    }
}