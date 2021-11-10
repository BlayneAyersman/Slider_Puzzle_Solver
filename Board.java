/******************************************************************************
 *  Author: Blayne Ayersman
 *  Last Edit Date: 9/15/2021
 *
 *  Dependencies: Stack.java
 *
 *  Description: This class models an n-by-n puzzle board with sliding tiles
 *
 ******************************************************************************/

import java.util.Arrays;

// API for an N x N size slider puzzle board state
public class Board {
    private final int[][] tiles;                                                // N x N grid of puzzle tiles
    private final int N;                                                        // Stores dimension (N) of puzzle board

    // Creates a board from an N x N array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        N = tiles.length;                                                       // Store dimension of passed tiles array
        this.tiles = new int[N][N];                                             // Instantiate instance grid of N x N size
        for (int i = 0; i < N; i++)
            this.tiles[i] = tiles[i].clone();                                   // Copy passed grid to instance grid
    }

    // Returns string representation of the board tiles
    public String toString() {
        StringBuilder s = new StringBuilder();                                  // Initialize new string builder
        s.append(N + "\n");                                                     // Append board dimension to string

        for (int[] row : tiles) {
            for (int tile : row)
                s.append(String.format("%2d ", tile));                          // Append formatted board tiles string
            s.append("\n");                                                     // Append new line to string
        }
        return s.toString();                                                    // Return string representation of board
    }

    // Returns dimension of the board
    public int dimension() {
        return N;                                                               // Return Board instance variable for dimension size
    }

    // Returns the number of tiles out of place (Hamming distance)
    public int hamming() {
        int numIncorrect = -1;
        int index = 1;

        for (int[] row : tiles) {
            for (int tile : row) {
                if (tile != index)
                    numIncorrect++;
                index++;
            }
        }
        return numIncorrect;
    }

    // Returns the sum of move distances between each tile and its correct position (Manhattan distance)
    public int manhattan() {
        int sum = 0;
        int index = 0;

        for (int row = 0; row < N; row++)
            for (int col = 0; col < N; col++) {
                int correctIdx = tiles[row][col] - 1;

                if (correctIdx != -1 && correctIdx != index) {
                    sum += Math.abs((correctIdx / N) - row);
                    sum += Math.abs((correctIdx % N) - col);
                }
                index++;
            }
        return sum;
    }

    // Return whether this board is the goal board
    public boolean isGoal() {
        return hamming() == 0;                                                  // Board is solved if hamming distance is 0
    }

    // Return whether passed object is equivalent to this board
    public boolean equals(Object obj) {
        if (obj == null) return false;                                          // Validate that the passed object is not null
        if (this.getClass() != obj.getClass()) return false;                    // Validate that passed object is of Board Class

        Board that = (Board) obj;
        return Arrays.deepEquals(this.tiles, that.tiles);                       // Check that the tile grids of the instance board and passed board are identical
    }

    // Return an iterable list of neighboring board states
    public Iterable<Board> neighbors() {
        int emptyRow = -1;                                                      // Row of the empty tile
        int emptyCol = -1;                                                      // Column of the empty tile

        // Iterate through each tile in the board, searching for empty tile
        search:
        for (int row = 0; row < N; row++)                                       // For each row in the board
            for (int col = 0; col < N; col++)                                   // For each column in the row
                if (tiles[row][col] == 0) {                                     // If the current tile is empty (value of 0)
                    emptyRow = row;                                             // Store the row of the empty tile
                    emptyCol = col;                                             // Store the column of the empty tile
                    break search;                                               // Break from search
                }

        Stack<Board> neighbors = new Stack<Board>();                            // Initialize a stack of neighboring board states

        if (emptyRow - 1 >= 0)                                                  // If there is a row of tiles above the empty tile
            neighbors.push(swap(emptyRow, emptyCol, emptyRow - 1, emptyCol));   // Swap empty tile with the above adjacent tile and add to neighbors stack

        if (emptyRow + 1 < N)                                                   // If there is a row of tiles below the empty tile
            neighbors.push(swap(emptyRow, emptyCol, emptyRow + 1, emptyCol));   // Swap empty tile with the below adjacent tile and add to neighbors stack

        if (emptyCol - 1 >= 0)                                                  // If there is a column of tiles to the left of the empty tile
            neighbors.push(swap(emptyRow, emptyCol, emptyRow, emptyCol - 1));   // Swap empty tile with the left adjacent tile and add to neighbors stack

        if (emptyCol + 1 < N)                                                   // If there is a column of tiles to the right of the empty tile
            neighbors.push(swap(emptyRow, emptyCol, emptyRow, emptyCol + 1));   // Swap empty tile with the right adjacent tile and add to neighbors stack

        return neighbors;                                                       // Return iterable stack of neighboring board states
    }

    // Returns new board state in which the two passed tiles are swapped in the current board
    private Board swap(int row1, int col1, int row2, int col2) {
        int[][] copy = new int[N][N];                                           // Instantiate a new N x N tile grid to avoid mutating the instance tile grid
        for (int row = 0; row < N; row++)
            copy[row] = tiles[row].clone();                                     // Copy each row of the instance grid to the copy grid

        // Swap tiles at the row/column indeces passed to the method
        int temp = copy[row1][col1];
        copy[row1][col1] = copy[row2][col2];
        copy[row2][col2] = temp;

        return new Board(copy);                                                 // Return new board containing swapped tiles
    }

    // Return a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int row1 = -1;                                                          // Row value of first tile to be exchanged
        int col1 = -1;                                                          // Column value of first tile to be exchanged
        int row2 = -1;                                                          // Row value of second tile to be exchanged
        int col2 = -1;                                                          // Column value of second tile to be exchanged
        int count = 0;                                                          // Number of non-empty tiles found to exchange

        search:
        for (int i = 0; i < N; i++)                                             // For each row in tiles grid
            for (int j = 0; j < N; j++) {                                       // For each tile space in row
                if (tiles[i][j] != 0) {                                         // If space contains a tile
                    if (count == 0) {                                           // If no tiles have been found yet
                        row1 = i;                                               // Set row value of first tile to be exchanged
                        col1 = j;                                               // Set column value of first tile to be exchanged
                        count++;
                    }
                    else if (count == 1) {                                      // Otherwise, if one tile has already been found to be exchanged
                        row2 = i;                                               // Set row value of second tile to be exchanged
                        col2 = j;                                               // Set column value of second tile to be exchanged
                        break search;                                           // End search
                    }
                }
            }
        return swap(row1, col1, row2, col2);                                    // Exchange the two located tiles by passing their row/col values to the swap method and return the resulting board
    }
}
