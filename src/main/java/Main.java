import Jama.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.IntStream;

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

        int n = continuousMinimum.getRowDimension();

        

        int numberOfCalls[] = new int[n];

        double lowerBound = Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        A.print(3, 6);
        continuousMinimum.print(3, 6);
        System.out.println(n);

        
        
        boolean stop = false;
        Matrix x = continuousMinimum;
        Matrix minimum;
        ArrayList<Matrix> points = new ArrayList<>();

        for (int d = 0; d < n; ++d){
            while (!stop){
                Matrix m = fixAtPosition(x, d, numberOfCalls, continuousMinimum);
                if (Evaluator.evaluateExpression(m, A, B) > lowerBound)
                    stop = true;

                points.add(m);
            }

            x = chooseMinimum(points, A, B);

            // todo: cofanie

            

            points.clear();
            stop = false;
        }

        minimum = x;

        minimum.print(3, 6);
 }

    private static Matrix chooseMinimum(ArrayList<Matrix> points, Matrix A, Matrix B){
        Matrix minMat;
        double currentMin;

        minMat = points.get(0);
        currentMin = Evaluator.evaluateExpression(minMat, A, B);

        for (Matrix x: points) {
            double val = Evaluator.evaluateExpression(x, A, B);

            if (val < currentMin){
                currentMin = val;
                minMat = x;
            }
        }

        return minMat;
    }

    private static Matrix fixAtPosition(Matrix m, int d, int[] calls, Matrix continuousMinimum){
        int numberOfCalls = calls[d];

        Matrix x = m;

        double howmuchtoadd = Math.floor((numberOfCalls) / 2);

        if (calls[d] % 2 != 0){
            x.set(d, 0, Math.floor(continuousMinimum.get(d, 0)) + howmuchtoadd);
        }
        else
            x.set(d, 0, Math.ceil(continuousMinimum.get(d, 0)) - howmuchtoadd);

        ++calls[d];

        return x;
    }

    private static boolean isSolutionInteger(Matrix m) {
        for (int i = 0; i < m.getRowDimension(); ++i) {
            if (m.getArray()[i][0] != Math.round(m.getArray()[i][0]))
                return false;
        }
        return true;
    }

    private static Matrix fixEntireMatrixToClosestInteger(Matrix x) {
        Matrix newMatrix = new Matrix(x.getArray());
        for (int i = 0; i < x.getRowDimension(); ++i) {
            newMatrix.set(i, 0, Math.round(x.get(i, 0)));
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