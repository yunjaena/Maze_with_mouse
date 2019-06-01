import java.util.Stack;

public class Mouse implements Event {

    private int energy;
    private double mana;
    private Maze maze;
    private Algorithm algorithm;
    private int count = 0;
    public static final double USING_MANA = 5.0;
    private Point deadPoint;

    public Mouse(Maze maze) {
        this.maze = maze;
        energy = maze.getxSize() * maze.getySize() * 2;
        algorithm = new Algorithm(maze, this);
        printMouseState();
        algorithm.Solve();
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public int getCount(){ return count; }

    @Override
    public void setDeadPoint(int x, int y){ deadPoint = new Point(x, y); }

    public Point getDeadPoint(){ return deadPoint;}
    public boolean isTeleportPossible() {
        return mana >= USING_MANA;
    }

    @Override
    public boolean isMouseDIe() {
        if (energy < 1) {
            return true;
        }
        else
            return  false;
    }

    @Override
    public void decreaseEnergy() {
        energy--;
    }

    @Override
    public void increaseMana() {
        mana += 0.1;
    }

    @Override
    public void useMana(int x, int y) {
        mana -= USING_MANA;
        /*System.out.print("(" + x + "," + y + ") Teleport");
        printMouseState();*/
    }

    @Override
    public void moveMouse(int x, int y) {
        count++;
        /*System.out.print("(" + x + "," + y + ")");
        printMouseState();*/
    }

    @Override
    public void printMouseState() {
        System.out.println("[" + "energy :" + energy + String.format(", mana : %.1f", mana) + ", count :" + count + "]");
    }
}
