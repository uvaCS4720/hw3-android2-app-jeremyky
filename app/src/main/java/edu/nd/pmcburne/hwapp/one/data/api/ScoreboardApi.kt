package edu.nd.pmcburne.hwapp.one.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScoreboardApi {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): Response<ScoreboardResponse>
}
