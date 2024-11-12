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
            amplitudes[i] = Math.pow(persistence, numberOfOctaves - i);
        }
    }

    public double getNoise(int x, int y) {
        double result = 0;

        for (int i = 0; i < octaves.length; i++) {
            double frequency = frequencies[i];
            double amplitude = amplitudes[i];

            result += octaves[i].noise(x / frequency, y / frequency) * amplitude;
        }

        return result;
    }
}
