package project.project.components;

public class AttackComponent implements Component{
    private int damage;

    public AttackComponent(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(damage, 0);
    }
}
