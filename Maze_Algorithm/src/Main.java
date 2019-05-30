public class Main {
    private static Mouse mouse;
    private static Maze maze;
    public static void main(String[] args) {
        maze = new Maze("Maze3.txt");
        maze.printMaze();
        mouse = new Mouse(maze);
        maze.printResult();
        mouse.printMouseState();
    }
}
