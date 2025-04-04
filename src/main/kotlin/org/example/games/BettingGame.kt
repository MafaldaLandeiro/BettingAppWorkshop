package org.example.games

import org.example.models.Bet

abstract class BettingGame {
    abstract val name: String
    abstract suspend fun playBet(bet: Bet): Boolean
}
