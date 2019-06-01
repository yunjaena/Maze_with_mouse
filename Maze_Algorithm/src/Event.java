public interface Event {

    void decreaseEnergy();

    void increaseMana();

    void useMana(int x, int y);

    void moveMouse(int x, int y);

    double getMana();

    boolean isMouseDIe();

    void setDeadPoint(int x, int y);

    void printMouseState();

}
