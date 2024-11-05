package src.game.components;

public class PositionComponent implements Component {
    public int x;
    public int y;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters for x and y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Setters for x and y coordinates
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Set both x and y coordinates at once
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Move the position by a certain offset
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    // Reset position to origin (0, 0)
    public void resetPosition() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public String toString() {
        return "PositionComponent{" + "x=" + x + ", y=" + y + '}';
    }
}
