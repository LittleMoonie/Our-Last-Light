package project.project.utils;

import java.security.SecureRandom;

public class SeedGenerator {

    /**
     * Generates a complex seed by combining various sources of entropy.
     *
     * @return A highly randomized integer seed.
     */
    public static int generateComplexSeed() {
        // Get the current time in nanoseconds (for uniqueness)
        long timeSeed = System.nanoTime();

        // Use SecureRandom for cryptographically strong random values
        SecureRandom secureRandom = new SecureRandom();
        int randomSeed1 = secureRandom.nextInt();
        int randomSeed2 = secureRandom.nextInt();

        // Use a hash of the time-based and random seeds to add entropy
        long mixedSeed = timeSeed ^ (randomSeed1 * 0x5DEECE66DL + randomSeed2);

        // Apply additional bitwise manipulations for more complexity
        mixedSeed ^= (mixedSeed << 21);
        mixedSeed ^= (mixedSeed >>> 35);
        mixedSeed ^= (mixedSeed << 4);

        // Take the final seed modulo to fit it within the int range
        int finalSeed = (int) (mixedSeed & 0xFFFFFFFFL);
        return finalSeed;
    }
}
