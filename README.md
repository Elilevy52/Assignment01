# Assignment01 - Obstacle Race

An endless obstacle-race game for Android (Kotlin, XML Views).

## Gameplay

- Three-lane road
- A car that moves left and right (two floating action buttons)
- Obstacles (rocks) come down the road at a constant speed
- Crash notification: Toast message + vibration
- 3 lives (hearts at the top right)
- Endless game: after losing all 3 lives the game starts again with 3 new lives

## How it works

- **No Canvas, no x/y movement.** The road is a fixed grid of `AppCompatImageView` cells
  (5 obstacle rows + 1 car row in each of the 3 lanes). Moving an obstacle or the car
  means switching cells between `VISIBLE` and `INVISIBLE`.
- **Code layering:**
  - `logic/GameManager` - the game state (obstacle matrix, car lane, crashes) and rules,
    with no Android view code.
  - `MainActivity` - finds the views, handles the buttons, runs the game loop and draws
    the `GameManager` state on the grid.
  - `utilities/Constants` - all constants (grid size, timer delay, state keys, log tags).
- **Game loop:** `Handler` + `Runnable` on the main looper, started in `onStart` and
  stopped in `onStop`.
- **Rotation:** the game state is saved in `onSaveInstanceState` and restored in `onCreate`.
- **Resources:** strings, dimens and colors in resource files, Hebrew strings
  (`values-iw`) and night-mode colors (`values-night`).

## Libraries

Only the default Android Studio project libraries: `core-ktx`, `appcompat`,
`activity-ktx`, `constraintlayout`, `material`.

## Video

<!-- Add the link to the gameplay video here -->
