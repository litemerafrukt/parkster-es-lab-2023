package se.parkster.eslab

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RPSTests {

    private val service = RPSService()

    @Test
    fun canInitiateGame() {
        val events = emptyList<RPSEvent>()
        val playerId1 = "player1"
        val playerId2 = "player2"
        val result = service.initiateGame(events, playerId1, playerId2)

        val state = result.evolve()

        assertThat(state is GameStateInitiated).isTrue()
        assertThat((state as GameStateInitiated).player1Id).isEqualTo(playerId1)
        assertThat(state.player2Id).isEqualTo(playerId2)

    }

    @Test
    fun rockBeatsScissors() {
        val playerId1 = "player1"
        val playerId2 = "player2"

        val operations = listOf(
            { e : List<RPSEvent> -> service.initiateGame(e, playerId1, playerId2) },
            { e : List<RPSEvent> -> service.showHandGesture(e, playerId1, HandGesture.ROCK) },
            { e : List<RPSEvent> -> service.showHandGesture(e, playerId2, HandGesture.SCISSORS) }
        )

        val gameHistory = operations.fold(emptyList<RPSEvent>())
        { acc, operation -> acc + operation(acc) }

        val gameState = gameHistory.evolve()

        assertThat(gameState is GameStateEnded).isTrue()
        val gameWon = gameHistory.filterIsInstance<GameWon>().first()
        assertThat(gameWon.winnerId).isEqualTo(playerId1)

    }

    @Test
    fun paperBeatsRock() {
        val playerId1 = "player1"
        val playerId2 = "player2"

        val operations = listOf(
            { e : List<RPSEvent> -> service.initiateGame(e, playerId1, playerId2) },
            { e : List<RPSEvent> -> service.showHandGesture(e, playerId1, HandGesture.PAPER) },
            { e : List<RPSEvent> -> service.showHandGesture(e, playerId2, HandGesture.ROCK) }
        )

        val gameHistory = operations.fold(emptyList<RPSEvent>())
        { acc, operation -> acc + operation(acc) }

        val gameState = gameHistory.evolve()

        assertThat(gameState is GameStateEnded).isTrue()
        val gameWon = gameHistory.filterIsInstance<GameWon>().first()
        assertThat(gameWon.winnerId).isEqualTo(playerId1)

    }
}