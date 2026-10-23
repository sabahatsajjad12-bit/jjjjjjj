package com.example.calculator

import com.example.model.FormValidationErrors
import com.example.model.PerformanceCategory
import com.example.model.PerformanceFormState
import com.example.model.PerformanceResult
import com.example.model.ScoreBreakdown
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Core business logic and calculations for the Student Study Performance Calculator.
 * Implements the weighted formula:
 * Performance Score = (Average Marks × 0.40) + (Attendance × 0.20) + (Assignment Score × 0.20) + (Quiz Score × 0.20)
 */
object PerformanceCalculator {

    const val MARKS_WEIGHT = 0.40
    const val ATTENDANCE_WEIGHT = 0.20
    const val ASSIGNMENT_WEIGHT = 0.20
    const val QUIZ_WEIGHT = 0.20

    /**
     * Validates input values for student name and numeric percentages between 0 and 100.
     */
    fun validate(state: PerformanceFormState): FormValidationErrors {
        val nameError = when {
            state.studentName.trim().isEmpty() -> "Student name cannot be empty."
            state.studentName.trim().length > 80 -> "Student name is too long."
            else -> null
        }

        val marksError = validatePercentageField(state.averageMarksText, "Average Marks")
        val attendanceError = validatePercentageField(state.attendanceText, "Attendance")
        val assignmentError = validatePercentageField(state.assignmentScoreText, "Assignment Score")
        val quizError = validatePercentageField(state.quizScoreText, "Quiz Score")

        return FormValidationErrors(
            studentNameError = nameError,
            averageMarksError = marksError,
            attendanceError = attendanceError,
            assignmentScoreError = assignmentError,
            quizScoreError = quizError
        )
    }

    /**
     * Validates an individual percentage field.
     * Must not be empty, must be numeric, and must be between 0 and 100.
     */
    private fun validatePercentageField(rawText: String, fieldName: String): String? {
        val text = rawText.trim()
        if (text.isEmpty()) {
            return "$fieldName cannot be empty."
        }
        val number = text.toDoubleOrNull()
            ?: return "$fieldName must be a valid numeric percentage."

        if (number < 0.0 || number > 100.0) {
            return "$fieldName must be between 0 and 100%."
        }
        return null
    }

    /**
     * Calculates the raw weighted score and rounds to one decimal place.
     */
    fun calculateScore(
        averageMarks: Double,
        attendance: Double,
        assignmentScore: Double,
        quizScore: Double
    ): Double {
        val rawScore = (averageMarks * MARKS_WEIGHT) +
                (attendance * ATTENDANCE_WEIGHT) +
                (assignmentScore * ASSIGNMENT_WEIGHT) +
                (quizScore * QUIZ_WEIGHT)

        return roundToOneDecimal(rawScore)
    }

    /**
     * Classifies a calculated score into academic performance categories:
     * 80–100: Excellent
     * 70–79.9: Very Good
     * 60–69.9: Good
     * 50–59.9: Needs Improvement
     * Below 50: Critical
     */
    fun classifyScore(score: Double): PerformanceCategory {
        return when {
            score >= 80.0 -> PerformanceCategory.EXCELLENT
            score >= 70.0 -> PerformanceCategory.VERY_GOOD
            score >= 60.0 -> PerformanceCategory.GOOD
            score >= 50.0 -> PerformanceCategory.NEEDS_IMPROVEMENT
            else -> PerformanceCategory.CRITICAL
        }
    }

    /**
     * Evaluates a valid form state and returns the complete PerformanceResult.
     * Returns null if inputs fail validation.
     */
    fun evaluate(form: PerformanceFormState): PerformanceResult? {
        val errors = validate(form)
        if (errors.hasErrors) return null

        val marks = form.averageMarksText.trim().toDouble()
        val attendance = form.attendanceText.trim().toDouble()
        val assignment = form.assignmentScoreText.trim().toDouble()
        val quiz = form.quizScoreText.trim().toDouble()

        val marksContrib = roundToOneDecimal(marks * MARKS_WEIGHT)
        val attendanceContrib = roundToOneDecimal(attendance * ATTENDANCE_WEIGHT)
        val assignmentContrib = roundToOneDecimal(assignment * ASSIGNMENT_WEIGHT)
        val quizContrib = roundToOneDecimal(quiz * QUIZ_WEIGHT)

        val finalScore = calculateScore(marks, attendance, assignment, quiz)
        val category = classifyScore(finalScore)

        return PerformanceResult(
            studentName = form.studentName.trim(),
            averageMarks = marks,
            attendance = attendance,
            assignmentScore = assignment,
            quizScore = quiz,
            finalScore = finalScore,
            category = category,
            recommendedAction = category.recommendedAction,
            breakdown = ScoreBreakdown(
                marksContribution = marksContrib,
                attendanceContribution = attendanceContrib,
                assignmentContribution = assignmentContrib,
                quizContribution = quizContrib
            )
        )
    }

    /**
     * Helper to reliably round values to 1 decimal place using standard HALF_UP rounding.
     */
    fun roundToOneDecimal(value: Double): Double {
        return BigDecimal.valueOf(value)
            .setScale(1, RoundingMode.HALF_UP)
            .toDouble()
    }
}
