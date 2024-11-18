package project.project.components;

public class HealthComponent implements Component {
    private int health;

    public HealthComponent(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void decreaseHealth(int amount) {
        this.health -= amount;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
