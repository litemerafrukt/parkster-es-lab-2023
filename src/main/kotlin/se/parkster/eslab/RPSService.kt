package se.parkster.eslab

import java.time.ZonedDateTime
import java.util.UUID

class RPSService {
    fun initiateGame(
        events: List<RPSEvent>,
        playerId1: String,
        playerId2: String
    ): List<RPSEvent> {
        if (events.isNotEmpty()) {
            throw IllegalStateException("Game already initiated")
        }

        return listOf(GameInitiated(
            eventId = UUID.randomUUID().toString(),
            gameId = UUID.randomUUID().toString(),
            dateTime = ZonedDateTime.now(),
            playerId1 = playerId1,
            playerId2 = playerId2
        ))
    }

    fun showHandGesture(
        events: List<RPSEvent>,
        playerId: String,
        gesture: HandGesture
    ): List<RPSEvent> {
        return when (val gameState = events.evolve()) {
            is GameStateStarted -> {
                val gameResult: RPSEvent

                val player1Move = if(gameState.player1Id == playerId) gesture else gameState.playerMove
                val player2Move = if(gameState.player2Id == playerId) gesture else gameState.playerMove

                gameResult = if (gameState.playerMove == gesture) {
                    GameDrawn(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId
                    )
                } else {
                    val winnerId = if (player1Move == HandGesture.ROCK && player2Move == HandGesture.SCISSORS ||
                        player1Move == HandGesture.PAPER && player2Move == HandGesture.ROCK ||
                        player1Move == HandGesture.SCISSORS && player2Move == HandGesture.PAPER
                    ) {
                        gameState.player1Id
                    } else {
                        gameState.player2Id
                    }

                    GameWon(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId,
                        winnerId = winnerId,
                        loserId = if (winnerId == gameState.player1Id) gameState.player2Id else gameState.player1Id
                    )
                }


                listOf(
                    HandShown(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId,
                        playerId = playerId,
                        gesture = gesture
                    ),

                    GameEnded(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId
                    ),

                    gameResult
                )
            }

            is GameStateInitiated -> {
                listOf(
                    HandShown(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId,
                        playerId = playerId,
                        gesture = gesture
                    ),
                    GameStarted(
                        eventId = UUID.randomUUID().toString(),
                        dateTime = ZonedDateTime.now(),
                        gameId = gameState.gameId
                    )
                )
            }

            else -> {
                throw IllegalStateException("Can't show hand gesture when in state $gameState")
            }
        }
    }

}


private fun evolve(gameState: GameState, event: RPSEvent): GameState =
    when (event) {
        is GameInitiated -> {
            if (gameState !is GameStateUnInitiated) {
                throw IllegalStateException("Can't initiate game when in state $gameState")
            }

            GameStateInitiated(
                player1Id = event.playerId1,
                player2Id = event.playerId2,
                gameId = event.gameId
            )
        }
        is HandShown -> {
            when (gameState) {
                is GameStateInitiated -> {
                    GameStateStarted(
                        player1Id = gameState.player1Id,
                        player2Id = gameState.player2Id,
                        playerMove = event.gesture,
                        gesturer = event.playerId,
                        gameId = gameState.gameId
                    )
                }
                is GameStateStarted -> {
                    val move1 = if(gameState.player1Id == event.playerId) event.gesture else gameState.playerMove
                    val move2 = if(gameState.player2Id == event.playerId) event.gesture else gameState.playerMove

                    GameStateEnded(
                        player1Id = gameState.player1Id,
                        player2Id = gameState.player2Id,
                        player1Move = move1,
                        player2Move = move2,
                        gameId = gameState.gameId
                    )
                }
                else -> throw IllegalStateException("Can't move when in state $gameState")
            }
        }
        else -> gameState
    }

fun List<RPSEvent>.evolve() : GameState = fold(
    GameStateUnInitiated(),
    ::evolve)