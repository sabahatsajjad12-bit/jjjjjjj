package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PerformanceCategory
import com.example.model.PerformanceResult

/**
 * Visual presentation of the calculated student performance score,
 * category, recommended next action, and weighted breakdown.
 */
@Composable
fun ScoreDisplayCard(
    result: PerformanceResult,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("score_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(result.category.badgeColor.copy(alpha = 0.35f))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge with student's name
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Student Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = result.studentName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Text(
                    text = "Overall Performance Score",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Circular Progress Indicator Gauge
                CircularScoreGauge(
                    score = result.finalScore,
                    categoryColor = result.category.badgeColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category pill
                CategoryBadge(
                    category = result.category,
                    modifier = Modifier.testTag("category_display")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Recommended Next Action Card
                RecommendationCard(
                    category = result.category,
                    recommendedAction = result.recommendedAction,
                    modifier = Modifier.testTag("recommendation_display")
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Weighted Contributions Breakdown
                WeightedBreakdownSection(result = result)

                Spacer(modifier = Modifier.height(16.dp))

                // Copy report button
                OutlinedButton(
                    onClick = {
                        copyReportToClipboard(context, result)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_report_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Summary",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Performance Report")
                }
            }
        }
    }
}

/**
 * Animated circular score progress gauge with center score text.
 */
@Composable
private fun CircularScoreGauge(
    score: Double,
    categoryColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        val target = (score.toFloat() / 100f).coerceIn(0f, 1f)
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(180.dp)
    ) {
        val strokeWidth = 14.dp
        val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

        Canvas(modifier = Modifier.size(170.dp)) {
            val strokePx = strokeWidth.toPx()

            // Background circle track
            drawCircle(
                color = trackColor,
                style = Stroke(width = strokePx)
            )

            // Animated foreground arc
            val sweepAngle = 360f * animatedProgress.value
            drawArc(
                color = categoryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round
                )
            )
        }

        // Center score display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = String.format(java.util.Locale.US, "%.1f", score),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = categoryColor,
                    fontSize = 42.sp
                ),
                modifier = Modifier.testTag("score_display")
            )
            Text(
                text = "out of 100",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Category chip display with title and range.
 */
@Composable
private fun CategoryBadge(
    category: PerformanceCategory,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = category.badgeContainerColor,
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(category.badgeColor)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(category.badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${category.title.uppercase()} (${category.scoreRangeText})",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = category.badgeColor
                )
            )
        }
    }
}

/**
 * Recommended Next Action display card with status icon.
 */
@Composable
private fun RecommendationCard(
    category: PerformanceCategory,
    recommendedAction: String,
    modifier: Modifier = Modifier
) {
    val actionIcon: ImageVector = when (category) {
        PerformanceCategory.EXCELLENT -> Icons.Default.CheckCircle
        PerformanceCategory.VERY_GOOD -> Icons.Default.Lightbulb
        PerformanceCategory.GOOD -> Icons.AutoMirrored.Filled.MenuBook
        PerformanceCategory.NEEDS_IMPROVEMENT -> Icons.Default.Warning
        PerformanceCategory.CRITICAL -> Icons.Default.Warning
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = category.badgeContainerColor.copy(alpha = 0.5f),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(category.badgeColor.copy(alpha = 0.5f))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = "Recommendation icon",
                tint = category.badgeColor,
                modifier = Modifier
                    .size(26.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recommended Next Action",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = category.badgeColor
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recommendedAction,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}

/**
 * Component-level contribution breakdown showing exact weight calculation.
 */
@Composable
private fun WeightedBreakdownSection(
    result: PerformanceResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Weighted Score Breakdown",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Formula: (Marks × 40%) + (Attendance × 20%) + (Assignments × 20%) + (Quizzes × 20%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        BreakdownRow(
            icon = Icons.Default.AutoGraph,
            label = "Average Marks (40%)",
            inputPercent = result.averageMarks,
            pointsAdded = result.breakdown.marksContribution
        )
        BreakdownRow(
            icon = Icons.Default.EventAvailable,
            label = "Attendance (20%)",
            inputPercent = result.attendance,
            pointsAdded = result.breakdown.attendanceContribution
        )
        BreakdownRow(
            icon = Icons.Default.Assignment,
            label = "Assignment Score (20%)",
            inputPercent = result.assignmentScore,
            pointsAdded = result.breakdown.assignmentContribution
        )
        BreakdownRow(
            icon = Icons.Default.Quiz,
            label = "Quiz Score (20%)",
            inputPercent = result.quizScore,
            pointsAdded = result.breakdown.quizContribution
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Performance Score",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", result.finalScore)} / 100",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = result.category.badgeColor
                )
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    icon: ImageVector,
    label: String,
    inputPercent: Double,
    pointsAdded: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "${String.format(java.util.Locale.US, "%.1f", inputPercent)}% → +${String.format(java.util.Locale.US, "%.1f", pointsAdded)} pts",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}

/**
 * Copies the student evaluation report summary to the system clipboard.
 */
private fun copyReportToClipboard(context: Context, result: PerformanceResult) {
    val report = buildString {
        appendLine("--- STUDENT STUDY PERFORMANCE REPORT ---")
        appendLine("Student Name: ${result.studentName}")
        appendLine("Final Score: ${String.format(java.util.Locale.US, "%.1f", result.finalScore)} / 100")
        appendLine("Classification: ${result.category.title} (${result.category.scoreRangeText})")
        appendLine("Recommended Action: ${result.recommendedAction}")
        appendLine()
        appendLine("Component Breakdown:")
        appendLine("• Average Marks (40%): ${result.averageMarks}% -> ${result.breakdown.marksContribution} pts")
        appendLine("• Attendance (20%): ${result.attendance}% -> ${result.breakdown.attendanceContribution} pts")
        appendLine("• Assignments (20%): ${result.assignmentScore}% -> ${result.breakdown.assignmentContribution} pts")
        appendLine("• Quizzes (20%): ${result.quizScore}% -> ${result.breakdown.quizContribution} pts")
    }

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Student Performance Report", report)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Performance report copied to clipboard", Toast.LENGTH_SHORT).show()
}
