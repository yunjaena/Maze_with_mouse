public class Algorithm {
    private int startX;
    private int startY;
    private int nextX = 1, nextY = 0 ;
    private Maze maze;
    public Algorithm(Maze maze){
        this.maze = maze;
    }
    public boolean Solve(){
        startX=0;
        startY=0;

        boolean result = false;
        if(MoveTo( startX, startY, Direction.NORTH))
            result  = true;
        else if(MoveTo(startX, startY, Direction.SOUTH))
            result  = true;
        else if(MoveTo( startX, startY, Direction.EAST))
            result  = true;
        else if(MoveTo( startX, startY, Direction.SOUTH))
            result  = true;

        return result;
    }

    private boolean MoveTo( int x, int y, Direction direction){
        int i=0;

        if((x != startX && y != startY) && y == 0 || x == 0 || y == maze.getxSize() || x == maze.getySize())
            return true;

        maze.getMaze()[y][x] = 2;
        for(Direction dir: Direction.values()) {
            if(!GetNextStep(x,y,dir,nextX,nextY))
                continue;
            if(MoveTo(nextX, nextY, Direction.NORTH))
                return true;
        }
        return false;
    }
    private boolean GetNextStep(int x, int y, Direction direction, int nextX, int nextY){
        switch (direction){
            case NORTH:
                nextX = x;
                nextY = y - 1;
                if(nextY < 0)  return false;
                break;
            case SOUTH:
                nextX = x;
                nextY = y + 1;
                if(nextY > this.maze.getxSize()) return false;
                break;
            case WEST:
                nextX = x - 1;
                nextY = y;
                if(nextX < 0) return false;
                break;
            case EAST:
                nextX = x + 1;
                nextY = y;
                if(nextX > maze.getxSize()) return false;
                break;

        }
        // 다음가는 곳이 벽이라면?
        if(maze.getMaze()[nextY][nextX] == 1){
            return false;
        }
        else {
            return true;
        }
    }
}
