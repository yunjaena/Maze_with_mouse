import java.util.Stack;
import java.util.ArrayList;

public class Algorithm {
    private int startX;
    private int startY;
    private int nextX = 1, nextY = 0;
    private Maze maze;
    private Event mouseEvent;
    private Stack<Point> Branch;
    private Stack<Point> Route;
    private ArrayList<Integer> Distance;

    public Algorithm(Maze maze, Event event) {
        this.maze = maze;
        mouseEvent = event;
        Branch = new Stack<>(); // 분기점을 저장하는 스택
        Route = new Stack<>(); // 지나온 경로를 저장하는 스택, 최종적으로는 최단 경로 저장
        Distance = new ArrayList<>(); // 막다른 길로부터 가장 최근 분기까지의 거리를 저장
    }

    public boolean Solve() {
        startX = 1;
        startY = 0;
        System.out.println(Math.sqrt(Math.sqrt(maze.getxSize()*maze.getySize())));
        boolean result = false;
        if (MoveTo(startX, startY, Direction.NORTH))
            result = true;
        else if (MoveTo(startX, startY, Direction.SOUTH))
            result = true;
        else if (MoveTo(startX, startY, Direction.EAST))
            result = true;
        else if (MoveTo(startX, startY, Direction.WEST))
            result = true;

        return result;
    }


    private boolean MoveTo(int x, int y, Direction direction) {
        //에너지 소진시 쥐 사망
        if (mouseEvent.isMouseDIe()) return true;
        //경로에 저장 후 쥐 이동
        Route.push(new Point(x, y));
        mouseEvent.moveMouse(x, y);
        if(!teleportPossible() || !isEndMaze(x,y)) {
            mouseEvent.increaseMana();
            mouseEvent.decreaseEnergy();
        }
        //분기점이라면 분기 스택에도 저장
        if (isBranch(x, y)) {
            Branch.push(new Point(x, y));
        }
        //출구 발견 시 종료
        if ((x != startX && y != startY) && (y == 0) || (x == 0) || (x == maze.getxSize() - 1) || (y == maze.getySize())) {
            maze.getMaze()[y][x] = 2;
            return true;
        }
        //지나온 길 표시
        maze.getMaze()[y][x] = 2;
        //막다른 길 도달 시 행동
        if (isEndMaze(x, y)) {
            int d = 0;
            boolean isEndOfMaze = true;
            //현 시점에서 텔레포트 사용 여부 결정
            boolean usingTeleport = teleportPossible();
            System.out.println(getDistance());
            //막다른 길의 마지막 칸을 버리고 3(막다른 길)로 표시
            Route.pop();
            maze.getMaze()[y][x] = 3;
            //경로 스택의 내용을 뽑아가며 분기점까지 되돌아감
            while (true) {
                //막다른 길에서 최근 분기까지의 거리 체크
                if(isEndOfMaze) d++;
                int rx = Route.peek().getX();
                int ry = Route.pop().getY();
                //텔레포트를 사용한다면 쥐는 움직이지 않음
                if(!usingTeleport) {
                    if (mouseEvent.isMouseDIe()) return true;
                    mouseEvent.moveMouse(rx, ry);
                    mouseEvent.increaseMana();
                    mouseEvent.decreaseEnergy();
                }
                //분기점 도달시 분기 유효성 검사 실시
                if (!Branch.empty() && (rx == Branch.peek().getX() && ry == Branch.peek().getY())) {
                    //분기 폐쇄(유효하지 않은 분기의 경우)
                    if (deleteBranch(rx, ry)) {
                        Branch.pop();
                        maze.getMaze()[ry][rx] = 3;
                        isEndOfMaze = false;
                        //현재 분기가 폐쇄됬으므로 현재 분기를 제외한 가장 최근의 분기를 향해 되돌아감
                        continue;
                    }
                    //분기 유지(유효한 분기의 경우)
                    else {
                        Route.push(new Point(rx, ry));
                        maze.getMaze()[ry][rx] = 2;
                    }
                    //텔레포트 사용
                    if(usingTeleport){
                        mouseEvent.useMana(rx,ry);
                        mouseEvent.increaseMana();
                        mouseEvent.decreaseEnergy();
                    }
                    //막다른 길에서 가장 가까운 분기까지의 거리를 저장
                    if(d > 2) Distance.add(d);
                    break;
                }
                //분기가 아닌 길의 경우
                else {
                    maze.getMaze()[ry][rx] = 3;
                }
            }
        }

        for (Direction dir : Direction.values()) {
            if (!GetNextStep(x, y, dir)) {
                continue;
            }
            if (MoveTo(nextX, nextY, Direction.EAST)) {
                return true;
            }
        }
        return false;
    }

    //해당 좌표가 미로의 범위를 넘어섰는지 검사하는 메소드
    private boolean isNotOutOfArray(int x, int y) {
        if (x >= 0 && y >= 0 && x < maze.getxSize() && y < maze.getySize())
            return true;
        else
            return false;
    }

    //분기점인지 검사하는 메소드
    private boolean isBranch(int x, int y) {
        int[][] direction = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int count = 0;
        for (int i = 0; i < direction.length; i++) {
            if (!isNotOutOfArray(x + direction[i][1], y + direction[i][0]) ||
                    (maze.getMaze()[y + direction[i][0]][x + direction[i][1]] == 1)) count++;
        }
        if (count > 1) return false;
        else return true;
    }

    //막다른 길인지 검사하는 메소드
    private boolean isEndMaze(int x, int y) {
        int[][] direction = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int count = 0;
        if (x == 1 && y == 0) return false;
        for (int i = 0; i < direction.length; i++) {
            if (!isNotOutOfArray(x + direction[i][0], y + direction[i][1]) || maze.getMaze()[y + direction[i][1]][x + direction[i][0]] == 1)
                count++;
        }
        if (count >= 3) return true;
        else return false;
    }

    //분기 폐쇄 여부를 결정하는 메소드
    private boolean deleteBranch(int x, int y) {
        int[][] direction = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int count = 0;
        for (int i = 0; i < direction.length; i++) {
            if (isNotOutOfArray(x + direction[i][1], y + direction[i][0])) {
                if (maze.getMaze()[y + direction[i][0]][x + direction[i][1]] == 2 || maze.getMaze()[y + direction[i][0]][x + direction[i][1]] == 0)
                    count++;
            }
        }
        if (count <= 1) return true;
        else return false;
    }

    //텔레포트 가능 여부를 반환하는 메소드
    private boolean teleportPossible() {
        double threshold = Math.sqrt(Math.sqrt(maze.getxSize()*maze.getySize()));
        for(int i = 0; i <= Distance.size() - 1; i++){
            threshold += Distance.get(i);
        }
        threshold /= (Distance.size() + 1);
        if(mouseEvent.getMana() >= Mouse.USING_MANA && !Branch.empty()){
            //여분의 텔레포트 마나가 있으면 2보다 긴 거리에서 사용
            if(mouseEvent.getMana() >= (Mouse.USING_MANA *2)){
                if(getDistance() > 2) return true;
                else return false;
            }
            //한 번 사용할 마나만 존재하면 기준점보다 긴 거리에서만 사용
            else {
                if (getDistance() > threshold) return true;
                else return false;
            }
        }
        else return false;
    }

    //막다른 길에서 가장 가까운 유효분기까지의 거리를 반환하는 함수
    private int getDistance() {
        int d = 0, count;
        int[][] direction = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int i = Branch.size() - 1; i > -1; i--) {
            for (int j = Route.size() - 1 - d; j > -1; j--) {
                if ((Route.get(j).getX() == Branch.get(i).getX()) && (Route.get(j).getY() == Branch.get(i).getY())) {
                    d = (Route.size() - j);
                    break;
                }
            }
            count = 0;
            for (int k = 0; k < direction.length; k++) {
                if (isNotOutOfArray(Branch.get(i).getX() + direction[k][0], Branch.get(i).getY() + direction[k][0])) {

                    //지나가지 않은 길이 하나라도 존재하면 무조건 유효한 분기
                    if (maze.getMaze()[Branch.get(i).getY() + direction[k][0]][Branch.get(i).getX() + direction[k][1]] == 0){
                        count = -1;
                        break;
                    }
                    //해당 분기가 무효한 분기(사방에 지나간 길이 2개)인지 검사
                    //막다른 길에서 판단하기 때문에 지나간 길이 1개가 아니라 2개일 때 무효분기
                    //원래라면 되돌아가면서 지나간 길 한 곳을 막다른 길(3)으로 바꾸기 때문
                    else if(maze.getMaze()[Branch.get(i).getY() + direction[k][0]][Branch.get(i).getX() + direction[k][1]] == 2){
                        count++;
                    }
                }
            }
            if(count != 2) break;
        }
        return d;
    }

    private boolean GetNextStep(int x, int y, Direction direction) {
        switch (direction) {
            case NORTH:
                if (!isNotOutOfArray(x, y - 1)) return false;
                else if (maze.getMaze()[y - 1][x] >= 1) return false;
                nextX = x;
                nextY = y - 1;
                break;
            case SOUTH:
                if (!isNotOutOfArray(x, y + 1)) return false;
                else if (maze.getMaze()[y + 1][x] >= 1) return false;
                nextX = x;
                nextY = y + 1;
                break;
            case WEST:
                if (!isNotOutOfArray(x - 1, y)) return false;
                else if (maze.getMaze()[y][x - 1] >= 1) return false;
                nextX = x - 1;
                nextY = y;
                break;
            case EAST:
                if (!isNotOutOfArray(x + 1, y)) return false;
                else if (maze.getMaze()[y][x + 1] >= 1) return false;
                nextX = x + 1;
                nextY = y;
                break;

        }

        // 다음가는 곳이 벽이라면?
        if (maze.getMaze()[nextY][nextX] == 1) {
            return false;
        } else {
            return true;
        }

    }
}
