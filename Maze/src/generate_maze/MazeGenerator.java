package generate_maze;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.IntConsumer;

/**
 * Maze generators.
 *
 * @author Armin Reichert
 */
public class MazeGenerator {

    enum Algorithm {
        RANDOM_DFS_RECURSIVE,
        RANDOM_DFS_NONRECURSIVE,
        RANDOM_BFS,
        PRIM,
        RECURSIVE_DIVISION,
        ALDOUS_BRODER,
        WILSON,
        SIDEWINDER,
        BINARY_TREE
    }

    /** Returns a maze of the given size starting generation at the given grid position. */
    static Grid maze(int numCols, int numRows, int startCol, int startRow, Algorithm algorithm) {
        Grid grid = new Grid(numCols, numRows);
        int startVertex = grid.vertex(startCol, startRow);
        BitSet visited = new BitSet(numCols * numRows);
        switch (algorithm) {
            case RANDOM_BFS:
                randomBFS(grid, startVertex, visited);
                break;
            case RANDOM_DFS_NONRECURSIVE:
                randomDFSNonrecursive(grid, startVertex, visited);
                break;
            case RANDOM_DFS_RECURSIVE:
                randomDFSRecursive(grid, startVertex, visited);
                break;
            case PRIM:
                prim(grid, startVertex, visited);
                break;
            case RECURSIVE_DIVISION:
                recursiveDivision(grid);
                break;
            case ALDOUS_BRODER:
                aldousBroder(grid, startVertex, visited);
                break;
            case WILSON:
                wilson(grid);
                break;
            case SIDEWINDER:
                sidewinder(grid);
                break;
            case BINARY_TREE:
                binaryTree(grid);
                break;
            default:
                break;
        }
        return grid;
    }

    // Randomized Depth-First Search (recursive)

    static void randomDFSRecursive(Grid grid, int v, BitSet visited) {
        visited.set(v);
        for (Direction dir = unvisitedDir(grid, v, visited); dir != null; dir = unvisitedDir(grid, v, visited)) {
            grid.addEdge(v, dir);
            randomDFSRecursive(grid, grid.neighbor(v, dir), visited);
        }
    }

    // Randomized Depth-First Search (non-recursive)

    static void randomDFSNonrecursive(Grid grid, int v, BitSet visited) {
        Deque<Integer> stack = new ArrayDeque<>();
        visited.set(v);
        stack.push(v);
        while (!stack.isEmpty()) {
            Direction dir = unvisitedDir(grid, v, visited);
            if (dir != null) {
                int neighbor = grid.neighbor(v, dir);
                grid.addEdge(v, dir);
                visited.set(neighbor);
                stack.push(neighbor);
                v = neighbor;
            }
            else {
                v = stack.pop();
            }
        }
    }

    // Randomized Breadth-First Search

    static void randomBFS(Grid grid, int v, BitSet visited) {
        List<Integer> frontier = new ArrayList<>();
        visited.set(v);
        frontier.add(v);
        Random rnd = new Random();
        while (!frontier.isEmpty()) {
            v = frontier.remove(rnd.nextInt(frontier.size()));
            for (Direction dir : unvisitedDirections(grid, v, visited)) {
                int neighbor = grid.neighbor(v, dir);
                grid.addEdge(v, dir);
                visited.set(neighbor);
                frontier.add(neighbor);
            }
        }
    }

    // Prim's MST algorithm

    static class WeightedEdge {

        int v;
        Direction dir;
        int weight;

        public WeightedEdge(int v, Direction dir, int weight) {
            this.v = v;
            this.dir = dir;
            this.weight = weight;
        }
    }

    public static void saveMaze(String filename, int x, int y)  throws Exception{
        String mazeString = "";
        StringBuilder sb = new StringBuilder();
        for (Algorithm algorithm : Algorithm.values()) {
            mazeString = maze(x/2, y/2, 0, 0, algorithm).toString();
        }

        for (int i = 0; i < mazeString.length(); i++) {
            char c = mazeString.charAt(i);
            if(i==1) c = '0';
            if(i == mazeString.length()-3) c = '0';
            sb.append(c);
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            for(int i = 0 ; i< sb.toString().length() ; i++) {
                char c = sb.toString().charAt(i);
                if(c=='0' || c=='1')
                out.write(c+" ");
                if(c =='\n')
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.err.println(e); // 에러가 있다면 메시지 출력
            System.exit(1);
        }


        System.out.println(sb.toString());
    }

    static void prim(Grid grid, int v, BitSet visited) {
        PriorityQueue<WeightedEdge> pq = new PriorityQueue<>((e1, e2) -> Integer.compare(e1.weight, e2.weight));
        Random rnd = new Random();
        IntConsumer fnExpand = vertex -> {
            visited.set(vertex);
            for (Direction dir : unvisitedDirections(grid, vertex, visited)) {
                pq.add(new WeightedEdge(vertex, dir, rnd.nextInt(Integer.MAX_VALUE)));
            }
        };
        fnExpand.accept(v);
        while (!pq.isEmpty()) {
            WeightedEdge edge = pq.poll();
            int neighbor = grid.neighbor(edge.v, edge.dir);
            if (!visited.get(neighbor)) {
                grid.addEdge(edge.v, edge.dir);
                fnExpand.accept(neighbor);
            }
        }
    }

    // Recursive division

    static void recursiveDivision(Grid grid) {
        for (int row = 0; row < grid.numRows; ++row) {
            for (int col = 0; col < grid.numCols; ++col) {
                int vertex = grid.vertex(col, row);
                if (row > 0) {
                    grid.addEdge(vertex, Direction.NORTH);
                }
                if (col > 0) {
                    grid.addEdge(vertex, Direction.WEST);
                }
            }
        }
        divide(grid, new Random(), 0, 0, grid.numCols, grid.numRows);
    }

    static void divide(Grid grid, Random rnd, int x0, int y0, int w, int h) {
        if (w <= 1 && h <= 1) {
            return;
        }
        if (w < h || (w == h && rnd.nextBoolean())) {
            // Build "horizontal wall" at random y from [y0 + 1, y0 + h - 1], keep random door
            int y = y0 + 1 + rnd.nextInt(h - 1);
            int door = x0 + rnd.nextInt(w);
            for (int x = x0; x < x0 + w; ++x) {
                if (x != door) {
                    grid.removeEdge(grid.vertex(x, y - 1), Direction.SOUTH);
                }
            }
            divide(grid, rnd, x0, y0, w, y - y0);
            divide(grid, rnd, x0, y, w, h - (y - y0));
        }
        else {
            // Build "vertical wall" at random x from [x0 + 1, x0 + w - 1], keep random door
            int x = x0 + 1 + rnd.nextInt(w - 1);
            int door = y0 + rnd.nextInt(h);
            for (int y = y0; y < y0 + h; ++y) {
                if (y != door) {
                    grid.removeEdge(grid.vertex(x - 1, y), Direction.EAST);
                }
            }
            divide(grid, rnd, x0, y0, x - x0, h);
            divide(grid, rnd, x, y0, w - (x - x0), h);
        }
    }

    // Aldous/Broder algorithm

    static void aldousBroder(Grid grid, int v, BitSet visited) {
        visited.set(v);
        while (visited.cardinality() < grid.numCols * grid.numRows) {
            Direction dir = Direction.random();
            int neighbor = grid.neighbor(v, dir);
            if (neighbor != Grid.NO_VERTEX && !visited.get(neighbor)) {
                grid.addEdge(v, dir);
                visited.set(neighbor);
            }
            v = neighbor;
        }
    }

    // Wilson's algorithm

    static void wilson(Grid grid) {
        int numVertices = grid.numCols * grid.numRows;
        BitSet tree = new BitSet(numVertices);
        tree.set(new Random().nextInt(numVertices));
        Map<Integer, Direction> lastWalkDir = new HashMap<>();
        for (int v = 0; v < numVertices; ++v) {
            loopErasedRandomWalk(grid, v, lastWalkDir, tree);
        }
    }

    static void loopErasedRandomWalk(Grid grid, int start, Map<Integer, Direction> lastWalkDir, BitSet tree) {
        // do random walk until a tree vertex is reached
        int v = start;
        while (!tree.get(v)) {
            Direction dir = Direction.random();
            int neighbor = grid.neighbor(v, dir);
            if (neighbor != Grid.NO_VERTEX) {
                lastWalkDir.put(v, dir);
                v = neighbor;
            }
        }
        // add the (loop-erased) random walk to the tree
        v = start;
        while (!tree.get(v)) {
            Direction dir = lastWalkDir.get(v);
            int neighbor = grid.neighbor(v, dir);
            if (neighbor != Grid.NO_VERTEX) {
                tree.set(v);
                grid.addEdge(v, dir);
                v = neighbor;
            }
        }
    }

    // Sidewinder algorithm

    static void sidewinder(Grid grid) {
        Random rnd = new Random();
        for (int col = 0; col < grid.numCols - 1; ++col) {
            grid.addEdge(grid.vertex(col, 0), Direction.EAST);
        }
        for (int row = 1; row < grid.numRows; ++row) {
            int horizPassageStart = 0;
            for (int col = 0; col < grid.numCols; ++col) {
                if (col == grid.numCols - 1 || rnd.nextBoolean()) {
                    int x = horizPassageStart + rnd.nextInt(col - horizPassageStart + 1);
                    grid.addEdge(grid.vertex(x, row), Direction.NORTH);
                    horizPassageStart = col + 1;
                }
                else {
                    grid.addEdge(grid.vertex(col, row), Direction.EAST);
                }
            }
        }
    }

    // Binary tree algorithm

    static void binaryTree(Grid grid) {
        Direction[] dirs = { Direction.EAST, Direction.SOUTH };
        Random rnd = new Random();
        for (int v = 0; v < grid.numCols * grid.numRows; ++v) {
            int choice = rnd.nextInt(2);
            int neighbor = grid.neighbor(v, dirs[choice]);
            if (neighbor != Grid.NO_VERTEX) {
                grid.addEdge(v, dirs[choice]);
            }
            else {
                neighbor = grid.neighbor(v, dirs[1 - choice]);
                if (neighbor != Grid.NO_VERTEX) {
                    grid.addEdge(v, dirs[1 - choice]);
                }
            }
        }
    }

    /** Returns directions to unvisited neighbors of {@code v} in random order. */
    static List<Direction> unvisitedDirections(Grid grid, int v, BitSet visited) {
        List<Direction> candidates = new ArrayList<>(4);
        for (Direction dir : Direction.values()) {
            int neighbor = grid.neighbor(v, dir);
            if (neighbor != Grid.NO_VERTEX && !visited.get(neighbor)) {
                candidates.add(dir);
            }
        }
        Collections.shuffle(candidates);
        return candidates;
    }

    /** Returns direction to some unvisited neighbor or {@code null} if no such neighbor exists. */
    static Direction unvisitedDir(Grid grid, int v, BitSet visited) {
        List<Direction> unvisitedDirections = unvisitedDirections(grid, v, visited);
        return unvisitedDirections.isEmpty() ? null : unvisitedDirections.get(0);
    }
}