package edu.nd.pmcburne.hwapp.one.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val gameId: String,
    val gender: String,
    val dateKey: String,
    val awayTeamName: String,
    val homeTeamName: String,
    val awayScore: String?,
    val homeScore: String?,
    val gameState: String,
    val startTime: String,
    val currentPeriod: String,
    val contestClock: String?,
    val winnerName: String?
)
