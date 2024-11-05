package src.game.components;

public class InputComponent implements Component {
    public boolean up, down, left, right, sprint, collecting, showInventory;

    public void reset() {
        collecting = false;
        showInventory = false;
    }
}
