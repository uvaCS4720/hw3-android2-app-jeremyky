package edu.nd.pmcburne.hwapp.one.data

data class Game(
    val gameId: String,
    val awayTeamName: String,
    val homeTeamName: String,
    val awayScore: String?,
    val homeScore: String?,
    val gameState: GameState,
    val startTime: String,
    val periodDisplay: String,
    val winnerName: String?
)

enum class GameState {
    UPCOMING,
    LIVE,
    FINAL
}
