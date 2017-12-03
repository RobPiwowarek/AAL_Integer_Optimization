import Jama.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws IOException {

        Matrix A = MatrixReader.readMatrixFromFile("A.txt");
        Matrix B = MatrixReader.readMatrixFromFile("B.txt");

        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        sortBySecondDerivative(A, B, Tuple.descending);

        Matrix continuousMinimum = A.inverse().times(B).times(-0.5);

        if (isSolutionInteger(continuousMinimum)) {
            continuousMinimum.print(continuousMinimum.getColumnDimension(), 5);
            return;
        }

        Matrix x = continuousMinimum;

        double lowerBound = Evaluator.evaluateExpression(continuousMinimum, A, B);
        double upperBound = Evaluator.evaluateExpression(fixToFloorInteger(continuousMinimum), A, B);
        int d = 0;
        final int n = continuousMinimum.getColumnDimension();
        double value = 0.0;
        Matrix min;


        // warunek wyjscia?
        while (true) {
            Matrix fixedSolution = Evaluator.fixToCeilInteger(x, d); // to powinno sie dynamicznie zmieniac a nie sam ceil tzn ceil, floor, ceil+1, floor+1 etc.

            double fixedSolutionValue = Evaluator.evaluateExpression(fixedSolution, A, B);

            if (fixedSolutionValue < upperBound) {
                // rozwijamy tzn?
                // przechodzimy do drugiej wspolrzednej etc.
                if (d < n)
                    ++d;

                x = fixedSolution;
            } else {
                // to nie rozwijamy dalej tego wezla
                if (d > 0)
                    --d;
            }
        }
    }

    private static boolean isSolutionInteger(Matrix m) {
        for (int i = 0; i < m.getColumnDimension(); ++i) {
            if (m.getArray()[0][i] != Math.round(m.getArray()[0][i]))
                return false;
        }
        return true;
    }

    private static Matrix fixToFloorInteger(Matrix x) {
        Matrix newMatrix = new Matrix(x.getArray());
        for (int i = 0; i < x.getColumnDimension(); ++i) {
            newMatrix.set(0, i, Math.floor(x.get(0, i)));
        }

        return newMatrix;
    }

    private static void sortBySecondDerivative(Matrix A, Matrix B, Comparator<Tuple> comparator) {
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(comparator);

        for (int i = 0; i < list.size(); ++i) {
            int key = list.get(i).key;

            if (key != i) {
                Collections.swap(list, i, key);
                swapColumns(A, i, key);
                swapColumns(B, i, key);
            }
        }
    }

    private static void swapColumns(Matrix m, int i1, int i2) {
        Matrix tempCol = JamaUtils.getcol(m, i1);
        JamaUtils.setcol(m, i1, JamaUtils.getcol(m, i2));
        JamaUtils.setcol(m, i2, tempCol);
    }
}