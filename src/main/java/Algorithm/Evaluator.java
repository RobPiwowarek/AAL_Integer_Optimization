package Algorithm;

import Jama.Matrix;

public class Evaluator {

    // evaluate x^T * A * x + b^T * x + c
    static double evaluateExpression(Matrix x, Matrix A, Matrix b, double c) {
        return x.transpose().times(A).times(x).plus(b.transpose().times(x)).get(0, 0) + c;
    }
}
