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
  - Custom number display to be used later
  - Animation timer (the game can now "run" without having to mash a button)

### To-do (roughly in order from top to bottom):
  - Add in counters for...
    - The number of remaining generations
    - The target number of cells on the board
    - The current number of cells on the board
  - Create save states (so players can make boards and play on them)
  - Add game modes (unsure exactly what this means yet; distant to-do goal)

### How to run this game from IntelliJ IDEA:
  - You must have JavaFX installed, I believe that comes with IDEA.
  - Right click src/main/java/com/vanityblade/cgol/CGoLGame -> Run CGoLGame.main()
    - Alternatively, the package name containing the relevant file is com.vanityblade.cgol
