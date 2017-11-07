import Jama.Matrix;

import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        double[][] array = {{2, 1}, {1, 2}};
        Random rand = new Random();

        Matrix A = new Matrix(array);
        Matrix b = new Matrix(2, 1, 1);

        System.out.println(A.det());
        System.out.println("B:");
        b.print(3, 5);
        System.out.println("A:");
        A.print(3, 5);

        System.out.println("A-1:");
        Matrix inversed = A.inverse();
        inversed.print(3, 5);

        System.out.println("Candidate 1: ");

        double[][] vect = {{0}, {0}};
        for (int i = 0; i < 10000; ++i) {
            Matrix startingPointForGradient = new Matrix(vect);

            Matrix gradient = A.times(startingPointForGradient).times(2).plus(b);

            gradient.print(3, 5);
            double dx = gradient.get(0, 0);
            double dy = gradient.get(1, 0);

            if (dx >= 0) {
                if (dx >= 1)
                    vect[0][0] -= 1;
            } else {
                if (dx <= -1)
                    vect[0][0] += 1;
            }

            if (dy >= 0) {
                if (dy >= 1)
                    vect[1][0] -= 1;
            } else {
                if (dy <= -1)
                    vect[1][0] += 1;
            }

        }

        System.out.println(vect[0][0]);
        System.out.println(vect[1][0]);


        System.out.println("Candidate 2: ");

        Matrix CandidateX = inversed.times(b).times(-0.5);
        CandidateX.print(3, 5);

        System.out.println(CandidateX);
    }

}
