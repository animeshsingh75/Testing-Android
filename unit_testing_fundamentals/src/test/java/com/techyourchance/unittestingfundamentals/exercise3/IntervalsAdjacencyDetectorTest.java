package com.techyourchance.unittestingfundamentals.exercise3;

import static org.hamcrest.CoreMatchers.is;

import com.techyourchance.unittestingfundamentals.example3.Interval;
import com.techyourchance.unittestingfundamentals.example3.IntervalsOverlapDetectorTest;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector intervalsAdjacencyDetector;
    @Before
    public void setUp() throws Exception {
        intervalsAdjacencyDetector=new IntervalsAdjacencyDetector();
    }
    Interval interval1;
    Interval interval2;
    //Interval1 overlap at left
    //Interval1 overlap at right
    //Interval1 inside
    //Interval2 inside
    //Interval1 before
    //Interval2 after
    //Interval1 adjacent
    //Interval2 adjacent
    @Test
    public void intervalAdjacent_interval1Start_falseReturned() {
        interval1=new Interval(5,7);
        interval2=new Interval(6,9);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }
    @Test
    public void intervalAdjacent_interval1End_falseReturned() {
        interval1=new Interval(5,7);
        interval2=new Interval(2,8);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }

    @Test
    public void intervalAdjacent_interval1Inside_falseReturned() {
        interval1=new Interval(6,9);
        interval2=new Interval(0,10);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }
    @Test
    public void intervalAdjacent_interval2Inside_falseReturned() {
        interval1=new Interval(5,10);
        interval2=new Interval(6,9);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }
    @Test
    public void intervalAdjacent_interval1Before_falseReturned() {
        interval1=new Interval(0,3);
        interval2=new Interval(6,7);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }
    @Test
    public void intervalAdjacent_interval1After_falseReturned() {
        interval1=new Interval(5,7);
        interval2=new Interval(0,3);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }

    @Test
    public void intervalAdjacent_interval1Adjacent_trueReturned() {
        interval1=new Interval(0,2);
        interval2=new Interval(2,8);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(true));
    }
    @Test
    public void intervalAdjacent_interval2Adjacent_trueReturned() {
        interval1=new Interval(5,7);
        interval2=new Interval(7,9);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(true));
    }

    @Test
    public void intervalAdjacent_intervalSame_falseReturned() {
        interval1=new Interval(5,7);
        interval2=new Interval(5,7);
        boolean result=intervalsAdjacencyDetector.isAdjacent(interval1,interval2);
        MatcherAssert.assertThat(result,is(false));
    }
}