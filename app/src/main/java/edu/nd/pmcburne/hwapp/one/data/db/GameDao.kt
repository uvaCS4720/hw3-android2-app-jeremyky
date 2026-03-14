package edu.nd.pmcburne.hwapp.one.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE gender = :gender AND dateKey = :dateKey ORDER BY startTime")
    fun getGamesByDateAndGender(gender: String, dateKey: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE gender = :gender AND dateKey = :dateKey")
    suspend fun deleteByDateAndGender(gender: String, dateKey: String)
}
