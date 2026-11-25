package com.example.assignment01.logic

import com.example.assignment01.utilities.Constants
import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {

    // obstacles[row][lane] == true -> there is an obstacle in that cell.
    // row 0 is the top of the road, the car drives below the last row.
    private val obstacles: Array<BooleanArray> =
        Array(Constants.GameLogic.ROWS) { BooleanArray(Constants.GameLogic.LANES) }

    private var ticksSinceSpawn: Int = 0

    var carLane: Int = Constants.GameLogic.START_LANE
        private set

    var crashes: Int = 0
        private set

    val livesLeft: Int
        get() = lifeCount - crashes

    val isGameOver: Boolean
        get() = crashes == lifeCount

    // flat copy of the matrix, row after row (for onSaveInstanceState):
    val obstaclesState: BooleanArray
        get() = obstacles.flatMap { it.toList() }.toBooleanArray()

    fun hasObstacle(row: Int, lane: Int): Boolean = obstacles[row][lane]

    fun moveLeft() {
        if (carLane > 0)
            carLane--
    }

    fun moveRight() {
        if (carLane < Constants.GameLogic.LANES - 1)
            carLane++
    }

    /**
     * Moves the road one step down.
     * @return true if an obstacle hit the car in this step.
     */
    fun tick(): Boolean {
        val lastRow = Constants.GameLogic.ROWS - 1

        // the last row now reaches the car row:
        val crashed = obstacles[lastRow][carLane]
        if (crashed)
            crashes++

        // every row moves one row down, a new empty row enters on top:
        for (row in lastRow downTo 1)
            obstacles[row] = obstacles[row - 1]
        obstacles[0] = BooleanArray(Constants.GameLogic.LANES)

        // new obstacle in a random lane:
        ticksSinceSpawn++
        if (ticksSinceSpawn >= Constants.GameLogic.SPAWN_INTERVAL) {
            obstacles[0][Random.nextInt(Constants.GameLogic.LANES)] = true
            ticksSinceSpawn = 0
        }

        return crashed
    }

    // Endless game: after the last life is lost everything starts over.
    fun newGame() {
        crashes = 0
        carLane = Constants.GameLogic.START_LANE
        ticksSinceSpawn = 0
        for (row in obstacles.indices)
            obstacles[row] = BooleanArray(Constants.GameLogic.LANES)
    }

    fun restore(carLane: Int, crashes: Int, obstaclesState: BooleanArray?) {
        this.carLane = carLane.coerceIn(0, Constants.GameLogic.LANES - 1)
        this.crashes = crashes.coerceIn(0, lifeCount)
        if (obstaclesState == null || obstaclesState.size != Constants.GameLogic.ROWS * Constants.GameLogic.LANES)
            return // nothing saved, keep an empty road
        for (row in 0 until Constants.GameLogic.ROWS)
            for (lane in 0 until Constants.GameLogic.LANES)
                obstacles[row][lane] = obstaclesState[row * Constants.GameLogic.LANES + lane]
    }
}
