package se.parkster.eslab

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.occurrent.dsl.decider.decide
import org.occurrent.dsl.decider.component1
import org.occurrent.dsl.decider.component2

class RPSTests {

    @Test
    fun canInitiateGame() {
        val playerId1 = "player1"
        val playerId2 = "player2"
        val (state) = rps.decide(
            events = listOf(),
            InitiateGame(playerId1, playerId2)
        )

        assertThat(state is GameStateInitiated).isTrue()
        assertThat((state as GameStateInitiated).player1Id).isEqualTo(playerId1)
        assertThat(state.player2Id).isEqualTo(playerId2)

    }

    @Test
    fun rockBeatsScissors() {
        val playerId1 = "player1"
        val playerId2 = "player2"

        val (gameState, gameHistory) = rps.decide(
            events = listOf(),
            InitiateGame(playerId1, playerId2),
            ShowHandGesture(playerId1, HandGesture.ROCK),
            ShowHandGesture(playerId2, HandGesture.SCISSORS)
        )


        assertThat(gameState is GameStateEnded).isTrue()
        val gameWon = gameHistory.filterIsInstance<GameWon>().first()
        assertThat(gameWon.winnerId).isEqualTo(playerId1)

    }

    @Test
    fun paperBeatsRock() {
        val playerId1 = "player1"
        val playerId2 = "player2"

        val (gameState, gameHistory) = rps.decide(
            events = listOf(),
            InitiateGame(playerId1, playerId2),
            ShowHandGesture(playerId1, HandGesture.PAPER),
            ShowHandGesture(playerId2, HandGesture.ROCK)
        )

        assertThat(gameState is GameStateEnded).isTrue()
        val gameWon = gameHistory.filterIsInstance<GameWon>().first()
        assertThat(gameWon.winnerId).isEqualTo(playerId1)
    }
}