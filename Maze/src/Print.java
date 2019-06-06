public class Print {
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;

    public static String printBlank(int type) {
        if (type == TYPE_ONE)
            return "   ";
        else
            return "    ";
    }
}
