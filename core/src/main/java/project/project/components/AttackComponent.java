//package project.project.components;
//
//public class AttackComponent implements Component {
//    private float damage;
//
//    public AttackComponent(float damage) {
//        this.damage = damage;
//    }
//
//    public float getDamage() {
//        return damage;
//    }
//}
package project.project.components;

//public class AttackComponent implements Component {
//    private int attackPower;
//
//    public AttackComponent(int attackPower) {
//        this.attackPower = attackPower;
//    }
//
//    public int getAttackPower() {
//        return attackPower;
//    }
//
//    public void setAttackPower(int attackPower) {
//        this.attackPower = attackPower;
//    }
//
//    public String getName() {
//        return "AttackComponent";
//    }
//}


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

    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    public int getDamage() {
        return damage;
    }

}
