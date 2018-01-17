package Algorithm;

import Jama.Matrix;
import JamaUtils.JamaUtils;

import java.util.ArrayList;
import java.util.Vector;

public class Optimizer {

    public Optimizer() {

    }

    public void optimize(Matrix A, Matrix B) {
        Matrix originalA = Matrix.constructWithCopy(A.getArray());
        Matrix originalB = Matrix.constructWithCopy(B.getArray());

        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        sortBySecondDerivative(A, B);

        int n = A.getRowDimension();

        int numberOfCalls[] = new int[n];

        ArrayList<Matrix> fixedPoints = new ArrayList<>();
        Vector<ArrayList<Matrix>> points = initPoints(n);
        Vector<Matrix> Bs = new Vector<>(n);
        Bs.add(B);
        Vector<Double> Cs = new Vector<>(n);
        Cs.add(0.0);
        ArrayList<Matrix> minors = initMinors(A, n);

//        System.out.println("A:");
//        a.print(3, 6);
//        System.out.println("B:");
//        b.print(3, 6);

        Matrix a = A;
        Matrix b = B;
        double c = 0.0;
        Matrix savedX = null;
        double upperBound = Double.MAX_VALUE;//Algorithm.Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        double _debugClosestIntVal = 0.0;

        for (int d = 0; d < n; ++d) {
            Matrix continuousMinimum = null;

            try {
                 continuousMinimum = a.inverse().times(b).times(-0.5);
            }
            catch (RuntimeException e) {
                System.out.println("runtime: ");
                a.print(3, 6);
                System.out.println("b: ");
                b.print(3, 6);
                System.out.println("A: ");
                originalA.print(3, 6);
                System.out.println("B: ");
                originalB.print(3, 6);
                System.exit(-1);
            }

//            System.out.println("Value: " + Evaluator.evaluateExpression(continuousMinimum, a, b, c));

            if (savedX == null) {
                savedX = Matrix.constructWithCopy(continuousMinimum.getArray());
                System.out.println("Continious minimum: ");
                continuousMinimum.print(3, 6);
                //System.out.println("Value: " + Evaluator.evaluateExpression(continuousMinimum, A, B, c));
                _debugClosestIntVal = Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B, 0.0);
                //System.out.println("Closest int val: " + _debugClosestIntVal);
            }

            // fixing
            if (numberOfCalls[d] == 0) {
                points.get(d).add(fix(continuousMinimum, numberOfCalls, d));
                points.get(d).add(fix(continuousMinimum, numberOfCalls, d));
                points.get(d).add(fix(continuousMinimum, numberOfCalls, d));
            } else {
                points.get(d).add(fix(continuousMinimum, numberOfCalls, d));
            }
            // choose branching point
            Matrix branch = chooseMinimum(points.get(d), a, b, c);

            System.out.println("branch: ");
            branch.print(3,  6);

            double value = Evaluator.evaluateExpression(branch, a, b, c);

//            System.out.println("branch min val: " + Evaluator.evaluateExpression(branch, a, b, c));
//            System.out.println("savedx val: " + Evaluator.evaluateExpression(savedX, A, B, 0.0));

//            for (Matrix x: points.get(d)){
//                System.out.println("X:");
//                x.print(3, 6);
//                System.out.println("val: " + Evaluator.evaluateExpression(x, a, b, c));
//            }

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
                savedX.set(d, 0, 1.5);

                d -= 2; // go back
            } else {
                savedX.set(d, 0, branch.get(0, 0));

                if (d != n - 1)
                    mergeReducedXWithX(savedX, branch, d);

                points.get(d).remove(branch);

                if (isSolutionInteger(savedX)) {
                    if (value < upperBound) {
                        upperBound = value;
                        System.out.println("FOUND MINIMA: ");
                        savedX.print(3, 6);
                        fixedPoints.add(Matrix.constructWithCopy(savedX.getArray()));
                    }
                    --d;
                } else {
                    c = getConstant(a, b, branch, c);
                    b = getMinorMatrixB(b, a, branch);
                    a = minors.get(d + 1);

                    Bs.add(b);
                    Cs.add(c);
                }
            }
        }

        Matrix minimum = chooseMinimum(fixedPoints, A, B, 0.0);

        double val = Evaluator.evaluateExpression(minimum, A, B, 0.0);

        System.out.println("MINIMUM: " + val);
        System.out.println("_Debug: " + _debugClosestIntVal);

        if (val > _debugClosestIntVal){
            System.out.println("FAILURE OF ALGORITHM");
            System.out.println("A");
            A.print(3, 6);
            System.out.println("B");
            B.print(3, 6);
        }

        //minimum.print(3, 6);
    }

    private Vector<ArrayList<Matrix>> initPoints(int n) {
        Vector<ArrayList<Matrix>> points = new Vector<>(n);
        for (int i = 0; i < n; ++i) {
            points.add(new ArrayList<>());
        }
        return points;
    }

    private ArrayList<Matrix> initMinors(Matrix A, int n) {
        ArrayList<Matrix> minors = new ArrayList<>(n);
        minors.add(A);
        calculateMinors(minors, A);
        return minors;
    }

    public double timedOptimize(int iterations, Matrix A, Matrix B) {
        for (int i = 0; i < iterations; ++i) {
            optimize(A, B);
        }

        long start = System.nanoTime();
        optimize(A, B);
        long end = System.nanoTime();

        return (end - start) / 1000000.0;
    }

    private Matrix chooseMinimum(ArrayList<Matrix> points, Matrix A, Matrix B, double c) {
        Matrix minMat;
        double currentMin;

        minMat = points.get(0);
        currentMin = Evaluator.evaluateExpression(minMat, A, B, c);

        for (Matrix x : points) {
            double val = Evaluator.evaluateExpression(x, A, B, c);

            System.out.println("chooseMin val: " + val);
            System.out.println("x: ");
            x.print(3, 6);

            if (val < currentMin) {
                currentMin = val;
                minMat = x;
            }
        }

        return minMat;
    }

    private Matrix fix(Matrix m, int[] calls, int d) {
        int numberOfCalls = calls[d];

        Matrix x = Matrix.constructWithCopy(m.getArray());

        double diff = Math.floor((numberOfCalls) / 2);

        double val = m.get(0, 0);

        if (Math.round(val) == Math.floor(val)) {
            if (calls[d] % 2 != 0) {
                x.set(0, 0, Math.floor(val) - diff);
            } else
                x.set(0, 0, Math.ceil(val) + diff);
        } else {
            if (calls[d] % 2 == 0) {
                x.set(0, 0, Math.floor(val) - diff);
            } else
                x.set(0, 0, Math.ceil(val) + diff);
        }

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

    private double getConstant(Matrix A, Matrix B, Matrix x, double prevC) {
        return A.get(0, 0) * Math.pow(x.get(0, 0), 2) + B.get(0, 0) * x.get(0, 0) + prevC;
    }

    private void mergeReducedXWithX(Matrix x, Matrix reduced, int index) {
        for (int i = index; i < x.getRowDimension(); ++i) {
            x.set(i, 0, reduced.get(i-index, 0));
        }
    }

    private void sortBySecondDerivative(Matrix A, Matrix B) {
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