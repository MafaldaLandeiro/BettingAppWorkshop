package org.example.app

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.games.BettingGame
import org.example.games.CoinToss
import org.example.games.DiceRoll
import org.example.games.HorseRace
import org.example.models.Bet
import org.example.models.Player

// Betting App with Coroutines
class BettingApp {
    private val players = mutableListOf<Player>()  // List to track all players' balances
    private var currentPlayer: Player? = null  // Reference to the current player
    private val games = mapOf(
        1 to CoinToss(),
        2 to DiceRoll(),
        3 to HorseRace()
    )

    suspend fun start() = coroutineScope {
        // Asking for player's name at the start
        println("🎲 Welcome to the Kotlin Betting App with Coroutines! 🎲")
        println("Enter your name to start:")
        val playerName = readlnOrNull()?.trim() ?: "Player"

        // Initialize the current player with a starting balance of 100
        currentPlayer = Player(playerName, 100)
        players.add(currentPlayer!!)

        println("Hello, ${currentPlayer?.name}! Your starting balance is 100 credits.")

        while ((currentPlayer?.balance ?: 0) > 0) {
            println("\nYour Balance: ${currentPlayer?.balance} credits")
            println("Choose a game: 1) Coin Toss  2) Dice Roll  3) Horse Race  4) Bet on Multiple Games  5) Show Leaderboard  6) Exit ")
            val choice = readLine()?.toIntOrNull()

            when (choice) {
                6 -> break // Exit the game
                5 -> showLeaderboard() // Show the leaderboard
                4 -> {
                    // Running multiple bets in parallel
                    placeMultipleBets()
                    continue
                }

                else -> {
                    val game = games[choice]
                    if (game != null) {
                        placeBet(game)
                    } else {
                        println("❌ Invalid choice, try again.")
                    }
                }
            }
        }

        println("Game Over! Final Balance: ${currentPlayer?.balance} credits")
        showLeaderboard() // Show leaderboard when game ends
    }

    private suspend fun placeBet(game: BettingGame) {
        println("You selected: ${game.name}")

        println("Enter bet amount:")
        val betAmount = readLine()?.toIntOrNull() ?: return
        if (betAmount > (currentPlayer?.balance ?: 0) || betAmount <= 0) {
            println("❌ Invalid bet amount!")
            return
        }

        println("Enter your choice (Heads/Tails for Coin Toss, 1-6 for Dice Roll, or Horse Name for Horse Race):")
        val userChoice = readLine()?.lowercase() ?: return

        val bet = Bet(game.name, betAmount, userChoice)

        try {
            val won = game.playBet(bet)
            if (won) {
                println("✅ You win! +$betAmount credits")
                currentPlayer?.balance = (currentPlayer?.balance ?: 0) + betAmount
            } else {
                println("❌ You lost! -$betAmount credits")
                currentPlayer?.balance = (currentPlayer?.balance ?: 0) - betAmount
            }
        } catch (e: Exception) {
            println("⚠️ An error occurred: ${e.message}")
        }
    }

    // Function to place bets on multiple games at once using async coroutines
    private suspend fun placeMultipleBets() = coroutineScope {
        println("💰 Betting on multiple games at once!")

        val bets = mutableListOf<Deferred<Pair<Bet, Boolean>>>()

        for ((_, game) in games) {
            println("Enter bet amount for ${game.name}:")
            val betAmount = readLine()?.toIntOrNull() ?: continue
            if (betAmount > (currentPlayer?.balance ?: 0) || betAmount <= 0) {
                println("❌ Invalid bet amount for ${game.name}, skipping.")
                continue
            }

            println("Enter your choice for ${game.name}:")
            val userChoice = readLine()?.lowercase() ?: continue

            val bet = Bet(game.name, betAmount, userChoice)
            val gameResult = async { bet to game.playBet(bet) }
            bets.add(gameResult)
        }

        // Await all betting results
        for (result in bets) {
            val (bet, won) = result.await()
            if (won) {
                println("✅ You won in ${bet.game}! +${bet.amount} credits")
                currentPlayer?.balance = (currentPlayer?.balance ?: 0) + bet.amount
            } else {
                println("❌ You lost in ${bet.game}! -${bet.amount} credits")
                currentPlayer?.balance = (currentPlayer?.balance ?: 0) - bet.amount
            }
        }
    }

    // Display the leaderboard
    private fun showLeaderboard() {
        println("\n🏆 Leaderboard 🏆")
        players.sortedByDescending { it.balance }
            .forEachIndexed { index, player ->
                println("${index + 1}. ${player.name} - ${player.balance} credits")
            }
    }
}
