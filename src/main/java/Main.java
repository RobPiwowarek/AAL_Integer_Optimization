import Jama.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {

        Matrix A = MatrixReader.readMatrixFromFile("A.txt");
        Matrix B = MatrixReader.readMatrixFromFile("B.txt");

        A.print(3, 6);
        B.print(3, 6);

        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        sortBySecondDerivative(A, B, Tuple.descending);

        Matrix continuousMinimum = A.inverse().times(B).times(-0.5);

        if (isSolutionInteger(continuousMinimum)) {
            continuousMinimum.print(continuousMinimum.getColumnDimension(), 5);
            return;
        }

        int n = continuousMinimum.getColumnDimension();

        Matrix x = continuousMinimum;

        Matrix minimum;

        Vector<Integer> numberOfCalls = new Vector<>(n);

        double lowerBound = Double.MAX_VALUE;


        A.print(3, 6);
        continuousMinimum.print(3, 6);
        System.out.println(n);

        fixToFloorInteger(continuousMinimum).print(3, 6);

        return;

//
//
//        for (int d = 0; d < n; ++d){
//            fixAtPosition(x, d, numberOfCalls);
//
//            double currentLowerBound = Evaluator.evaluateExpression(x, A, B);
//
//            //lowerBound = lowerBound < currentLowerBound ? lowerBound : currentLowerBound;
//
//            if (lowerBound <= currentLowerBound){
//                // fuck go back
//                 --d;
//
//                 //
//
//                 --d;
//            }
//            else {
//                lowerBound = currentLowerBound;
//            }
//
//            // fix x at d
//            // calc lower bound by calculating f(so far fixed x)
//            // check if lowerbound > current minimal lower bound
//            // yes -> dont go that way - step back one step and calc one more point (3 points)
//            //                         - go to the point which has lowest lower bound
//            // no -> go that way, ++d repeat algorithm
//
//
//
//        }
//
//        minimum = x;
 }

    private static void fixAtPosition(Matrix m, int d, Vector<Integer> calls){
        int numberOfCalls = calls.get(d);

        // d % 2 == 0 => -
        // else +

        // numberOfCalls

    }

    private static boolean isSolutionInteger(Matrix m) {
        for (int i = 0; i < m.getRowDimension(); ++i) {
            if (m.getArray()[i][0] != Math.round(m.getArray()[i][0]))
                return false;
        }
        return true;
    }

    private static Matrix fixToFloorInteger(Matrix x) {
        Matrix newMatrix = new Matrix(x.getArray());
        for (int i = 0; i < x.getRowDimension(); ++i) {
            newMatrix.set(i, 0, Math.floor(x.get(i, 0)));
        }

        return newMatrix;
    }

    private static void sortBySecondDerivative(Matrix A, Matrix B, Comparator<Tuple> comparator) {
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(comparator);

        B = B.transpose();

        for (int i = 0; i < list.size(); ++i) {
            int key = list.get(i).key;

            if (key != i) {
                Collections.swap(list, i, key);
                swapColumns(A, i, key);
                swapColumns(B, i, key);
            }
        }

        B = B.transpose();

    }

    private static void swapColumns(Matrix m, int i1, int i2) {
        Matrix tempCol = JamaUtils.getcol(m, i1);
        JamaUtils.setcol(m, i1, JamaUtils.getcol(m, i2));
        JamaUtils.setcol(m, i2, tempCol);
    }
}