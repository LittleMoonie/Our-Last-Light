package project.project.components;


public class AttackCooldownComponent implements Component {
    public float cooldownDuration; // Durée totale du cooldown
    public float cooldownTimer; // Temps restant avant la prochaine attaque

    public AttackCooldownComponent(float cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
        this.cooldownTimer = 0;
    }

}
