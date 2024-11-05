package src.game.components.stats;

import src.game.components.Component;

public class SanityComponent implements Component {
    private int maxSanity;
    private int currentSanity;

    public SanityComponent(int maxSanity, int initialSanity) {
        this.maxSanity = maxSanity;
        this.currentSanity = Math.min(initialSanity, maxSanity);
    }

    public int getSanity() {
        return currentSanity;
    }

    public void setSanity(int sanity) {
        this.currentSanity = Math.min(Math.max(sanity, 0), maxSanity);
    }

    public int getMaxSanity() {
        return maxSanity;
    }

    public void decreaseSanity(int amount) {
        currentSanity = Math.max(currentSanity - amount, 0);
    }

    public void increaseSanity(int amount) {
        currentSanity = Math.min(currentSanity + amount, maxSanity);
    }
}
