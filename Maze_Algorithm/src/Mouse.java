import java.util.Stack;

public class Mouse {
    private int energy;
    private double mana;
    private Maze maze;
    private Stack<Point> bifurcation;
    public final int[][] direction ={{0,1},{1,0},{0,-1},{-1,0}};


    public Mouse(Maze maze) {
        this.maze = maze;
        energy = 100; //TODO -> 에너지 수정
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public boolean isTeleportPossible() {
        return mana >= 50;
    }
}
