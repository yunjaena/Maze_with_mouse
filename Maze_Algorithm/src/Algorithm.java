import java.util.ArrayList;
import java.util.Stack;

public class Algorithm {
    private int startX, startY, nextX, nextY;
    private Maze maze;
    private Event mouseEvent;
    private Stack<Point> Branch, shortestPath, path, teleportSpot;
    private ArrayList<Integer> Distance;

    public Algorithm(Maze maze, Event event) {
        this.maze = maze;
        mouseEvent = event;
        Branch = new Stack<>(); // 분기점을 저장하는 스택
        shortestPath = new Stack<>(); // 최단 경로를 저장하는 스택
        path = new Stack<>(); // 지나온 경로를 저장하는 스택
        teleportSpot = new Stack<>(); // 텔레포트 지접을 저장
        Distance = new ArrayList<>(); // 막다른 길로부터 가장 최근 분기까지의 거리를 저장
    }

    public boolean Solve() {
        startX = 1;
        startY = 0;
        boolean result = false;
        if (MoveTo(startX, startY)) result = true;
        return result;
    }


    private boolean MoveTo(int x, int y) {
        //경로에 저장 후 쥐 이동
        shortestPath.push(new Point(x, y));
        path.push(new Point(x, y));
        //분기점이라면 분기 스택에도 저장
        if (isBranch(x, y)) Branch.push(new Point(x, y));
        if(maze.getLowBound() < y) maze.setLowBound(y);
        mouseEvent.moveMouse(x, y);
        mouseEvent.increaseMana();
        mouseEvent.decreaseEnergy();

        //출구 발견 시 종료
        if ((x != startX && y != startY) && (y == 0) || (x == 0) || (x == maze.getxSize() - 1) || (y == maze.getySize()-1)) {
            maze.getMaze()[y][x] = 2;
            return true;
        }
        //지나온 길 표시
        maze.getMaze()[y][x] = 2;
        //에너지 소진시 쥐 사망
        if (mouseEvent.isMouseDIe()){
            System.out.println("쥐가 죽었습니다");
            return true;
        }
        //막다른 길 도달 시 행동
        if (isEndMaze(x, y)) {
            int d = 0;
            //현 시점에서 텔레포트 사용 여부 결정
            boolean usingTeleport = teleportPossible();
            if(usingTeleport){
                teleportSpot.push(new Point(x,y));
                System.out.print(x + ", " + y);
            }

            //막다른 길의 마지막 칸을 버리고 3(막다른 길)로 표시
            shortestPath.pop();
            maze.getMaze()[y][x] = 3;
            //경로 스택의 내용을 뽑아가며 분기점까지 되돌아감
            while (true) {
                //막다른 길에서 최근 분기까지의 거리 체크
                d++;

                int rx = shortestPath.peek().getX();
                int ry = shortestPath.pop().getY();
                //텔레포트를 사용한다면 쥐는 움직이지 않음
                if(!usingTeleport) {
                    mouseEvent.moveMouse(rx, ry);
                    mouseEvent.increaseMana();
                    mouseEvent.decreaseEnergy();
                    //에너지 소진시 쥐 사망
                    if (mouseEvent.isMouseDIe()){
                        System.out.println("쥐가 죽었습니다");
                        return true;
                    }
                    //되돌아 오는 과정 경로에 저장
                    path.push(new Point(rx, ry));
                }
                //분기점 도달시 분기 유효성 검사 실시
                if (!Branch.empty() && (rx == Branch.peek().getX() && ry == Branch.peek().getY())) {
                    //분기 폐쇄(유효하지 않은 분기의 경우)
                    if (deleteBranch(rx, ry)) {
                        Branch.pop();
                        maze.getMaze()[ry][rx] = 3;
                        //현재 분기가 폐쇄됬으므로 현재 분기를 제외한 가장 최근의 분기를 향해 되돌아감
                        continue;
                    }
                    //분기 유지(유효한 분기의 경우)
                    else {
                        shortestPath.push(new Point(rx, ry));
                        maze.getMaze()[ry][rx] = 2;
                    }
                    //텔레포트 사용
                    if(usingTeleport){
                        mouseEvent.useMana(rx,ry);
                        teleportSpot.push(new Point(rx, ry));
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
        //다음 길 탐색
        for (Direction dir : Direction.searchOrder(maze, x, y)) {
            if (!GetNextStep(x, y, dir)) {
                continue;
            }
            if (MoveTo(nextX, nextY)) {
                return true;
            }
        }
        return false;
    }

    //해당 좌표가 미로의 범위를 넘어섰는지 검사하는 메소드
    private boolean isNotOutOfArray(int x, int y) {
        if (x >= 0 && y >= 0 && x < maze.getxSize() && y < maze.getySize()) return true;
        else return false;
    }

    //해당 좌표가 분기점인지 검사하는 메소드
    private boolean isBranch(int x, int y) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (!isNotOutOfArray(dir.getNextX(x), dir.getNextY(y)) || (maze.getMaze()[dir.getNextY(y)][dir.getNextX(x)] == 1)) count++;
        }
        if(count > 1) return false;
        else return true;
    }

    //해당 좌표가 막다른 길인지 검사하는 메소드
    private boolean isEndMaze(int x, int y) {
        int count = 0;
        if (x == 1 && y == 0) return false;
        for (Direction dir : Direction.values()) {
            if (!isNotOutOfArray(dir.getNextX(x), dir.getNextY(y)) || maze.getMaze()[dir.getNextY(y)][dir.getNextX(x)] == 1) count++;
        }
        if (count >= 3) return true;
        else return false;
    }

    //분기 폐쇄 여부를 결정하는 메소드
    private boolean deleteBranch(int x, int y) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (isNotOutOfArray(dir.getNextX(x), dir.getNextY(y))) {
                if (maze.getMaze()[dir.getNextY(y)][dir.getNextX(x)] == 2 || maze.getMaze()[dir.getNextY(y)][dir.getNextX(x)] == 0) count++;
            }
        }
        if (count <= 1) return true;
        else return false;
    }

    //텔레포트 가능 여부를 반환하는 메소드
    private boolean teleportPossible() {
        double threshold = Math.sqrt(Math.sqrt(maze.getxSize()*maze.getySize()));
        for(int i = 0; i <= Distance.size() - 1; i++) threshold += Distance.get(i);
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
        for (int i = Branch.size() - 1; i > -1; i--) {
            for (int j = shortestPath.size() - 1 - d; j > -1; j--) {
                if ((shortestPath.get(j).getX() == Branch.get(i).getX()) && (shortestPath.get(j).getY() == Branch.get(i).getY())) {
                    d = (shortestPath.size() - j);
                    break;
                }
            }
            count = 0;
            for (Direction dir : Direction.values()) {
                if (isNotOutOfArray(dir.getNextX(Branch.get(i).getX()), dir.getNextY(Branch.get(i).getY()))) {
                    //지나가지 않은 길이 하나라도 존재하면 무조건 유효한 분기
                    if (maze.getMaze()[dir.getNextY(Branch.get(i).getY())][dir.getNextX(Branch.get(i).getX())] == 0){
                        count = -1;
                        break;
                    }
                    //해당 분기가 무효한 분기(사방에 지나간 길이 2개)인지 검사
                    //막다른 길에서 판단하기 때문에 지나간 길이 1개가 아니라 2개일 때 무효분기
                    //원래라면 되돌아가면서 지나간 길 한 곳을 막다른 길(3)으로 바꾸기 때문
                    else if(maze.getMaze()[dir.getNextY(Branch.get(i).getY())][dir.getNextX(Branch.get(i).getX())] == 2) count++;
                }
            }
            if(count != 2) break;
        }
        return d;
    }

    //해당 좌표에서 해당방향으로 진행가능 여부를 반환
    private boolean GetNextStep(int x, int y, Direction dir) {
        if (!isNotOutOfArray(dir.getNextX(x), dir.getNextY(y))) return false;
        else if (maze.getMaze()[dir.getNextY(y)][dir.getNextX(x)] >= 1) return false;
        nextX = dir.getNextX(x);
        nextY = dir.getNextY(y);
        return true;
    }

    public Stack<Point> getPath(){ return path; }

    public Stack<Point> getTeleportSpot(){ return teleportSpot; }
}
