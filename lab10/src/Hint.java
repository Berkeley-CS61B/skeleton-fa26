public class Hint {
    void main() {
        IO.println("The powers of 256 in Java are:");
        int x = 1;
        for (int i = 0; i < 10; i += 1) {
            IO.println(i + "th power: " + x);
            x = x * 256;
        }
    }
}
