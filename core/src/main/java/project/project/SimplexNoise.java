// core/src/main/java/project/project/SimplexNoise.java
package project.project;

import java.util.Random;

public class SimplexNoise {
    private SimplexNoiseOctave[] octaves;
    private double[] frequencies;
    private double[] amplitudes;

    private int largestFeature;
    private double persistence;
    private int seed;

    public SimplexNoise(int largestFeature, double persistence, int seed) {
        this.largestFeature = largestFeature;
        this.persistence = persistence;
        this.seed = seed;

        // Number of octaves based on largest feature
        int numberOfOctaves = (int) Math.ceil(Math.log(largestFeature) / Math.log(2));

        octaves = new SimplexNoiseOctave[numberOfOctaves];
        frequencies = new double[numberOfOctaves];
        amplitudes = new double[numberOfOctaves];

        Random rnd = new Random(seed);

        for (int i = 0; i < numberOfOctaves; i++) {
            octaves[i] = new SimplexNoiseOctave(rnd.nextInt());

            frequencies[i] = Math.pow(2, i);
            amplitudes[i] = Math.pow(persistence, i);
        }
    }

    /**
     * Generates a normalized noise value by summing contributions from all octaves.
     *
     * @param x The x-coordinate in noise space.
     * @param y The y-coordinate in noise space.
     * @return A normalized noise value within [-1.0, 1.0].
     */
    public double getNoise(double x, double y) {
        double noiseValue = 0.0;
        double maxAmplitude = 0.0;

        for (int i = 0; i < octaves.length; i++) {
            double frequency = frequencies[i];
            double amplitude = amplitudes[i];

            noiseValue += octaves[i].noise(x / frequency, y / frequency) * amplitude;
            maxAmplitude += amplitude;
        }

        // Normalize the noise value to ensure it stays within [-1.0, 1.0]
        double normalizedNoise = noiseValue / maxAmplitude;


        // Optional: Clamp the noise value to ensure it stays within [-1.0, 1.0]
        normalizedNoise = Math.max(-1.0, Math.min(1.0, normalizedNoise));

        System.out.println(normalizedNoise);
        return normalizedNoise;
    }
}
