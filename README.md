# c-gol
Competitive Game of Life (the acronym is pronounced "seagull")

### How it works:
  - The game board is a simulation of Conway's Game of Life.
  - The game starts with a number of cells pre-filled
  - Your objective is to place your cells such that after a number of simulation steps the number of filled cells is less than some threshold.

### The game modes:
  - Create Mode: This is the mode the game is loaded into by default. You can place cells and step through the simulation freely.
  - Objective Mode: If a game board that was loaded has its objectives set, then this mode will be entered. In Objective Mode, you can only place cells before the first time the board updates. After that point, you will no longer be able to modify the board.

### How to run this game from IntelliJ IDEA:
  - You must have JavaFX installed, which I believe is packaged with IDEA.
  - Right click src/main/java/com/vanityblade/cgol/CGoLGame -> Run CGoLGame.main()
    - Alternatively, the package name containing the relevant file is com.vanityblade.cgol
  - I have been unsucessful in exporting the project to a standalone executable so far, when I manage to have that it will be included in this repository.
