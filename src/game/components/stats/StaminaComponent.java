package src.game.components.stats;

import src.game.components.Component;

public class StaminaComponent implements Component {
    private int maxStamina;
    private int currentStamina;

    public StaminaComponent(int maxStamina, int initialStamina) {
        this.maxStamina = maxStamina;
        this.currentStamina = Math.min(initialStamina, maxStamina);
    }

    public int getStamina() {
        return currentStamina;
    }

    public void setStamina(int stamina) {
        this.currentStamina = Math.min(Math.max(stamina, 0), maxStamina);
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void decreaseStamina(int amount) {
        currentStamina = Math.max(currentStamina - amount, 0);
    }

    public void increaseStamina(int amount) {
        currentStamina = Math.min(currentStamina + amount, maxStamina);
    }
}
