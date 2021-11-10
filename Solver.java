/******************************************************************************
 *  Author: Blayne Ayersman
 *  Last Edit Date: 9/15/2021
 *
 *  Compilation:  javac Solver.java
 *  Execution:    java Solver filename.txt
 *  Dependencies: Board.java
 *                In.java
 *                MinPQ.java
 *                StdIn.java
 *                StdOut.java
 *
 *  This program creates an initial board from the filename specified
 *  on the command line and outputs the minimum number of moves to
 *  reach the goal state, as well as each board state along the optimal solution path.
 ******************************************************************************/

// Solves a passed Slider Puzzle Board
public class Solver {
    private final Stack<Board> solution = new Stack<>();            // Stores all board states on the most efficient path from the initial board to its solved state
    private final int moves;                                        // Stores the minimum number of moves required to solve the initial slider puzzle board
    private final boolean solvable;                                 // Value representing whether or not the given board is solvable

    // Find a solution to the initial board using the A* algorithm
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();  // Validate that the board object is not null

        MinPQ<Node> minPQ = new MinPQ<>();                          // Initialize a priority queue to store search nodes of the initial board
        MinPQ<Node> minPQTwin = new MinPQ<>();                      // Initialize a priority queue to store search nodes of the intiial board's "twin"

        minPQ.insert(new Node(initial, null));                      // Insert new search node containing initial board into the priority queue
        minPQTwin.insert(new Node(initial.twin(), null));           // Insert new search node containing twin board into the twin queue

        Node minPriority;
        Node minPriorityTwin;
        do {                                                        // Until either the given board or its twin are sovled
            minPriority = minPQ.delMin();                           // Pull minimum priority node from minPQ
            minPriorityTwin = minPQTwin.delMin();                   // Pull minimum priority node from minPQTwin
            addNeighbors(minPriority, minPQ);
            addNeighbors(minPriorityTwin, minPQTwin);
        } while (!minPriority.board.isGoal() && !minPriorityTwin.board.isGoal());

        if (minPriority.board.isGoal()) {                           // If minPQ leads to a solved board state
            solvable = true;                                        // Initial board was solvable
            moves = minPriority.moves;                              // Record number of moves it took to solve

            while (minPriority != null) {                           // Push the board states from each search node in the tree to the solution stack
                solution.push(minPriority.board);
                minPriority = minPriority.previous;
            }
        }
        else {                                                      // If minPQTwin leads to a solved board state
            solvable = false;                                       // Initial board was not solvable
            moves = -1;                                             // Set moves to invalid value
        }
    }

    // Returns if the initial board is solvable
    public boolean isSolvable() {
        return solvable;
    }

    // Returns the minimum number of moves required to solve initial board
    public int moves() {
        return moves;
    }

    // Returns iterable sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (solvable)
            return solution;
        return null;
    }

    private void addNeighbors(Node minPriority, MinPQ<Node> minPQ) {
        for (Board board : minPriority.board.neighbors()) {         // For each board neighboring the minPriority board
            if (minPriority.previous != null)                       // If neighbor's previous node is not null
                if (board.equals(minPriority.previous.board))       // Check if the neighbor is equal to the minPriority board
                    continue;                                       // If so, do not insert the neighbor board into the queue
            minPQ.insert(new Node(board, minPriority));             // Otherwise, insert neighbor into the queue
        }
    }

    // Structure for Node objects to be used in A* search algorithm
    private class Node implements Comparable<Node> {
        public final Board board;                                   // The current board state
        public final Node previous;                                 // The previous board state
        public int manhattan;                                       // The manhattan distance of the board
        public int moves;                                           // The number of moves that have been made since the initial board

        public Node(Board board, Node previous) {
            this.board = board;
            this.previous = previous;
            this.manhattan = board.manhattan();
            moves = (previous == null) ? 0 : previous.moves + 1;    // Set number of moves to one higher than the previous node
        }

        /* Compare method returns positive if the instance search node is on a more efficient
           solution path than the passed search node based on their respective boards'
           manhattan distances and the number of moves that have already been made.
         */
        @Override
        public int compareTo(Node that) {
            // Establishes the node containing the board with the greater sum of manhattan distance
            // and prior moves made as the lesser node.
            return (this.manhattan + this.moves) - (that.manhattan + that.moves);
        }
    }

    // Test client
    public static void main(String[] args) {
        // Create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // Solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
