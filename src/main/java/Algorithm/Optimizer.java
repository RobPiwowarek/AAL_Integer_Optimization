package Algorithm;

import Jama.Matrix;
import JamaUtils.JamaUtils;
import java.util.ArrayList;
import java.util.Vector;

public class Optimizer {

    public Optimizer() {

    }

    public void optimize(Matrix A, Matrix B) {
        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        sortBySecondDerivative(A, B);

        int n = A.getRowDimension();

        int numberOfCalls[] = new int[n];

        double upperBound = Double.MAX_VALUE;//Algorithm.Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        ArrayList<Matrix> fixedPoints = new ArrayList<>();

        Vector<ArrayList<Matrix>> points = new Vector<>(n);

        Vector<Matrix> Bs = new Vector<>(n);
        Bs.add(B);

        Vector<Double> Cs = new Vector<>(n);
        Cs.add(0.0);

        for (int i = 0; i < n; ++i) {
            points.add(new ArrayList<>());
        }

        ArrayList<Matrix> minors = new ArrayList<>(n);
        minors.add(A);
        calculateMinors(minors, A);

        Matrix a = A;
        Matrix b = B;
        double c = 0.0;
        Matrix savedX = null;

        System.out.println("A:");
        a.print(3, 6);
        System.out.println("B:");
        b.print(3, 6);

        for (int d = 0; d < n; ++d) {
            Matrix continuousMinimum = a.inverse().times(b).times(-0.5);

            if (savedX == null) {
                savedX = Matrix.constructWithCopy(continuousMinimum.getArray());
                System.out.println("Continious minimum: ");
                continuousMinimum.print(3, 6);
                System.out.println("Value: " + Evaluator.evaluateExpression(continuousMinimum, A, B, c));
                System.out.println("Closest int val: " + Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B, 0.0));
            }

            // fixing
            if (numberOfCalls[d] == 0) {
                points.get(d).add(fix(continuousMinimum, numberOfCalls));
                points.get(d).add(fix(continuousMinimum, numberOfCalls));
                points.get(d).add(fix(continuousMinimum, numberOfCalls));
            } else {
                points.get(d).add(fix(continuousMinimum, numberOfCalls));
            }
            // choose branching point
            Matrix branch = chooseMinimum(points.get(d), a, b, c);

            double value = Evaluator.evaluateExpression(branch, a, b, c);

            // conditions
            if (value > upperBound) {
                points.get(d).clear(); //

                Bs.remove(d);
                Cs.remove(d);

                // we went back and there is no more points we could branch on
                if (d == 0)
                    break;

                a = minors.get(d - 1);
                b = Bs.get(d - 1);
                c = Cs.get(d - 1);

                d -= 2; // go back
            } else {
                savedX.set(d, 0, branch.get(0, 0));

                if (d != n - 1)
                    mergeReducedXWithX(savedX, getReducedX(branch), d);

                points.get(d).remove(branch);

                if (isSolutionInteger(savedX)) {
                    if (value < upperBound) {
                        upperBound = value;
                        fixedPoints.add(Matrix.constructWithCopy(savedX.getArray()));
                    }
                    --d;
                } else {
                    c = getConstant(a, b, branch);
                    b = getMinorMatrixB(b, a, branch);
                    a = minors.get(d + 1);

                    Bs.add(b);
                    Cs.add(c);
                }
            }
        }

        System.out.println("end");

        Matrix minimum = chooseMinimum(fixedPoints, A, B, 0.0);

        System.out.println(Evaluator.evaluateExpression(minimum, A, B, 0.0));

        minimum.print(3, 6);
    }

    public long timedOptimize(int iterations, Matrix A, Matrix B) {

        for (int i = 0; i < iterations; ++i) {
            optimize(A, B);
        }

        System.gc();

        long start = System.nanoTime();

        optimize(A, B);

        long end = System.nanoTime();

        return (end - start) / 1000000;
    }

    private Matrix chooseMinimum(ArrayList<Matrix> points, Matrix A, Matrix B, double c) {
        Matrix minMat;
        double currentMin;

        minMat = points.get(0);
        currentMin = Evaluator.evaluateExpression(minMat, A, B, c);

        for (Matrix x : points) {
            double val = Evaluator.evaluateExpression(x, A, B, c);

            if (val < currentMin) {
                currentMin = val;
                minMat = x;
            }
        }

        return minMat;
    }

    private Matrix fix(Matrix m, int[] calls) {
        int index = calls.length - m.getRowDimension();
        int numberOfCalls = calls[index];

        Matrix x = Matrix.constructWithCopy(m.getArray());

        double diff = Math.floor((numberOfCalls) / 2);

        double val = m.get(0, 0);

        if (Math.round(val) == Math.floor(val)) {
            if (calls[index] % 2 != 0) {
                x.set(0, 0, Math.floor(val) - diff);
            } else
                x.set(0, 0, Math.ceil(val) + diff);
        } else {
            if (calls[index] % 2 == 0) {
                x.set(0, 0, Math.floor(val) - diff);
            } else
                x.set(0, 0, Math.ceil(val) + diff);
        }

        ++calls[index];

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

    private Matrix getReducedX(Matrix x) {
        double array[][] = new double[x.getRowDimension() - 1][1];

        for (int i = 0; i < x.getRowDimension() - 1; ++i) {
            array[i][0] = x.getArray()[i + 1][0];
        }

        return new Matrix(array);
    }

    private void calculateMinors(ArrayList<Matrix> minorsA, Matrix A) {
        Matrix a = A;

        for (int i = 0; i < A.getRowDimension() - 1; ++i) {
            a = getMinorMatrix(a);

            minorsA.add(a);
        }
    }

    private Matrix getMinorMatrix(Matrix original) {
        int y = original.getRowDimension() - 1;
        int x = original.getColumnDimension() - 1;

        double array[][] = new double[y][x];

        double originalArray[][] = original.getArray();

        for (int i = 1; i < y + 1; ++i) {
            for (int j = 1; j < x + 1; ++j) {
                array[i - 1][j - 1] = originalArray[i][j];
            }
        }

        return new Matrix(array);
    }

    private Matrix getMinorMatrixB(Matrix B, Matrix A, Matrix x) {
        Matrix b = new Matrix(new double[B.getRowDimension() - 1][1]);

        for (int i = 0; i < x.getRowDimension() - 1; ++i) {
            double val = A.get(i + 1, 0) * x.get(0, 0) + A.get(0, i + 1) * x.get(0, 0) + B.get(i + 1, 0);
            b.set(i, 0, val);
        }

        return b;
    }

    private double getConstant(Matrix A, Matrix B, Matrix x) {
        return A.get(0, 0) * Math.pow(x.get(0, 0), 2) + B.get(0, 0) * x.get(0, 0);
    }

    private void mergeReducedXWithX(Matrix x, Matrix reduced, int index) {
        for (int i = 0; i < reduced.getRowDimension(); ++i) {
            x.set(i + index + 1, 0, reduced.get(i, 0));
        }
    }

    private ArrayList<Tuple> sortBySecondDerivative(Matrix A, Matrix B) {
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(Tuple.descending);

        for (int i = 0; i < list.size(); ++i) {
            int key = list.get(i).key;

            if (key != i) {
                swapColumns(A, i, key);
                swapRows(A, i, key);
                swapRows(B, i, key);
            }
        }

        return list;
    }

    private void swapColumns(Matrix m, int i, int j) {
        Matrix tempCol = JamaUtils.getcol(m, i);
        JamaUtils.setcol(m, i, JamaUtils.getcol(m, j));
        JamaUtils.setcol(m, j, tempCol);
    }

    private void swapRows(Matrix m, int i, int j) {
        Matrix tempCol = JamaUtils.getrow(m, i);
        JamaUtils.setrow(m, i, JamaUtils.getrow(m, j));
        JamaUtils.setrow(m, j, tempCol);
    }

}
