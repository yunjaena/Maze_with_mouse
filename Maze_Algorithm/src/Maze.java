import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Maze {
	private int maze[][];
	private int xSize;
	private int ySize;

	public void reaedMaze(String fileName) {

		ArrayList<String> readMaze = new ArrayList<>();
		try {
			// ���� ��ü ����
			File file = new File(fileName);
			// �Է� ��Ʈ�� ����
			FileReader filereader = new FileReader(file);
			// �Է� ���� ����
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				readMaze.add(line);
			}
			// .readLine()�� ���� ���๮�ڸ� ���� �ʴ´�.
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
		for (int i = 0; i < ySize; i++) {
			for (int j = 0; j < xSize; j++) {
				switch (maze[i][j]) {
					case 0:
						System.out.print(" ");
						break;
					case 1:
						System.out.print("��");
						break;
					case 2:
						System.out.print("-");
						break;
					case 3:
						System.out.print("��");
						break;
				}
			}
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
