package com.example

import com.example.calculator.PerformanceCalculator
import com.example.model.PerformanceCategory
import com.example.model.PerformanceFormState
import com.example.ui.PerformanceViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PerformanceCalculatorTest {

    @Test
    fun `verify Example 1 - Expected score 90_0 and Excellent category`() {
        // Average Marks = 90, Attendance = 95, Assignment = 85, Quiz = 90
        val form = PerformanceFormState(
            studentName = "Alex Mercer",
            averageMarksText = "90",
            attendanceText = "95",
            assignmentScoreText = "85",
            quizScoreText = "90"
        )

        val result = PerformanceCalculator.evaluate(form)
        assertNotNull("Result should not be null for valid inputs", result)
        assertEquals(90.0, result!!.finalScore, 0.001)
        assertEquals(PerformanceCategory.EXCELLENT, result.category)
        assertEquals("Excellent", result.category.title)
        assertEquals(
            "Maintain your current study habits and continue aiming for high performance.",
            result.recommendedAction
        )
    }

    @Test
    fun `verify Example 2 - Expected score 65_0 and Good category`() {
        // Average Marks = 65, Attendance = 70, Assignment = 60, Quiz = 65
        val form = PerformanceFormState(
            studentName = "Samira Khan",
            averageMarksText = "65",
            attendanceText = "70",
            assignmentScoreText = "60",
            quizScoreText = "65"
        )

        val result = PerformanceCalculator.evaluate(form)
        assertNotNull("Result should not be null for valid inputs", result)
        assertEquals(65.0, result!!.finalScore, 0.001)
        assertEquals(PerformanceCategory.GOOD, result.category)
        assertEquals("Good", result.category.title)
        assertEquals(
            "Improve your weaker areas through more consistent study and practice.",
            result.recommendedAction
        )
    }

    @Test
    fun `verify Example 3 - Expected score 43_0 and Critical category`() {
        // Average Marks = 40, Attendance = 45, Assignment = 50, Quiz = 40
        val form = PerformanceFormState(
            studentName = "Jordan Lee",
            averageMarksText = "40",
            attendanceText = "45",
            assignmentScoreText = "50",
            quizScoreText = "40"
        )

        val result = PerformanceCalculator.evaluate(form)
        assertNotNull("Result should not be null for valid inputs", result)
        assertEquals(43.0, result!!.finalScore, 0.001)
        assertEquals(PerformanceCategory.CRITICAL, result.category)
        assertEquals("Critical", result.category.title)
        assertEquals(
            "Develop an immediate improvement plan and consult your teacher/advisor for academic support.",
            result.recommendedAction
        )
    }

    @Test
    fun `verify classification score boundaries`() {
        assertEquals(PerformanceCategory.EXCELLENT, PerformanceCalculator.classifyScore(100.0))
        assertEquals(PerformanceCategory.EXCELLENT, PerformanceCalculator.classifyScore(80.0))
        assertEquals(PerformanceCategory.VERY_GOOD, PerformanceCalculator.classifyScore(79.9))
        assertEquals(PerformanceCategory.VERY_GOOD, PerformanceCalculator.classifyScore(70.0))
        assertEquals(PerformanceCategory.GOOD, PerformanceCalculator.classifyScore(69.9))
        assertEquals(PerformanceCategory.GOOD, PerformanceCalculator.classifyScore(60.0))
        assertEquals(PerformanceCategory.NEEDS_IMPROVEMENT, PerformanceCalculator.classifyScore(59.9))
        assertEquals(PerformanceCategory.NEEDS_IMPROVEMENT, PerformanceCalculator.classifyScore(50.0))
        assertEquals(PerformanceCategory.CRITICAL, PerformanceCalculator.classifyScore(49.9))
        assertEquals(PerformanceCategory.CRITICAL, PerformanceCalculator.classifyScore(0.0))
    }

    @Test
    fun `validation rejects empty student name`() {
        val form = PerformanceFormState(
            studentName = "   ",
            averageMarksText = "90",
            attendanceText = "90",
            assignmentScoreText = "90",
            quizScoreText = "90"
        )
        val errors = PerformanceCalculator.validate(form)
        assertTrue(errors.hasErrors)
        assertEquals("Student name cannot be empty.", errors.studentNameError)
        assertNull(PerformanceCalculator.evaluate(form))
    }

    @Test
    fun `validation rejects values outside 0 to 100 range`() {
        val formNegative = PerformanceFormState(
            studentName = "Test",
            averageMarksText = "-5",
            attendanceText = "80",
            assignmentScoreText = "80",
            quizScoreText = "80"
        )
        val errorsNegative = PerformanceCalculator.validate(formNegative)
        assertTrue(errorsNegative.hasErrors)
        assertNotNull(errorsNegative.averageMarksError)

        val formOver100 = PerformanceFormState(
            studentName = "Test",
            averageMarksText = "80",
            attendanceText = "105",
            assignmentScoreText = "80",
            quizScoreText = "80"
        )
        val errorsOver100 = PerformanceCalculator.validate(formOver100)
        assertTrue(errorsOver100.hasErrors)
        assertNotNull(errorsOver100.attendanceError)
    }

    @Test
    fun `validation rejects non-numeric input`() {
        val form = PerformanceFormState(
            studentName = "Test",
            averageMarksText = "abc",
            attendanceText = "80",
            assignmentScoreText = "80",
            quizScoreText = "80"
        )
        val errors = PerformanceCalculator.validate(form)
        assertTrue(errors.hasErrors)
        assertNotNull(errors.averageMarksError)
    }

    @Test
    fun `viewModel reset returns to initial blank state`() {
        val viewModel = PerformanceViewModel()
        viewModel.onStudentNameChange("Alex")
        viewModel.onMarksChange("90")
        viewModel.onAttendanceChange("95")
        viewModel.onAssignmentChange("85")
        viewModel.onQuizChange("90")
        viewModel.calculatePerformance()

        assertNotNull(viewModel.uiState.value.result)

        // Reset
        viewModel.reset()

        val state = viewModel.uiState.value
        assertEquals("", state.formState.studentName)
        assertEquals("", state.formState.averageMarksText)
        assertEquals("", state.formState.attendanceText)
        assertEquals("", state.formState.assignmentScoreText)
        assertEquals("", state.formState.quizScoreText)
        assertNull(state.result)
        assertFalse(state.validationErrors.hasErrors)
    }
}
