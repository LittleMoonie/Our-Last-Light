package src.game.components.stats;

import src.game.components.Component;

public class SpeedComponent implements Component {
    public int normalSpeed;
    public int sprintSpeed;

    public SpeedComponent(int normalSpeed, int sprintSpeed) {
        this.normalSpeed = normalSpeed;
        this.sprintSpeed = sprintSpeed;
    }
}
