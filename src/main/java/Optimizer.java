import Jama.Matrix;

import java.util.ArrayList;
import java.util.Vector;

class Optimizer {

    Optimizer() {

    }

    void optimize(Matrix A, Matrix B) {
        System.out.println("A:");
        A.print(3, 6);
        System.out.println("B:");
        B.print(3, 6);

        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        ArrayList<Tuple> mapping = sortBySecondDerivative(A);

        Matrix continuousMinimum = A.inverse().times(B).times(-0.5);

        System.out.println("continuousMinimum:");
        continuousMinimum.print(3, 6);

        System.out.println("value: " + Evaluator.evaluateExpression(continuousMinimum, A, B));

        if (isSolutionInteger(continuousMinimum)) {
            continuousMinimum.print(continuousMinimum.getColumnDimension(), 5);
            return;
        }

        int n = continuousMinimum.getRowDimension();

        int numberOfCalls[] = new int[n];

        double upperBound = Double.MAX_VALUE;//Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        Matrix x = continuousMinimum;

        ArrayList<Matrix> fixedPoints = new ArrayList<>();

        Vector<ArrayList<Matrix>> points = new Vector<>(n);

        for (int i = 0; i < n; ++i)
            points.add(new ArrayList<>());

        for (int d = 0; d < n; ++d) {
            if (numberOfCalls[d] == 0) {
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum, mapping));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum, mapping));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum, mapping));
            } else
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum, mapping));

            x = chooseMinimum(points.get(d), A, B);

            double val = Evaluator.evaluateExpression(x, A, B);

            if (val > upperBound) {
                points.get(d).clear();

                int size = 0;
                for (int i = 0; i < n; ++i)
                    size += points.get(i).size();

                if (size == 0)
                    break;

                if (d == 0)
                    d = -1;
                else
                    d = d - 2;

                continue;
            }

            if (isSolutionInteger(x)) {
                points.get(d).remove(x);

                if (val <= upperBound) {
                    upperBound = val;
                    fixedPoints.add(x);
                }

                --d;
                continue;
            }

            points.get(d).remove(x);
        }

        System.out.println("end");

        Matrix minimum = chooseMinimum(fixedPoints, A, B);

        System.out.println(Evaluator.evaluateExpression(minimum, A, B));

        minimum.print(3, 6);
    }

    long timedOptimize(int iterations, Matrix A, Matrix B) {

        for (int i = 0; i < iterations; ++i) {
            optimize(A, B);
        }

        long start = System.nanoTime();

        optimize(A, B);

        long end = System.nanoTime();

        return (end - start) / 1000;
    }

    private Matrix chooseMinimum(ArrayList<Matrix> points, Matrix A, Matrix B) {
        Matrix minMat;
        double currentMin;

        minMat = points.get(0);
        currentMin = Evaluator.evaluateExpression(minMat, A, B);

        for (Matrix x : points) {
            double val = Evaluator.evaluateExpression(x, A, B);

            if (val < currentMin) {
                currentMin = val;
                minMat = x;
            }
        }

        return minMat;
    }

    private Matrix fixAtPosition(Matrix m, int d, int[] calls, Matrix continuousMinimum, ArrayList<Tuple> mapping) {
        int numberOfCalls = calls[d];

        Matrix x = Matrix.constructWithCopy(m.getArray());

        double diff = Math.floor((numberOfCalls) / 2);

        if (calls[d] % 2 != 0) {
            x.set(mapping.get(d).key, 0, Math.floor(continuousMinimum.get(mapping.get(d).key, 0)) - diff);
        } else
            x.set(mapping.get(d).key, 0, Math.ceil(continuousMinimum.get(mapping.get(d).key, 0)) + diff);

        ++calls[d];

        return x;
    }

    private boolean isSolutionInteger(Matrix m) {
        double[][] temp = m.getArray();

        for (int i = 0; i < m.getRowDimension(); ++i) {
            if (temp[i][0] != Math.round(temp[i][0]))
                return false;
        }
        return true;
    }

    private Matrix fixEntireMatrixToClosestInteger(Matrix x) {
        Matrix newMatrix = Matrix.constructWithCopy(x.getArray());

        for (int i = 0; i < x.getRowDimension(); ++i) {
            newMatrix.set(i, 0, Math.round(x.get(i, 0)));
        }

        return newMatrix;
    }

    private ArrayList<Tuple> sortBySecondDerivative(Matrix A) {
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(Tuple.descending);

        return list;
    }
}
