package org.example.games

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.example.models.Bet
import kotlin.random.Random

// Horse Race Game
class HorseRace : BettingGame() {
    override val name = "Horse Race"

    override suspend fun playBet(bet: Bet): Boolean = coroutineScope {
        println("🏇 The horse race is starting... (Processing in background)")
        delay(Random.nextLong(2000, 5000))

        val horses = listOf("Thunder", "Blaze", "Storm", "Lightning")
        val winner = horses.random()
        println("🏆 Winner: $winner")

        return@coroutineScope bet.userChoice.lowercase() == winner.lowercase()
    }
}
