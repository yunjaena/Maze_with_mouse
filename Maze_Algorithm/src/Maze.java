import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Maze {
    private int maze[][];
    private int xSize;
    private int ySize;
    private String fileName;

    public Maze(String fileName)
    {
        this.fileName = fileName;
        readMaze();
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
            }
        }

    }

    public void printMaze() {
        System.out.println("[원본 미로]");
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                switch (maze[i][j]) {
                    case 0:
                        System.out.print("    ");
                        break;
                    case 1:
                        System.out.print(" ■ ");
                        break;
                    case 2:
                        System.out.print("    ");
                        break;
                    case 3:
                        System.out.print("    ");
                        break;
                }
            }
            System.out.println();
        }
    }
    public void writeMaze(int energy, int count, boolean isMouseDead, Point deadPoint){
        int i, j;
        try {
            File file = new File("result.txt");
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
            StringBuilder line = new StringBuilder("");
            for (i = 0; i < ySize; i++) {
                for (j = 0; j < xSize; j++) {
                    if(maze[i][j] == 2) line.append("O");
                    else line.append("  ");
                    if(isMouseDead && (i == deadPoint.getY() && j == deadPoint.getX())) break;
                }
                line.append("\n");
                if(isMouseDead && (i == deadPoint.getY() && j == deadPoint.getX())) break;
            }
            if(isMouseDead) line.append("출구를 못 찾았습니다.\n");
            line.append("[Initial Energy : " + Integer.toString(energy + count) + "]\n");
            line.append("[Wasted Energy : " + Integer.toString(count) + "]\n");
            line.append("[Remain Energy : " + Integer.toString(energy) + "]");
            bufWriter.write(line.toString());
            bufWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public void printResult() {
        System.out.println("[결과 출력]");
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                switch (maze[i][j]) {
                    case 0:
                        System.out.print("    ");
                        break;
                    case 1:
                        System.out.print("    ");
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

}
