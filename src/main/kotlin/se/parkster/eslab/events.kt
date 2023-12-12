package se.parkster.eslab

import java.time.ZonedDateTime

// Events, rock paper scissors

// Game initialized

// Game started

// Player shown hand gesture

// Hand shown

// Game ended

// Player 1 won

// Player 2 won

// Drawn

sealed interface RPSEvent

enum class Move {
    ROCK, PAPER, SCISSORS
}

data class GameInitiated(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val playerId1: String,
    val playerId2: String
) : RPSEvent

data class GameStarted(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
    // Might be unnecessary
    val player1Move: Move?,
    val player2Move: Move?
) : RPSEvent

data class HandShown(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
    val playerId: String,
    val move: Move
) : RPSEvent

data class GameWon(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
    val winnerId: String,
    val loserId: String
) : RPSEvent

data class GameDrawn(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String
) : RPSEvent

data class GameEnded(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
) : RPSEvent

