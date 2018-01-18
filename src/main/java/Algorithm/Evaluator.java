package Algorithm;

import Jama.Matrix;

public class Evaluator {

    // evaluate x^T * A * x + b^T * x + c
    static double evaluateExpression(Matrix x, Matrix A, Matrix b, double c) {
        Matrix temp = x.times(0.5).transpose().times(A).times(x);
        return temp.plus(b.transpose().times(x)).get(0, 0) + c;
    }
}
