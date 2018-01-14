import Algorithm.Optimizer;
import Generator.Generator;
import Jama.Matrix;
import Reader.MatrixReader;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Integer Optimization");
        System.out.println("Created by Robert Piwowarek EITI ISI 5");

        Optimizer optimizer = new Optimizer();
        Generator generator = new Generator();

        boolean stop = false;

        while (!stop) {
            int choice;
            int dim;
            int iterations;
            System.out.println("Choose execution mode");
            System.out.println("1 - optimize from (files A, B)");
            System.out.println("2 - optimize from (random matrices)");
            System.out.println("3 - benchmarking from (random matrices)");
            System.out.println("4 - mass benchmarking");
            System.out.println("5 - end");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Matrix A = MatrixReader.readMatrixFromFile("A.txt");
                    Matrix B = MatrixReader.readMatrixFromFile("B.txt");

                    //optimizer.optimize(A, B);
                    System.out.println("Time: " + optimizer.timedOptimize(1, A, B));
                    break;
                case 2:
                    System.out.print("Enter matrix dimensions: ");
                    dim = scanner.nextInt();

                    Matrix A2 = generator.generatePositiveDefiniteMatrixOfIntegers(dim);
                    Matrix B2 = generator.generateVectorBOfIntegers(dim);

                    optimizer.optimize(A2, B2);
                    break;
                case 3:
                    System.out.print("Enter matrix dimensions: ");
                    dim = scanner.nextInt();
                    System.out.println("Enter iterations");
                    iterations = scanner.nextInt();

                    Matrix A3 = generator.generatePositiveDefiniteMatrix(dim);
                    Matrix B3 = generator.generateVectorB(dim);

                    System.out.println("Time: " + optimizer.timedOptimize(iterations, A3, B3));
                    break;
                case 4:
                    long[] times = new long[10];
                    System.out.println("Enter iterations");
                    iterations = scanner.nextInt();

                    for (int i = 0; i < 10; ++i) {
                        Matrix A4 = generator.generatePositiveDefiniteMatrix(i+5);
                        Matrix B4 = generator.generateVectorB(i+5);

                        times[i] = optimizer.timedOptimize(iterations, A4, B4);
                        System.gc();
                    }

                    System.out.println("Times table:");
                    for (int i = 0; i < 10; ++i) {
                        System.out.println("[" + (i + 5) + "] ------ time: " + times[i]);
                    }
                    break;
                case 5:
                    stop = true;
                    break;
                default:
                    System.out.println("incorrect choice");
            }
        }
    }

}