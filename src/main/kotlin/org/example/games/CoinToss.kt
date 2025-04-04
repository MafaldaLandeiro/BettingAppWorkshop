package org.example.games

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.example.models.Bet
import kotlin.random.Random

// Coin Toss Game
class CoinToss : BettingGame() {
    override val name = "Coin Toss"

    override suspend fun playBet(bet: Bet): Boolean = coroutineScope {
        println("Flipping the coin... (Processing in background)")
        delay(Random.nextLong(1000, 3000))  // Random suspense delay

        val result = if (Random.nextBoolean()) "heads" else "tails"
        println("🪙 Coin landed on: $result")

        return@coroutineScope bet.userChoice == result
    }
}
