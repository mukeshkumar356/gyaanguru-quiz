package com.gyaanguru.quiz;

import org.junit.Test;
import static org.junit.Assert.*;

public class QuizGradeCalculatorTest {

    @Test
    public void calculatePercentage_wholeNumberScore() {
        assertEquals(50, QuizGradeCalculator.calculatePercentage(5, 10));
        assertEquals(100, QuizGradeCalculator.calculatePercentage(10, 10));
        assertEquals(0, QuizGradeCalculator.calculatePercentage(0, 10));
    }

    @Test
    public void calculatePercentage_totalZeroOrNegative_returnsZeroInsteadOfCrashing() {
        assertEquals(0, QuizGradeCalculator.calculatePercentage(0, 0));
        assertEquals(0, QuizGradeCalculator.calculatePercentage(5, 0));
        assertEquals(0, QuizGradeCalculator.calculatePercentage(5, -1));
    }

    @Test
    public void gradeFor_boundaryAt90IsTopGrade() {
        assertEquals("शानदार!", QuizGradeCalculator.gradeFor(90).label);
        assertEquals("शानदार!", QuizGradeCalculator.gradeFor(100).label);
        assertEquals("बहुत अच्छा!", QuizGradeCalculator.gradeFor(89).label);
    }

    @Test
    public void gradeFor_boundaryAt70() {
        assertEquals("बहुत अच्छा!", QuizGradeCalculator.gradeFor(70).label);
        assertEquals("अच्छा!", QuizGradeCalculator.gradeFor(69).label);
    }

    @Test
    public void gradeFor_boundaryAt50() {
        assertEquals("अच्छा!", QuizGradeCalculator.gradeFor(50).label);
        assertEquals("और पढ़ें!", QuizGradeCalculator.gradeFor(49).label);
    }

    @Test
    public void gradeFor_zeroPercent_isLowestGrade() {
        assertEquals("और पढ़ें!", QuizGradeCalculator.gradeFor(0).label);
        assertEquals("#F44336", QuizGradeCalculator.gradeFor(0).colorHex);
    }
}
