package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.calculator.PerformanceCalculator
import com.example.model.FormValidationErrors
import com.example.model.PerformanceFormState
import com.example.model.PerformanceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for the Performance Calculator Screen.
 */
data class PerformanceUiState(
    val formState: PerformanceFormState = PerformanceFormState(),
    val validationErrors: FormValidationErrors = FormValidationErrors(),
    val result: PerformanceResult? = null,
    val hasAttemptedCalculation: Boolean = false
)

/**
 * Preset data structure for test cases and quick fill.
 */
data class PerformancePreset(
    val label: String,
    val studentName: String,
    val marks: String,
    val attendance: String,
    val assignment: String,
    val quiz: String,
    val expectedScore: String,
    val expectedCategory: String
)

class PerformanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceUiState())
    val uiState: StateFlow<PerformanceUiState> = _uiState.asStateFlow()

    val presets = listOf(
        PerformancePreset(
            label = "Example 1 (90.0)",
            studentName = "Alex Mercer",
            marks = "90",
            attendance = "95",
            assignment = "85",
            quiz = "90",
            expectedScore = "90.0",
            expectedCategory = "Excellent"
        ),
        PerformancePreset(
            label = "Example 2 (65.0)",
            studentName = "Samira Khan",
            marks = "65",
            attendance = "70",
            assignment = "60",
            quiz = "65",
            expectedScore = "65.0",
            expectedCategory = "Good"
        ),
        PerformancePreset(
            label = "Example 3 (43.0)",
            studentName = "Jordan Lee",
            marks = "40",
            attendance = "45",
            assignment = "50",
            quiz = "40",
            expectedScore = "43.0",
            expectedCategory = "Critical"
        )
    )

    fun onStudentNameChange(value: String) {
        _uiState.update { current ->
            val updatedForm = current.formState.copy(studentName = value)
            current.copy(
                formState = updatedForm,
                validationErrors = if (current.hasAttemptedCalculation) {
                    PerformanceCalculator.validate(updatedForm)
                } else current.validationErrors
            )
        }
    }

    fun onMarksChange(value: String) {
        val sanitized = sanitizeNumericInput(value)
        _uiState.update { current ->
            val updatedForm = current.formState.copy(averageMarksText = sanitized)
            current.copy(
                formState = updatedForm,
                validationErrors = if (current.hasAttemptedCalculation) {
                    PerformanceCalculator.validate(updatedForm)
                } else current.validationErrors
            )
        }
    }

    fun onAttendanceChange(value: String) {
        val sanitized = sanitizeNumericInput(value)
        _uiState.update { current ->
            val updatedForm = current.formState.copy(attendanceText = sanitized)
            current.copy(
                formState = updatedForm,
                validationErrors = if (current.hasAttemptedCalculation) {
                    PerformanceCalculator.validate(updatedForm)
                } else current.validationErrors
            )
        }
    }

    fun onAssignmentChange(value: String) {
        val sanitized = sanitizeNumericInput(value)
        _uiState.update { current ->
            val updatedForm = current.formState.copy(assignmentScoreText = sanitized)
            current.copy(
                formState = updatedForm,
                validationErrors = if (current.hasAttemptedCalculation) {
                    PerformanceCalculator.validate(updatedForm)
                } else current.validationErrors
            )
        }
    }

    fun onQuizChange(value: String) {
        val sanitized = sanitizeNumericInput(value)
        _uiState.update { current ->
            val updatedForm = current.formState.copy(quizScoreText = sanitized)
            current.copy(
                formState = updatedForm,
                validationErrors = if (current.hasAttemptedCalculation) {
                    PerformanceCalculator.validate(updatedForm)
                } else current.validationErrors
            )
        }
    }

    /**
     * Executes validation and calculates overall performance score.
     */
    fun calculatePerformance() {
        val form = _uiState.value.formState
        val errors = PerformanceCalculator.validate(form)

        if (errors.hasErrors) {
            _uiState.update {
                it.copy(
                    validationErrors = errors,
                    result = null,
                    hasAttemptedCalculation = true
                )
            }
            return
        }

        val result = PerformanceCalculator.evaluate(form)
        _uiState.update {
            it.copy(
                validationErrors = FormValidationErrors(),
                result = result,
                hasAttemptedCalculation = true
            )
        }
    }

    /**
     * Resets the entire form and clears calculated score, category, and recommendation.
     */
    fun reset() {
        _uiState.value = PerformanceUiState()
    }

    /**
     * Loads a preset example and calculates it immediately.
     */
    fun applyPreset(preset: PerformancePreset) {
        val form = PerformanceFormState(
            studentName = preset.studentName,
            averageMarksText = preset.marks,
            attendanceText = preset.attendance,
            assignmentScoreText = preset.assignment,
            quizScoreText = preset.quiz
        )
        val result = PerformanceCalculator.evaluate(form)
        _uiState.value = PerformanceUiState(
            formState = form,
            validationErrors = FormValidationErrors(),
            result = result,
            hasAttemptedCalculation = true
        )
    }

    /**
     * Filters input to numeric characters and at most one decimal point.
     */
    private fun sanitizeNumericInput(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        // Ensure at most one decimal point
        val parts = filtered.split('.')
        return if (parts.size <= 2) {
            filtered
        } else {
            "${parts[0]}.${parts.subList(1, parts.size).joinToString("")}"
        }
    }
}
