import Algorithm.Optimizer;
import Generator.Generator;
import Jama.Matrix;
import Reader.MatrixReader;

import java.util.Scanner;

import static java.lang.Thread.sleep;

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
            System.out.println("Choose execution mode");
            System.out.println("1 - optimize from (files A, B)");
            System.out.println("2 - optimize from (random matrices)");
            System.out.println("3 - benchmarking from (random matrices)");
            System.out.println("4 - mass benchmarking");
            System.out.println("5 - preheat");
            System.out.println("6 - optimize from random integer matrices");
            System.out.println("7 - end");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Matrix A = MatrixReader.readMatrixFromFile("A.txt");
                    Matrix B = MatrixReader.readMatrixFromFile("B.txt");

                    //optimizer.optimize(A, B);
                    System.out.println("Time: " + optimizer.timedOptimize(A, B));
                    break;
                case 2:
                    System.out.print("Enter matrix dimensions: ");
                    dim = scanner.nextInt();
                    Matrix A2 = generator.generatePositiveDefiniteMatrix(dim);
                    Matrix B2 = generator.generateVectorB(dim);
                    optimizer.optimize(A2, B2);
                    break;
                case 3:
                    System.out.print("Enter matrix dimensions: ");
                    dim = scanner.nextInt();

                    Matrix A3 = generator.generatePositiveDefiniteMatrix(dim);
                    Matrix B3 = generator.generateVectorB(dim);

                    System.out.println("Time: " + optimizer.timedOptimize(A3, B3));
                    break;
                case 4:
                    double[] times = new double[100];

                    for (int i = 2; i < 25; ++i) {
                        Matrix A4 = generator.generatePositiveDefiniteMatrix(i);
                        Matrix B4 = generator.generateVectorB(i);

                        times[i] = optimizer.timedOptimize(A4, B4);
                    }

                    System.out.println("Times table:");
                    for (int i = 2; i < 25; ++i) {
                        System.out.println("[" + i + "] ------ time: " + times[i]);
                    }
                    break;
                case 5:
                    System.out.println("Preheat");

                    for (int i = 0; i < 100; ++i) {
                        Matrix A5 = generator.generatePositiveDefiniteMatrix(3);
                        Matrix B5 = generator.generateVectorB(3);

                        optimizer.optimize(A5, B5);
                    }

                    System.out.println("Preheat finished");
                    break;
                case 6:
                    System.out.print("Enter matrix dimensions: ");
                    dim = scanner.nextInt();
                    Matrix A6 = generator.generatePositiveDefiniteMatrixOfIntegers(dim);
                    Matrix B6 = generator.generateVectorBOfIntegers(dim);
                    optimizer.optimize(A6, B6);
                    break;
                case 7:
                    stop = true;
                    break;
                default:
                    System.out.println("incorrect choice");
                    System.out.println("choose between 1 and 7");
            }
        }
    }

}