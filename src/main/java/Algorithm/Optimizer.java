package Algorithm;

import Jama.Matrix;
import JamaUtils.JamaUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class Optimizer {

    public Optimizer() {

    }

    public void optimize(Matrix A, Matrix B) {
//        System.out.println("A:");
//        A.print(3, 6);
//        System.out.println("B:");
//        B.print(3, 6);

        if (!JamaUtils.isSymmetric(A)) {
            JamaUtils.makeMatrixSymmetric(A, JamaUtils.Function.MEAN);
        }

        ArrayList<Tuple> mapping = sortBySecondDerivative(A, B);

        Matrix continuousMinimum = A.inverse().times(B).times(-0.5);
        B = B.transpose();

        System.out.println("continuousMinimum:");
        //continuousMinimum.print(3, 6);

        System.out.println("value: " + Evaluator.evaluateExpression(continuousMinimum, A, B));

        if (isSolutionInteger(continuousMinimum)) {
            continuousMinimum.print(continuousMinimum.getColumnDimension(), 5);
            return;
        }

        int n = continuousMinimum.getRowDimension();

        int numberOfCalls[] = new int[n];

        double upperBound = Double.MAX_VALUE;//Algorithm.Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B);

        Matrix x = continuousMinimum;

        ArrayList<Matrix> fixedPoints = new ArrayList<>();

        Vector<ArrayList<Matrix>> points = new Vector<>(n);

        for (int i = 0; i < n; ++i)
            points.add(new ArrayList<>());

        ArrayList<Matrix> minors = new ArrayList<>(n);

        for (int d = 0; d < n; ++d) {
            if (numberOfCalls[d] == 0) {
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));
            } else
                points.get(d).add(fixAtPosition(x, d, numberOfCalls, continuousMinimum));

            x = chooseMinimum(points.get(d), A, B);

            double val = Evaluator.evaluateExpression(x, A, B);

            //System.out.println("d: " + d + " val: " + val + " ");
            // todo: znalezc minimum dla pod problemu tzn. rzeczywistoliczbowe "pomijajac" juz calkowitoliczbowe

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

        System.out.println("closest-fix value: " + Evaluator.evaluateExpression(fixEntireMatrixToClosestInteger(continuousMinimum), A, B));

        System.out.println("end");

        Matrix minimum = chooseMinimum(fixedPoints, A, B);

        System.out.println(Evaluator.evaluateExpression(minimum, A, B));

        //minimum.print(3, 6);
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

    private Matrix fixAtPosition(Matrix m, int d, int[] calls, Matrix continuousMinimum) {
        int numberOfCalls = calls[d];

        Matrix x = Matrix.constructWithCopy(m.getArray());

        double diff = Math.floor((numberOfCalls) / 2);

        double val = continuousMinimum.get(d, 0);

        if (Math.round(val) == Math.floor(val)){
            if (calls[d] % 2 != 0) {
                x.set(d, 0, Math.floor(continuousMinimum.get(d, 0)) - diff);
            } else
                x.set(d, 0, Math.ceil(continuousMinimum.get(d, 0)) + diff);

        }
        else {
            if (calls[d] % 2 == 0) {
                x.set(d, 0, Math.floor(continuousMinimum.get(d, 0)) - diff);
            } else
                x.set(d, 0, Math.ceil(continuousMinimum.get(d, 0)) + diff);

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

    private ArrayList<Tuple> sortBySecondDerivative(Matrix A) {
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(Tuple.descending);

        return list;
    }

    private void calculateMinors(ArrayList<Matrix> minorsA, ArrayList<Matrix> minorsB, Matrix A, Matrix B, Matrix x){
        Matrix a = A;
        Matrix b = B;

        for (int i = 0; i < A.getRowDimension(); ++i){
            Matrix tmpA = getMinorMatrix(a);
            Matrix tmpB = getMinorMatrixB(b, a, x);
            
            a = tmpA;
            b = tmpB;

            minorsA.add(a);
            minorsB.add(b);
        }
    }

    private Matrix getMinorMatrix(Matrix original){
        int y = original.getRowDimension()-1;
        int x = original.getColumnDimension()-1;

        double array[][] = new double[y][x];

        double originalArray[][] = original.getArray();

        for (int i = 1; i < y+1; ++i){
            for (int j = 1; j < x+1; ++j){
                array[i][j] = originalArray[i][j];
            }
        }

        return new Matrix(array);
    }

    private Matrix getMinorMatrixB(Matrix B, Matrix A, Matrix x){
        Matrix b = new Matrix(B.getRowDimension()-1, 1);

        for (int i = 0; i < x.getRowDimension()-1; ++i){
            double val = A.get(i+1, 0)*x.get(0,0) + A.get(0, i+1)*x.get(0,0) + B.get(i, 0);
            b.set(i, 0, val);
        }

        return b;
    }

    private double getConstant(Matrix A, Matrix B, Matrix x){
        return A.get(0, 0) * Math.sqrt(x.get(0,0)) + B.get(0, 0)*x.get(0,0);
    }

    private ArrayList<Tuple> sortBySecondDerivative(Matrix A, Matrix B){
        ArrayList<Tuple> list = new ArrayList<>();

        for (int i = 0; i < A.getColumnDimension(); ++i) {
            list.add(new Tuple(i, A.get(i, i)));
        }

        list.sort(Tuple.descending);

        for (int i = 0; i < list.size(); ++i){
            int key = list.get(i).key;

            if (key != i){
                swapColumns(A, i, key);
                swapRows(A, i, key);
                swapRows(B, i, key);
            }
        }

        return list;
    }

    private void swapColumns(Matrix m, int i, int j){
        Matrix tempCol = JamaUtils.getcol(m, i);
        JamaUtils.setcol(m, i, JamaUtils.getcol(m, j));
        JamaUtils.setcol(m, j, tempCol);
    }

    private void swapRows(Matrix m, int i, int j){
        Matrix tempCol = JamaUtils.getrow(m, i);
        JamaUtils.setrow(m, i, JamaUtils.getrow(m, j));
        JamaUtils.setrow(m, j, tempCol);
    }
}
