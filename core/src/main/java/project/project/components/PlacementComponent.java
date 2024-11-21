package project.project.components;

public class PlacementComponent implements Component {
    public boolean isPlaceable;
    public int tileWidth;
    public int tileHeight;

    public PlacementComponent(boolean isPlaceable, int tileWidth, int tileHeight) {
        this.isPlaceable = isPlaceable;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }
}
