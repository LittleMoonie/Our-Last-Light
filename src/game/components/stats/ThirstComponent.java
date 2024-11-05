package src.game.components.stats;

import src.game.components.Component;

public class ThirstComponent implements Component {
    private int maxThirst;
    private int currentThirst;

    public ThirstComponent(int maxThirst, int initialThirst) {
        this.maxThirst = maxThirst;
        this.currentThirst = Math.min(initialThirst, maxThirst);
    }

    public int getThirst() {
        return currentThirst;
    }

    public void setThirst(int thirst) {
        this.currentThirst = Math.min(Math.max(thirst, 0), maxThirst);
    }

    public int getMaxThirst() {
        return maxThirst;
    }

    public void decreaseThirst(int amount) {
        currentThirst = Math.max(currentThirst - amount, 0);
    }

    public void increaseThirst(int amount) {
        currentThirst = Math.min(currentThirst + amount, maxThirst);
    }
}
