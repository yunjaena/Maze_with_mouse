public enum Direction {

    EAST(1, 0),
    NORTH(0, -1),
    SOUTH(0, 1),
    WEST(-1, 0);

    private int x;
    private int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getNextX(int px){ return px + x; }
    public int getNextY(int py){ return py + y; }

    public static Direction[] searchOrder(Maze maze, int x, int y){
        Direction[] order;
        if((maze.getGoal().getX() < x) && ((maze.getGoal().getY() < y))){
            order = new Direction[] {Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.EAST};
            return order;
        }
        else if((maze.getGoal().getX() >= x) && ((maze.getGoal().getY() < y))){
            order = new Direction[] {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};
            return order;
        }
        else if((maze.getGoal().getX() >= x) && ((maze.getGoal().getY() >= y))){
            order = new Direction[] {Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST};
            return order;
        }
        else{
            order = new Direction[] {Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST};
            return order;
        }
    }

}
