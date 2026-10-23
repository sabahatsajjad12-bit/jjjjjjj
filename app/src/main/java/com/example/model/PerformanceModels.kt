package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Performance classification categories as defined in the academic evaluation guidelines.
 */
enum class PerformanceCategory(
    val title: String,
    val scoreRangeText: String,
    val recommendedAction: String,
    val badgeColor: Color,
    val badgeContainerColor: Color
) {
    EXCELLENT(
        title = "Excellent",
        scoreRangeText = "80 – 100",
        recommendedAction = "Maintain your current study habits and continue aiming for high performance.",
        badgeColor = Color(0xFF047857), // Emerald Green
        badgeContainerColor = Color(0xFFD1FAE5)
    ),
    VERY_GOOD(
        title = "Very Good",
        scoreRangeText = "70 – 79.9",
        recommendedAction = "Maintain your performance and focus on the areas with relatively lower scores.",
        badgeColor = Color(0xFF0284C7), // Sky Blue
        badgeContainerColor = Color(0xFFE0F2FE)
    ),
    GOOD(
        title = "Good",
        scoreRangeText = "60 – 69.9",
        recommendedAction = "Improve your weaker areas through more consistent study and practice.",
        badgeColor = Color(0xFFD97706), // Amber
        badgeContainerColor = Color(0xFFFEF3C7)
    ),
    NEEDS_IMPROVEMENT(
        title = "Needs Improvement",
        scoreRangeText = "50 – 59.9",
        recommendedAction = "Increase study time, improve attendance, and seek academic support where needed.",
        badgeColor = Color(0xFFEA580C), // Orange
        badgeContainerColor = Color(0xFFFFEDD5)
    ),
    CRITICAL(
        title = "Critical",
        scoreRangeText = "Below 50",
        recommendedAction = "Develop an immediate improvement plan and consult your teacher/advisor for academic support.",
        badgeColor = Color(0xFFDC2626), // Crimson Red
        badgeContainerColor = Color(0xFFFEE2E2)
    )
}

/**
 * Form input data model holding user text inputs.
 */
data class PerformanceFormState(
    val studentName: String = "",
    val averageMarksText: String = "",
    val attendanceText: String = "",
    val assignmentScoreText: String = "",
    val quizScoreText: String = ""
)

/**
 * Validation errors associated with the input fields.
 */
data class FormValidationErrors(
    val studentNameError: String? = null,
    val averageMarksError: String? = null,
    val attendanceError: String? = null,
    val assignmentScoreError: String? = null,
    val quizScoreError: String? = null
) {
    val hasErrors: Boolean
        get() = studentNameError != null ||
                averageMarksError != null ||
                attendanceError != null ||
                assignmentScoreError != null ||
                quizScoreError != null
}

/**
 * Breakdown of weighted contributions.
 */
data class ScoreBreakdown(
    val marksContribution: Double,
    val attendanceContribution: Double,
    val assignmentContribution: Double,
    val quizContribution: Double
)

/**
 * Final calculated evaluation result for a student.
 */
data class PerformanceResult(
    val studentName: String,
    val averageMarks: Double,
    val attendance: Double,
    val assignmentScore: Double,
    val quizScore: Double,
    val finalScore: Double,
    val category: PerformanceCategory,
    val recommendedAction: String,
    val breakdown: ScoreBreakdown
)
