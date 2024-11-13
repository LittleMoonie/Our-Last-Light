// core/src/main/java/project/project/SimplexNoiseOctave.java
package project.project;

import java.util.Random;

public class SimplexNoiseOctave {
    private static final int GRADIENT_SIZE_TABLE = 256;
    private int[] perm;
    private Grad[] grad3;

    public SimplexNoiseOctave(int seed) {
        perm = new int[GRADIENT_SIZE_TABLE * 2];
        grad3 = new Grad[] {
            new Grad(1,1,0), new Grad(-1,1,0), new Grad(1,-1,0), new Grad(-1,-1,0),
            new Grad(1,0,1), new Grad(-1,0,1), new Grad(1,0,-1), new Grad(-1,0,-1),
            new Grad(0,1,1), new Grad(0,-1,1), new Grad(0,1,-1), new Grad(0,-1,-1)
        };
        initPermutationTable(seed);
    }

    private void initPermutationTable(int seed) {
        int[] p = new int[GRADIENT_SIZE_TABLE];
        for (int i = 0; i < GRADIENT_SIZE_TABLE; i++) {
            p[i] = i;
        }

        // Shuffle using the seed
        Random random = new Random(seed);
        for (int i = GRADIENT_SIZE_TABLE - 1; i > 0; i--) {
            int k = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[k];
            p[k] = temp;
        }

        // Duplicate the permutation table
        for (int i = 0; i < GRADIENT_SIZE_TABLE * 2; i++) {
            perm[i] = p[i % GRADIENT_SIZE_TABLE];
        }
    }

    // Inner class for gradient vectors
    private static class Grad {
        double x, y, z;

        Grad(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Generates the raw simplex noise value for the given coordinates.
     *
     * @param xin The x-coordinate in noise space.
     * @param yin The y-coordinate in noise space.
     * @return The raw noise value.
     */
    public double noise(double xin, double yin) {
        double n0, n1, n2; // Noise contributions from the three corners

        // Skewing/Unskewing factors for 2D
        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

        // Skew the input space to determine which simplex cell we're in
        double s = (xin + yin) * F2;
        int i = fastfloor(xin + s);
        int j = fastfloor(yin + s);

        double t = (i + j) * G2;
        double X0 = i - t; // Unskew the cell origin back to (x,y) space
        double Y0 = j - t;
        double x0 = xin - X0; // The x,y distances from the cell origin
        double y0 = yin - Y0;

        // Determine which simplex we are in
        int i1, j1; // Offsets for second corner
        if (x0 > y0) {
            i1 = 1; j1 = 0; // Lower triangle
        } else {
            i1 = 0; j1 = 1; // Upper triangle
        }

        // Offsets for remaining corners
        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        // Hash the coordinates to get gradient indices
        int ii = i & 0xFF;
        int jj = j & 0xFF;
        int gi0 = perm[ii + perm[jj]] % 12;
        int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

        // Calculate the noise contributions from the three corners
        n0 = cornerContribution(x0, y0, grad3[gi0]);
        n1 = cornerContribution(x1, y1, grad3[gi1]);
        n2 = cornerContribution(x2, y2, grad3[gi2]);

        // Sum the noise contributions without excessive scaling
        double rawNoise = n0 + n1 + n2;

        // No normalization or clamping here
        return rawNoise;
    }

    private double cornerContribution(double x, double y, Grad grad) {
        double t = 0.5 - x * x - y * y;
        if (t < 0) return 0.0;
        t *= t;
        return t * t * dot(grad, x, y);
    }

    private static int fastfloor(double x) {
        return x > 0 ? (int)x : (int)x - 1;
    }

    private static double dot(Grad g, double x, double y) {
        return g.x * x + g.y * y;
    }
}
