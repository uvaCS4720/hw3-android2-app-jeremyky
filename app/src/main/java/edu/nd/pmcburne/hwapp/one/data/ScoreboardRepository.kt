package edu.nd.pmcburne.hwapp.one.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.data.api.RetrofitClient
import edu.nd.pmcburne.hwapp.one.data.api.mapToEntity
import edu.nd.pmcburne.hwapp.one.data.db.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.db.GameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScoreboardRepository(context: Context) {
    private val api = RetrofitClient.scoreboardApi
    private val db = AppDatabase.getInstance(context)
    private val dao = db.gameDao()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun getGames(gender: String, date: LocalDate): Flow<List<Game>> {
        val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return dao.getGamesByDateAndGender(gender, dateKey).map { entities ->
            entities.map { it.toGame() }
        }
    }

    suspend fun refresh(gender: String, date: LocalDate): Result<Unit> {
        val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return if (isOnline()) {
            try {
                val year = date.year.toString()
                val month = date.monthValue.toString().padStart(2, '0')
                val day = date.dayOfMonth.toString().padStart(2, '0')
                val response = api.getScoreboard(gender, year, month, day)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val entities = body.games.map { it.game.mapToEntity(gender, dateKey) }
                        dao.insertAll(entities)
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Empty response"))
                    }
                } else {
                    Result.failure(Exception("API error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    private fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun GameEntity.toGame(): Game {
        val state = when (gameState.lowercase()) {
            "pre" -> GameState.UPCOMING
            "post" -> GameState.FINAL
            "final" -> GameState.FINAL
            else -> GameState.LIVE
        }
        val periodDisplay = when (state) {
            GameState.UPCOMING -> ""
            GameState.FINAL -> "Final"
            GameState.LIVE -> {
                val clock = contestClock?.takeIf { it.isNotBlank() } ?: "0:00"
                "$currentPeriod $clock".trim()
            }
        }
        return Game(
            gameId = gameId,
            awayTeamName = awayTeamName,
            homeTeamName = homeTeamName,
            awayScore = awayScore,
            homeScore = homeScore,
            gameState = state,
            startTime = startTime,
            periodDisplay = periodDisplay,
            winnerName = winnerName
        )
    }
}

private fun edu.nd.pmcburne.hwapp.one.data.api.ApiGame.mapToEntity(
    gender: String,
    dateKey: String
): GameEntity {
    val winner = when {
        home.winner == true -> home.names.shortName
        away.winner == true -> away.names.shortName
        else -> null
    }
    return GameEntity(
        gameId = gameId,
        gender = gender,
        dateKey = dateKey,
        awayTeamName = away.names.shortName,
        homeTeamName = home.names.shortName,
        awayScore = away.score,
        homeScore = home.score,
        gameState = gameState,
        startTime = startTime,
        currentPeriod = currentPeriod,
        contestClock = contestClock,
        winnerName = winner
    )
}
