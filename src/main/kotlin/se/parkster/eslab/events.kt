package se.parkster.eslab

import java.time.ZonedDateTime

sealed interface RPSEvent

enum class HandGesture {
    ROCK, PAPER, SCISSORS
}

data class GameInitiated(
    val eventId: String,
    val gameId: String,
    val dateTime: ZonedDateTime,
    val playerId1: String,
    val playerId2: String
) : RPSEvent

data class GameStarted(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
) : RPSEvent

data class HandShown(
    val eventId: String,
    val dateTime: ZonedDateTime,
    val gameId: String,
    val playerId: String,
    val gesture: HandGesture
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

