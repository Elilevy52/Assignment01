package com.example.assignment01.utilities

object Constants {
    object GameLogic {
        const val ROWS: Int = 5
        const val LANES: Int = 3
        const val START_LANE: Int = 1
        // a new obstacle appears every SPAWN_INTERVAL ticks:
        const val SPAWN_INTERVAL: Int = 2
    }

    object Timer {
        const val DELAY: Long = 600L
        const val VIBRATION_DURATION: Long = 300L
    }

    object StateKeys {
        const val CAR_LANE: String = "CAR_LANE"
        const val CRASHES: String = "CRASHES"
        const val OBSTACLES: String = "OBSTACLES"
    }

    object Logging {
        const val GAME_STATUS: String = "Game Status"
    }
}
