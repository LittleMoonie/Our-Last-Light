package src.game.components.stats;

import src.game.components.Component;

public class HungerComponent implements Component {
    private int maxHunger;
    private int currentHunger;

    // Constructor to set initial hunger level and maximum hunger
    public HungerComponent(int maxHunger, int initialHunger) {
        this.maxHunger = maxHunger;
        this.currentHunger = Math.min(initialHunger, maxHunger); // Ensure initial hunger does not exceed max
    }

    // Getter for current hunger
    public int getHunger() {
        return currentHunger;
    }

    // Setter for current hunger with bounds check
    public void setHunger(int hunger) {
        this.currentHunger = Math.min(Math.max(hunger, 0), maxHunger); // Ensure within 0 and maxHunger
    }

    // Getter for max hunger
    public int getMaxHunger() {
        return maxHunger;
    }

    // Method to decrease hunger, ensuring it doesn't go below 0
    public void decreaseHunger(int amount) {
        currentHunger = Math.max(currentHunger - amount, 0);
    }

    // Method to increase hunger, ensuring it doesn't exceed max hunger
    public void increaseHunger(int amount) {
        currentHunger = Math.min(currentHunger + amount, maxHunger);
    }
}
