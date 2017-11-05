import Jama.Matrix;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
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

        System.out.println("Candidate: ");

        Matrix CandidateX = inversed.times(b).times(-0.5);
        CandidateX.print(3, 5);

        System.out.println(CandidateX);
    }
}
