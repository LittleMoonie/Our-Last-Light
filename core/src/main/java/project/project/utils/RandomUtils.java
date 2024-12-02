package project.project.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random RANDOM = new Random();

    public static float randomFloat(int min, int max) {
        return min + RANDOM.nextFloat() * (max - min);
    }
}
