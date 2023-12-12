package se.parkster.eslab


sealed interface GameState

class GameStateUnInitiated() : GameState
data class GameStateInitiated(
    val player1Id: String,
    val player2Id: String,
    val gameId: String
) : GameState

data class GameStateStarted (
    val player1Id: String,
    val player2Id: String,
    val playerMove: HandGesture,
    val gesturer: String,
    val gameId: String
) : GameState

data class GameStateEnded(
    val player1Id: String,
    val player2Id: String,
    val player1Move: HandGesture,
    val player2Move: HandGesture,
    val gameId: String
) : GameState
