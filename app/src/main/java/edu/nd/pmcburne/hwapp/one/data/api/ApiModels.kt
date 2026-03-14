package edu.nd.pmcburne.hwapp.one.data.api

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("games") val games: List<GameWrapper>
)

data class GameWrapper(
    @SerializedName("game") val game: ApiGame
)

data class ApiGame(
    @SerializedName("gameID") val gameId: String,
    @SerializedName("away") val away: ApiTeam,
    @SerializedName("home") val home: ApiTeam,
    @SerializedName("gameState") val gameState: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("startTimeEpoch") val startTimeEpoch: Long?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("currentPeriod") val currentPeriod: String,
    @SerializedName("contestClock") val contestClock: String?
)

data class ApiTeam(
    @SerializedName("score") val score: String?,
    @SerializedName("names") val names: ApiTeamNames,
    @SerializedName("winner") val winner: Boolean?
)

data class ApiTeamNames(
    @SerializedName("short") val shortName: String,
    @SerializedName("char6") val char6: String?,
    @SerializedName("seo") val seo: String?,
    @SerializedName("full") val full: String?
)
