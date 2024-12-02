package project.project.components;

public class WaveComponent implements Component {
    private int currentWave;
    private int mobsRemaining;

    public WaveComponent(int currentWave, int mobsRemaining) {
        this.currentWave = currentWave;
        this.mobsRemaining = mobsRemaining;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void incrementWave() {
        currentWave++;
    }

    public int getMobsRemaining() {
        return mobsRemaining;
    }

    public void decrementMobsRemaining() {
        mobsRemaining = Math.max(mobsRemaining - 1, 0);
    }

    public void setMobsRemaining(int mobsRemaining) {
        this.mobsRemaining = mobsRemaining;
    }
}
