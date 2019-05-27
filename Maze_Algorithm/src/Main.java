public class Main {
    private static Mouse mouse;
    private static Maze maze;
    public static void main(String[] args) {
        maze = new Maze("Maze1.txt");
        mouse = new Mouse(maze);
        maze.printMaze();
        mouse.printMouseState();
    }
}
