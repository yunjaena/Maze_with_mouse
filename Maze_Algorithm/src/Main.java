import generate_maze.MazeGenerator;

public class Main {
    private static Mouse mouse;
    private static Maze maze;
    private static final String FILE_NAME = "Maze1.txt";
    private static final int PRINT_TYPE = Print.TYPE_TWO; //콘솔 출력 이상이 타입을 바꿔주세요(TYPE_ONE, TYPE_TWO)
    private static final int MAX_X = 50;
    private static final int MAX_Y = 50;

    public static void main(String[] args) throws Exception {
        //MazeGenerator.saveMaze(FILE_NAME,MAX_X,MAX_Y);
        maze = new Maze(FILE_NAME, PRINT_TYPE);
        maze.printMaze();
        mouse = new Mouse(maze);
        maze.printResult();
        mouse.printMouseState();
        maze.writeMaze(mouse.getEnergy(), mouse.getCount(), mouse.getMana(), mouse.isMouseDIe());
        maze.writeRoute(mouse.getAlgorithm().getPath(), mouse.getAlgorithm().getTeleportSpot());
    }
}
