package Generator;

import Jama.Matrix;

import java.util.Random;

public class Generator {
    public Generator() {

    }

    public Matrix generatePositiveDefiniteMatrix(int dim) {
        double array[][] = new double[dim][dim];

        Random rand = new Random();

        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j) {
                array[i][j] = rand.nextDouble();
            }

        Matrix matrix = new Matrix(array);
        Matrix old = Matrix.constructWithCopy(array);
        return old.times(matrix.transpose());
    }

    public Matrix generateVectorB(int length) {
        double array[][] = new double[length][1];

        Random rand = new Random();

        for (int i = 0; i < length; ++i)
            for (int j = 0; j < 1; ++j) {
                array[i][j] = rand.nextDouble();
            }

        return new Matrix(array);
    }

    public Matrix generatePositiveDefiniteMatrixOfIntegers(int dim){
        double array[][] = new double[dim][dim];

        Random rand = new Random();

        for (int i = 0; i < dim; ++i)
            for (int j = 0; j < dim; ++j) {
                array[i][j] = rand.nextInt() % 5;
            }

        Matrix matrix = new Matrix(array);
        Matrix old = Matrix.constructWithCopy(array);
        return old.times(matrix.transpose());
    }

    public Matrix generateVectorBOfIntegers(int length) {
        double array[][] = new double[length][1];

        Random rand = new Random();

        for (int i = 0; i < length; ++i)
            for (int j = 0; j < 1; ++j) {
                array[i][j] = rand.nextInt() % 5;
            }

        return new Matrix(array);
    }
}
