package com.sourav.streaks.data

import android.os.Parcelable
import java.time.LocalDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
        val time: String, // ISO_LOCAL_TIME string
        val days: List<Int> = emptyList() // 0=Mon, 6=Sun
) : Parcelable

@Parcelize
data class Streak(
        val id: String,
        val name: String,
        val emoji: String,
        val frequency: FrequencyType,
        val frequencyCount: Int,
        val createdDate: String, // Store as ISO string for Parcelable
        val lastCompletedDate: String?, // Store as ISO string or null
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val isCompletedToday: Boolean = false,
        val completions: List<String> = emptyList(), // Store as ISO strings
        val reminder: Reminder? = null,
        val color: String = "#FF9900", // Default to neon orange
        val position: Int = Int.MAX_VALUE // New field for ordering
) : Parcelable {
        fun getCreatedDate(): LocalDate = LocalDate.parse(createdDate)
        fun getLastCompletedDate(): LocalDate? = lastCompletedDate?.let { LocalDate.parse(it) }
        fun asLocalDateCompletions(): List<LocalDate> = completions.map { LocalDate.parse(it) }
}

@Parcelize
enum class FrequencyType : Parcelable {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
}

data class StreakExportDto(
        val id: String,
        val name: String,
        val emoji: String,
        val frequency: FrequencyType,
        val frequencyCount: Int,
        val createdDate: String, // formatted as ISO string
        val lastCompletedDate: String?, // formatted as ISO string or null
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val completions: List<String> = emptyList(),
        val reminder: Reminder? = null,
        val color: String = "#FF9900", // Default to neon orange
        val position: Int = Int.MAX_VALUE // New field for ordering
)
