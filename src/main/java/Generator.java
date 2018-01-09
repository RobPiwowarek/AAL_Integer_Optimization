import Jama.Matrix;

import java.util.Random;

class Generator {
    Generator() {

    }

    Matrix generatePositiveDefiniteMatrix(int dim) {
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

    Matrix generateVectorB(int length) {
        double array[][] = new double[length][1];

        Random rand = new Random();

        for (int i = 0; i < length; ++i)
            for (int j = 0; j < 1; ++j) {
                array[i][j] = rand.nextDouble();
            }

        return new Matrix(array);
    }
}
