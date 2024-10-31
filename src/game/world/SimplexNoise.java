package src.game.world;

import java.util.Random;

public class SimplexNoise {
    private static final int GRADIENT_SIZE_TABLE = 256;
    private int[] perm;
    private Grad[] grad3;

    private static class Grad {
        double x, y, z;

        Grad(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public SimplexNoise(long seed) {
        grad3 = new Grad[] {
                new Grad(1,1,0), new Grad(-1,1,0), new Grad(1,-1,0), new Grad(-1,-1,0),
                new Grad(1,0,1), new Grad(-1,0,1), new Grad(1,0,-1), new Grad(-1,0,-1),
                new Grad(0,1,1), new Grad(0,-1,1), new Grad(0,1,-1), new Grad(0,-1,-1)
        };

        perm = new int[GRADIENT_SIZE_TABLE * 2];
        Random rand = new Random(seed);

        int[] p = new int[GRADIENT_SIZE_TABLE];
        for (int i = 0; i < GRADIENT_SIZE_TABLE; i++) {
            p[i] = i;
        }

        // Shuffle array
        for (int i = GRADIENT_SIZE_TABLE - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[index];
            p[index] = temp;
        }

        for (int i = 0; i < GRADIENT_SIZE_TABLE * 2; i++) {
            perm[i] = p[i % GRADIENT_SIZE_TABLE];
        }
    }

    // 2D simplex noise
    public double noise(double xin, double yin) {
        double n0, n1, n2; // Noise contributions from the three corners

        // Skewing and unskewing factors for 2D
        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

        // Skew the input space to determine which simplex cell we're in
        double s = (xin + yin) * F2;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);

        double t = (i + j) * G2;
        double X0 = i - t; // Unskew the cell origin back to (x,y) space
        double Y0 = j - t;
        double x0 = xin - X0; // The x,y distances from the cell origin
        double y0 = yin - Y0;

        // Determine which simplex we are in
        int i1, j1; // Offsets for second corner of simplex in (i,j) coords
        if (x0 > y0) {
            i1 = 1; j1 = 0; // Lower triangle
        } else {
            i1 = 0; j1 = 1; // Upper triangle
        }

        // Offsets for remaining corners
        double x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2; // Offsets for last corner
        double y2 = y0 - 1.0 + 2.0 * G2;

        // Hash the coordinates to get the gradient indices
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = perm[ii + perm[jj]] % 12;
        int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

        // Calculate the noise contributions from the three corners
        n0 = calcCornerContribution(x0, y0, grad3[gi0]);
        n1 = calcCornerContribution(x1, y1, grad3[gi1]);
        n2 = calcCornerContribution(x2, y2, grad3[gi2]);

        // Add contributions from each corner to get the final noise value
        // The result is scaled to return values in the interval [-1,1]
        double noiseValue = 70.0 * (n0 + n1 + n2);

        return noiseValue;
    }

    private double calcCornerContribution(double x, double y, Grad grad) {
        double t = 0.5 - x * x - y * y;
        if (t < 0) return 0.0;
        t *= t;
        return t * t * (grad.x * x + grad.y * y);
    }

    private int fastFloor(double x) {
        return x > 0 ? (int) x : ((int) x) - 1;
    }
}
