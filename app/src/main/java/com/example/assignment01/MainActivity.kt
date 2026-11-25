package com.example.assignment01

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment01.logic.GameManager
import com.example.assignment01.utilities.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var gameManager: GameManager

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    // main_IMG_obstacles[row][lane]
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>

    private lateinit var main_IMG_cars: Array<AppCompatImageView>

    private lateinit var main_FAB_left: FloatingActionButton

    private lateinit var main_FAB_right: FloatingActionButton

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            gameTick()
            // Reschedule:
            handler.postDelayed(this, Constants.Timer.DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        savedInstanceState?.let { restoreInstanceState(it) }
        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.StateKeys.CAR_LANE, gameManager.carLane)
        outState.putInt(Constants.StateKeys.CRASHES, gameManager.crashes)
        outState.putBooleanArray(Constants.StateKeys.OBSTACLES, gameManager.obstaclesState)
    }

    private fun restoreInstanceState(savedInstanceState: Bundle) {
        gameManager.restore(
            carLane = savedInstanceState.getInt(Constants.StateKeys.CAR_LANE),
            crashes = savedInstanceState.getInt(Constants.StateKeys.CRASHES),
            obstaclesState = savedInstanceState.getBooleanArray(Constants.StateKeys.OBSTACLES)
        )
    }

    override fun onStart() {
        super.onStart()
        startTicking()
    }

    override fun onStop() {
        super.onStop()
        stopTicking()
    }

    private fun startTicking() {
        // never schedule the same Runnable twice
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, Constants.Timer.DELAY)
    }

    private fun stopTicking() {
        handler.removeCallbacks(runnable)
    }

    private fun initViews() {
        main_FAB_left.setOnClickListener { moveCar(left = true) }
        main_FAB_right.setOnClickListener { moveCar(left = false) }
        refreshUI()
    }

    private fun moveCar(left: Boolean) {
        if (left)
            gameManager.moveLeft()
        else
            gameManager.moveRight()
        refreshUI()
    }

    private fun gameTick() {
        val crashed = gameManager.tick()
        if (crashed)
            onCrash()
        refreshUI()
    }

    private fun onCrash() {
        vibrate()
        if (gameManager.isGameOver) { // Lost all lives -> endless game, start over:
            Log.d(Constants.Logging.GAME_STATUS, "Game Over!")
            toast(R.string.game_over)
            gameManager.newGame()
        } else {
            Log.d(Constants.Logging.GAME_STATUS, "Crash! lives left: ${gameManager.livesLeft}")
            toast(R.string.crash)
        }
    }

    private fun refreshUI() {
        // obstacles:
        main_IMG_obstacles.forEachIndexed { row, lanes ->
            lanes.forEachIndexed { lane, cell ->
                cell.visibility = if (gameManager.hasObstacle(row, lane))
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
        }
        // car:
        main_IMG_cars.forEachIndexed { lane, car ->
            car.visibility = if (lane == gameManager.carLane)
                View.VISIBLE
            else
                View.INVISIBLE
        }
        // hearts:
        main_IMG_hearts.forEachIndexed { index, heart ->
            heart.visibility = if (index < gameManager.livesLeft)
                View.VISIBLE
            else
                View.INVISIBLE
        }
    }

    private fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        else
            getSystemService(VIBRATOR_SERVICE) as Vibrator

        vibrator.vibrate(
            VibrationEffect.createOneShot(
                Constants.Timer.VIBRATION_DURATION,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    private fun findViews() {
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_obstacles = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_obstacle00),
                findViewById(R.id.main_IMG_obstacle01),
                findViewById(R.id.main_IMG_obstacle02)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle10),
                findViewById(R.id.main_IMG_obstacle11),
                findViewById(R.id.main_IMG_obstacle12)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle20),
                findViewById(R.id.main_IMG_obstacle21),
                findViewById(R.id.main_IMG_obstacle22)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle30),
                findViewById(R.id.main_IMG_obstacle31),
                findViewById(R.id.main_IMG_obstacle32)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle40),
                findViewById(R.id.main_IMG_obstacle41),
                findViewById(R.id.main_IMG_obstacle42)
            )
        )
        main_IMG_cars = arrayOf(
            findViewById(R.id.main_IMG_car0),
            findViewById(R.id.main_IMG_car1),
            findViewById(R.id.main_IMG_car2)
        )
        main_FAB_left = findViewById(R.id.main_FAB_left)
        main_FAB_right = findViewById(R.id.main_FAB_right)
    }
}
