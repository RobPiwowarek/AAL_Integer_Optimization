import Jama.Matrix;

public class Evaluator {

    // evaluate x^T * A * x + b^T * x
    static double evaluateExpression(Matrix x, Matrix A, Matrix b) {
        return x.transpose().times(A).times(x).plus(b.transpose().times(x)).get(0, 0);
    }

    public static Matrix fixToNearestInteger(Matrix x, int position) {
        Matrix newMatrix = new Matrix(x.getArray());
        newMatrix.set(0, position, Math.round(x.get(0, position)));
        return newMatrix;
    }

    public static Matrix fixToCeilInteger(Matrix x, int position) {
        Matrix newMatrix = new Matrix(x.getArray());
        newMatrix.set(0, position, Math.ceil(x.get(0, position)));
        return newMatrix;
    }

    public static Matrix fixToFloorInteger(Matrix x, int position) {
        Matrix newMatrix = new Matrix(x.getArray());
        newMatrix.set(0, position, Math.floor(x.get(0, position)));
        return newMatrix;
    }

}
