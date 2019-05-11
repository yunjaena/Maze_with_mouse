import java.util.Stack;

public class Mouse {
    private int energy;
    private double mana;
    private Maze maze;
    private Stack<Point> bifurcation;

    public Mouse(Maze maze) {
        this.maze = maze;
        energy = maze.getxSize() * maze.getySize() * 2;
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

    public void decreaseEnergy() {
        energy--;
    }

    public void increaseMana() {
        mana += 0.1;
    }

    public void useMana() {
        mana -= 50.0;
    }
}
