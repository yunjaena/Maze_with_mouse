public enum Direction {

    EAST(1, 0),
    WEST(-1, 0),
    NORTH(1, 0), SOUTH(-1, 0);
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
}
