////package project.project.components;
////
////public class HealthComponent implements Component {
////    private int health;
////
////    public HealthComponent(int health) {
////        this.health = health;
////    }
////
////    public int getHealth() {
////        return health;
////    }
////
////    public void setHealth(int health) {
////        this.health = health;
////    }
////
////    public void decreaseHealth(int amount) {
////        this.health -= amount;
////    }
////
////    public boolean isAlive() {
////        return health > 0;
////    }
////}
//
//package project.project.components;
//
//public class HealthComponent implements Component {
//    private int currentHealth;
//    private int maxHealth;
//
//    public HealthComponent(int maxHealth) {
//        this.maxHealth = Math.max(maxHealth, 0); // Empêche une santé maximale négative
//        this.currentHealth = this.maxHealth;
//    }
//
//    // Retourne la santé actuelle
//    public int getCurrentHealth() {
//        return currentHealth;
//    }
//
//    // Retourne la santé maximale
//    public int getMaxHealth() {
//        return maxHealth;
//    }
//
//    // Définit la santé maximale et ajuste la santé actuelle si nécessaire
//    public void setMaxHealth(int maxHealth) {
//        this.maxHealth = Math.max(maxHealth, 0);
//        if (currentHealth > this.maxHealth) {
//            currentHealth = this.maxHealth;
//        }
//    }
//
//    // Inflige des dégâts, mais ne descend pas en dessous de zéro
//    public void takeDamage(int damage) {
//        if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative.");
//        currentHealth = Math.max(currentHealth - damage, 0);
//    }
//
//    // Soigne l'entité, mais ne dépasse pas la santé maximale
//    public void heal(int amount) {
//        if (amount < 0) throw new IllegalArgumentException("Heal amount cannot be negative.");
//        currentHealth = Math.min(currentHealth + amount, maxHealth);
//    }
//
//    // Vérifie si l'entité est en vie
//    public boolean isAlive() {
//        return currentHealth > 0;
//    }
//
//    // Définit directement la santé actuelle (utile pour tests ou buffs/débuffs)
//    public void setHealth(int health) {
//        this.currentHealth = Math.min(Math.max(health, 0), maxHealth);
//    }
//
//    // Méthode utilitaire pour réinitialiser la santé
//    public void resetHealth() {
//        this.currentHealth = this.maxHealth;
//    }
//}


//package project.project.components;
//
//public class HealthComponent implements Component {
//    private int currentHealth;
//    private int maxHealth;
//
//    public HealthComponent(int maxHealth) {
//        this.maxHealth = Math.max(maxHealth, 0); // Empêche une santé maximale négative
//        this.currentHealth = this.maxHealth;
//    }
//
//    public int getCurrentHealth() {
//        return currentHealth;
//    }
//
//    public int getMaxHealth() {
//        return maxHealth;
//    }
//
//    public void setMaxHealth(int maxHealth) {
//        this.maxHealth = Math.max(maxHealth, 0);
//        if (currentHealth > this.maxHealth) {
//            currentHealth = this.maxHealth;
//        }
//    }
//
//    public void takeDamage(int damage) {
//        if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative.");
//        currentHealth = Math.max(currentHealth - damage, 0);
//    }
//
//    public void heal(int amount) {
//        if (amount < 0) throw new IllegalArgumentException("Heal amount cannot be negative.");
//        currentHealth = Math.min(currentHealth + amount, maxHealth);
//    }
//
//    public boolean isAlive() {
//        return currentHealth > 0;
//    }
//
//    public void setHealth(int health) {
//        this.currentHealth = Math.min(Math.max(health, 0), maxHealth);
//    }
//
//    public void resetHealth() {
//        this.currentHealth = this.maxHealth;
//    }
//}

package project.project.components;
public class HealthComponent implements Component {
    public int currentHealth;
    private int maxHealth;

    public HealthComponent(int maxHealth) {
        this.maxHealth = Math.max(maxHealth, 0); // Empêche une santé maximale négative
        this.currentHealth = this.maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(maxHealth, 0);
        if (currentHealth > this.maxHealth) {
            currentHealth = this.maxHealth;
        }
    }

    public void takeDamage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative.");
        currentHealth = Math.max(currentHealth - damage, 0);
        System.out.println(getName() + " takes " + damage + " damage. Current health: " + currentHealth);
    }

    public void heal(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Heal amount cannot be negative.");
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public void setHealth(int health) {
        this.currentHealth = Math.min(Math.max(health, 0), maxHealth);
    }

    public void resetHealth() {
        this.currentHealth = this.maxHealth;
    }

    public String getName() {
        return "HealthComponent";
    }
}
