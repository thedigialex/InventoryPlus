package com.thedigialex.inventory.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Feature::class,
        parentColumns = ["id"],
        childColumns = ["featureId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("featureId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val featureId: Int,
    val title: String,
    val description: String,
    val dueDate: Long?,
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    val repeatIntervalDays: Int = 0
) {
    companion object {
        private val REPEAT_OPTIONS = listOf(0, 1, 7, 14, 30)

        fun repeatToSpinnerIndex(days: Int) = REPEAT_OPTIONS.indexOf(days).coerceAtLeast(0)
        fun repeatFromSpinnerIndex(index: Int) = REPEAT_OPTIONS.getOrElse(index) { 0 }
        fun repeatLabel(days: Int) = when (days) {
            1 -> "Daily"; 7 -> "Weekly"; 14 -> "Bi-weekly"; 30 -> "Monthly"
            else -> "Every ${days}d"
        }
    }
}
