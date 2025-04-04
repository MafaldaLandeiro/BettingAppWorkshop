package org.example.games

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.example.models.Bet
import kotlin.random.Random

// Dice Roll Game
class DiceRoll : BettingGame() {
    override val name = "Dice Roll"

    override suspend fun playBet(bet: Bet): Boolean = coroutineScope {
        println("Rolling the dice... 🎲 (Processing in background)")
        delay(Random.nextLong(1500, 3500))

        val result = Random.nextInt(1, 7)
        println("🎲 Dice rolled: $result")

        return@coroutineScope bet.userChoice.toIntOrNull() == result
    }
}
