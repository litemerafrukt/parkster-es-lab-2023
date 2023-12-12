package se.parkster.eslab

import java.time.ZonedDateTime
import java.util.UUID

import org.occurrent.dsl.decider.decider

val rps = decider(
    initialState = GameStateUnInitiated(),
    decide = ::decide,
    evolve = ::evolve,
)

sealed interface GameCommand

data class InitiateGame(
    val playerId1: String,
    val playerId2: String
): GameCommand

data class ShowHandGesture(
    val playerId: String,
    val gesture: HandGesture
): GameCommand

fun decide(command: GameCommand, state: GameState): List<RPSEvent> =
    when (command) {
        is InitiateGame ->  {
            if (state !is GameStateUnInitiated) {
                throw IllegalStateException("Game already initiated")
            }

            listOf(GameInitiated(
                eventId = UUID.randomUUID().toString(),
                gameId = UUID.randomUUID().toString(),
                dateTime = ZonedDateTime.now(),
                playerId1 = command.playerId1,
                playerId2 = command.playerId2
            ))
        }
        is ShowHandGesture -> {
            when (state) {
                is GameStateStarted -> {
                    val gameResult: RPSEvent

                    val player1Move = if(state.player1Id == command.playerId) command.gesture else state.playerMove
                    val player2Move = if(state.player2Id == command.playerId) command.gesture else state.playerMove

                    gameResult = if (state.playerMove == command.gesture) {
                        GameDrawn(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId
                        )
                    } else {
                        val winnerId = if (player1Move == HandGesture.ROCK && player2Move == HandGesture.SCISSORS ||
                            player1Move == HandGesture.PAPER && player2Move == HandGesture.ROCK ||
                            player1Move == HandGesture.SCISSORS && player2Move == HandGesture.PAPER
                        ) {
                            state.player1Id
                        } else {
                            state.player2Id
                        }

                        GameWon(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId,
                            winnerId = winnerId,
                            loserId = if (winnerId == state.player1Id) state.player2Id else state.player1Id
                        )
                    }


                    listOf(
                        HandShown(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId,
                            playerId = command.playerId,
                            gesture = command.gesture
                        ),

                        GameEnded(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId
                        ),

                        gameResult
                    )
                }

                is GameStateInitiated -> {
                    listOf(
                        HandShown(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId,
                            playerId = command.playerId,
                            gesture = command.gesture
                        ),
                        GameStarted(
                            eventId = UUID.randomUUID().toString(),
                            dateTime = ZonedDateTime.now(),
                            gameId = state.gameId
                        )
                    )
                }

                else -> {
                    throw IllegalStateException("Can't show hand gesture when in state $state")
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