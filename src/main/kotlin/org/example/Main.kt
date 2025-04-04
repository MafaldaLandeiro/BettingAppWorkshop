package org.example

import org.example.app.BettingApp

// Main function to start the app
suspend fun main() {
    println("Hello, World!")
    val app = BettingApp()
    app.start()
}
