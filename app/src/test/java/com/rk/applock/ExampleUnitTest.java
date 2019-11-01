package com.rk.applock;

import org.junit.Test;

import java.util.Calendar;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    int startTime = 01;
    int endTime = 05;
    private int curentHour = 01;

    @Test
    public void addition_isCorrect() throws Exception {


        String result = checkHour()? "yes" : "no";
        System.out.println("is blocking hour " + result);
    }

    private boolean checkHour() {
        if (startTime != 0 && endTime != 0) {
            if (startTime < endTime) {
                if (getCurrentHour() >= startTime && getCurrentHour() <= endTime)
                    return true;
            } else {
                if (getCurrentHour() >= startTime || getCurrentHour() <= endTime)
                    return true;
            }
        }
        return false;
    }

    private int getCurrentHour() {
        return curentHour;
    }
}