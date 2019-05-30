import generate_maze.MazeGenerator;

public class Main {
    private static Mouse mouse;
    private static Maze maze;
    private static final String FILE_NAME = "Maze1.txt";
    private static final int MAX_X = 200;
    private static final int MAX_Y = 200;
    public static void main(String[] args) throws Exception {
        MazeGenerator.saveMaze(FILE_NAME,MAX_X,MAX_Y);
        maze = new Maze(FILE_NAME);
        maze.printMaze();
        mouse = new Mouse(maze);
        maze.printResult();
        mouse.printMouseState();
    }
}
