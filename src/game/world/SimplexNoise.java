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

    public double noise(double xin, double yin) {
        double n0, n1, n2;

        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

        double s = (xin + yin) * F2;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);

        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1; j1 = 0;
        } else {
            i1 = 0; j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        int ii = i & 255;
        int jj = j & 255;
        int gi0 = perm[ii + perm[jj]] % 12;
        int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

        n0 = calcCornerContribution(x0, y0, grad3[gi0]);
        n1 = calcCornerContribution(x1, y1, grad3[gi1]);
        n2 = calcCornerContribution(x2, y2, grad3[gi2]);

        return 70.0 * (n0 + n1 + n2);
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
