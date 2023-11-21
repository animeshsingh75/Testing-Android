package com.techyourchance.testdoublesfundamentals.example6;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FitnessTrackerTest {

    FitnessTracker SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FitnessTracker();
    }

    @Test
    public void step_totalIncremented() {
        SUT.step();
        int step = SUT.getTotalSteps();
        MatcherAssert.assertThat(step, is(1));
    }

    @Test
    public void runStep_totalIncrementedByCorrectRatio() {
        SUT.runStep();
        int step = SUT.getTotalSteps();
        MatcherAssert.assertThat(step, is(2));
    }
}