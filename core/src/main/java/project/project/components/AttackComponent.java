package project.project.components;

public class AttackComponent implements Component {
    private int damage;
    private float attackRange;

    public AttackComponent(int damage, float attackRange) {
        this.damage = damage;
        this.attackRange = attackRange;
    }

    public float getAttackRange() {
        return attackRange;
    }


    public int getAttackDamage() {
        return damage;
    }

    public void setAttackDamage(int damage) {
        this.damage = damage;
    }

}
