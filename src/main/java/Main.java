import Jama.Matrix;

import java.lang.reflect.Array;
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

        double upperBound = Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        Matrix x = continuousMinimum;

        ArrayList<Matrix> fixedPoints = new ArrayList<>();

        Vector<ArrayList<Matrix>> points = new Vector<>(n);

        for (int i = 0; i < n; ++i)
            points.add(new ArrayList<>());

        for (int d = 0; d < n; ++d){
            if (numberOfCalls[d] == 0) {
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
            }
            else
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));

            x = chooseMinimum(points.get(d), A, B);

            double val = Evaluator.evaluateExpression(x, A, B);

//            System.out.println("o: " + points.get(0).size());
//            System.out.println("1: " + points.get(1).size());
//            x.print(3,6);
//            System.out.println("val: " + val);

            if (val > upperBound){
                points.get(d).clear();

                int size = 0;
                for (int i = 0; i < n; ++i)
                    size += points.get(i).size();

                if (size == 0)
                    break;

                if (d == 0)
                    d = -1;
                else
                    d = d-2;

                continue;
            }

            if (isSolutionInteger(x)) {
                points.get(d).remove(x);

                if (val <= upperBound) {
                    upperBound = val;
                    fixedPoints.add(x);
                }

                --d;
            }

            points.get(d).remove(x);
        }

        System.out.println("end");

        Matrix minimum = chooseMinimum(fixedPoints, A, B);

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

        Matrix x = Matrix.constructWithCopy(m.getArray());

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
        double[][] temp = m.getArray();

        for (int i = 0; i < m.getRowDimension(); ++i) {
            if (temp[i][0] != Math.round(temp[i][0]))
                return false;
        }
        return true;
    }

    private static Matrix fixEntireMatrixToClosestInteger(Matrix x) {
        Matrix newMatrix = Matrix.constructWithCopy(x.getArray());

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