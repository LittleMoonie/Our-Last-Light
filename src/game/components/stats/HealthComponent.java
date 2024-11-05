package src.game.components.stats;

import src.game.components.Component;

public class HealthComponent implements Component {
    private int maxHealth;
    private int currentHealth;

    public HealthComponent(int maxHealth, int initialHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = Math.min(initialHealth, maxHealth);
    }

    public int getHealth() {
        return currentHealth;
    }

    public void setHealth(int health) {
        this.currentHealth = Math.min(Math.max(health, 0), maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void decreaseHealth(int amount) {
        currentHealth = Math.max(currentHealth - amount, 0);
    }

    public void increaseHealth(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }
}
