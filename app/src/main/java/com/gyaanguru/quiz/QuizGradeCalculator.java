package com.gyaanguru.quiz;

/**
 * Pure scoring/grading logic for the Result screen, pulled out of
 * ResultActivity so it can be unit-tested without any Android dependency.
 */
public final class QuizGradeCalculator {

    private QuizGradeCalculator() {}

    public static final class Grade {
        public final String label;
        public final String colorHex;

        public Grade(String label, String colorHex) {
            this.label = label;
            this.colorHex = colorHex;
        }
    }

    /**
     * Percentage of correct answers, 0-100. Guards against total <= 0
     * (e.g. a malformed intent extra) which would otherwise throw an
     * ArithmeticException on divide-by-zero.
     */
    public static int calculatePercentage(int score, int total) {
        if (total <= 0) return 0;
        return (score * 100) / total;
    }

    public static Grade gradeFor(int pct) {
        if (pct >= 90) return new Grade("शानदार!", "#4CAF50");
        if (pct >= 70) return new Grade("बहुत अच्छा!", "#1E88E5");
        if (pct >= 50) return new Grade("अच्छा!", "#FF9800");
        return new Grade("और पढ़ें!", "#F44336");
    }
}
