import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Maze {
    private int printType;
    private int maze[][];
    private int xSize;
    private int ySize;
    private int lowBound;
    private Point goal;
    private String fileName;

    public Maze(String fileName, int printType)
    {
        this.fileName = fileName;
        this.printType = printType;
        goal = new Point(1, 0);
        readMaze();
    }

    public void setPrintType(int printType) {
        this.printType = printType;
    }

    public void readMaze() {
        ArrayList<String> readMaze = new ArrayList<>();
        try {
            File file = new File(fileName);
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                line = line.replace(" ","");
                readMaze.add(line);
            }
            bufReader.close();
        } catch (FileNotFoundException e) {
            // TODO: handle exception
        } catch (IOException e) {
            System.out.println(e);
        }

        xSize = readMaze.get(0).length();
        ySize = readMaze.size();

        maze = new int[ySize][xSize];

        for (int i = 0; i < readMaze.size(); i++) {
            for (int j = 0; j < xSize; j++) {
                maze[i][j] = Character.getNumericValue(readMaze.get(i).charAt(j));
                if((maze[i][j] == 0) && (i == 0 || j == 0 || i == ySize - 1 || j == xSize - 1)) goal = new Point(j, i);
            }
        }
    }

    public void printMaze() {
        System.out.println("[원본 미로]");
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                switch (maze[i][j]) {
                    case 0:
                        System.out.print(Print.printBlank(printType));
                        break;
                    case 1:
                        System.out.print(" ■ ");
                        break;
                    case 2:
                        System.out.print(Print.printBlank(printType));
                        break;
                    case 3:
                        System.out.print(Print.printBlank(printType));
                        break;
                }
            }
            System.out.println();
        }
    }
    public void writeMaze(int energy, int count, double mana, boolean isMouseDead){
        int i, j;
        double remainMana = (Math.round(mana*10))/10.0, stackedMana = count * 0.1, teleportUsageCount = Math.round((stackedMana - remainMana) / 5.0);
        try {
            File file = new File("result.txt");
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
            StringBuilder line = new StringBuilder("[고정폭 글꼴(굴림체, 돋움체 등) 사용 권장 * : 최단 경로, - : 되돌아온 길]\n[Input File = " + fileName + "]\r\n");
            for (i = 0; i < ySize; i++) {
                for (j = 0; j < xSize; j++) {
                    if(maze[i][j] == 1) line.append("1 ");
                    else if(maze[i][j] == 2) line.append("* ");
                    else if(maze[i][j] == 3) line.append("- ");
                    else line.append("  ");
                }
                line.append("\r\n");
            }
            if(isMouseDead) line.append("출구를 못 찾았습니다.\r\n");
            line.append("[Initial Energy : " + (energy + count) + ", Stacked Mana : " + stackedMana+ "]\r\n");
            line.append("[Wasted Energy : " + count + ", Teleport usage count : " + (int)teleportUsageCount + "]\r\n");
            line.append("[Remain Energy : " + energy + ", Remain Mana : " + remainMana + "]\r\n");
            bufWriter.write(line.toString());
            bufWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void writeRoute(Stack<Point> path, Stack<Point> teleportSpot){
        int i, j = 0, cnt = 0;
        try {
            File file = new File("path.txt");
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
            StringBuilder line = new StringBuilder("[Input File = " + fileName + "]\r\n");
            for (i = 0; i < path.size(); i++) {
                cnt++;
                line.append("(" + path.get(i).getX() + ", " + path.get(i).getY() + ") ");
                if((j < teleportSpot.size()) && ((path.get(i).getX() == teleportSpot.get(j).getX()) && (path.get(i).getY() == teleportSpot.get(j).getY()))){
                    line.append("\r\n\r\n");
                    line.append("(" + teleportSpot.get(j).getX() + ", " + teleportSpot.get(j).getY() + ") --> ("  + teleportSpot.get(j+1).getX() + ", " + teleportSpot.get(j+1).getY() + ") Teleport\r\n\r\n");
                    j += 2;
                    cnt = 0;
                }

                    if(cnt == 10){
                        line.append("\r\n\r\n");
                        cnt = 0;
                    }

            }
            line.append("\r\n");
            bufWriter.write(line.toString());
            bufWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public void printResult() {
        System.out.println("[결과 출력]");
        for (int i = 0; i <= lowBound; i++) {
            for (int j = 0; j < xSize; j++) {
                switch (maze[i][j]) {
                    case 0:
                        System.out.print(Print.printBlank(printType));
                        break;
                    case 1:
                        System.out.print(Print.printBlank(printType));
                        break;
                    case 2:
                        System.out.print(" ■ ");
                        break;
                    case 3:
                        System.out.print(" □ ");
                        break;
                }
            }
            System.out.println();
        }
    }



    public int[][] getMaze() {
        return maze;
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public Point getGoal() { return goal; }

    public int getLowBound(){ return lowBound; }

    public void setLowBound(int lowBound){ this.lowBound = lowBound; }

}
