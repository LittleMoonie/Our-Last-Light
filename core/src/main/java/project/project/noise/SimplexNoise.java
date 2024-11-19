package project.project.noise;

public class SimplexNoise {
    private SimplexNoiseOctave[] octaves;
    private double[] frequencies;
    private double[] amplitudes;
    private int seed;

    public SimplexNoise(int largestFeature, double persistence, int seed) {
        this.seed = seed;

        int numberOfOctaves = (int) Math.ceil(Math.log(largestFeature) / Math.log(2));

        octaves = new SimplexNoiseOctave[numberOfOctaves];
        frequencies = new double[numberOfOctaves];
        amplitudes = new double[numberOfOctaves];

        for (int i = 0; i < numberOfOctaves; i++) {
            octaves[i] = new SimplexNoiseOctave(seed + i);

            frequencies[i] = Math.pow(2, i); // Lower frequency scaling
            amplitudes[i] = Math.pow(persistence, i) * 0.8; // Increased smoothing
        }
    }

    public double getNoise(double x, double y) {
        double result = 0;

        for (int i = 0; i < octaves.length; i++) {
            double frequency = frequencies[i];
            double amplitude = amplitudes[i] * 2; // Increase amplitude
            result += octaves[i].noise(x / frequency, y / frequency) * amplitude;
        }

        return result;
    }
}
