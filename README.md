# c-gol
Competitive Game of Life (the acronym is pronounced "seagull")

### How it works:
  - The game board is a simulation of Conway's Game of Life.
  - The game starts with a number of cells pre-filled
  - Your objective is to place your cells such that after a number of simulation steps the number of filled cells is less than some threshold.

### Completed so far:
  - Game logic to handle state transitions
  - Display logic
  - Player interaction

### To-do (roughly in order from top to bottom):
  - Add in counters for...
    - The number of remaining generations
    - The target number of cells on the board
    - The current number of cells on the board
  - Create save states (so players can make boards and play on them)
  - Add game modes (unsure exactly what this means yet; distant to-do goal)