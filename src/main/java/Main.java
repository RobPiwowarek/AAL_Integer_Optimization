import Jama.Matrix;
import sun.awt.EventListenerAggregate;

import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {

       Matrix A = MatrixReader.readMatrixFromFile("A.txt");
       Matrix B = MatrixReader.readMatrixFromFile("B.txt");
       // preprocessing phase

        // ustal kolejnosc zmiennych malejaca 2 pochodna
        // mozna to zrobic biorac wartosci na przekatnej macierzy A gdyz sa to wspolczynniki przy kwadratach xi*xi for i = 1..n
        // wylicz macierze dla kazdej z wyliczonych zmiennych?

        Matrix continuousMinimum = A.inverse().times(B).times(-0.5);

        Matrix floorFixed = Evaluator.fixToFloorInteger(continuousMinimum, 0);
        Matrix ceilFixed = Evaluator.fixToCeilInteger(continuousMinimum, 0);
        Matrix x;

        double lowerBound = 0.0;
        int d = 1;
        double value = 0.0;
        Matrix min;

        // warunek wyjscia?
        while (true){

            // todo: optimization
            if (Evaluator.evaluateExpression(floorFixed, A, B) < Evaluator.evaluateExpression(ceilFixed, A, B)){
                x = floorFixed;
            }
            else {
                x = ceilFixed;
            }

            value = Evaluator.evaluateExpression(x, A, B);

            if (value < lowerBound){
                lowerBound = value;
            }

            ++d;
        }
    }
}






























//
//    int d = 0;
//    double upperBound = Double.MAX_VALUE;
//// algorithm while phase
//        while (d >= 0) {
//                // define f_
//                // L_, C_
//
//                // calc minimum
//                Matrix inv = A.inverse();
//                Matrix min = inversed.times(B).times(-0.5);
//
//                System.out.println(Evaluator.evaluateExpression(continuousMinimum, A, B));
//                //
//
//                double lowerBound = Evaluator.evaluateExpression(continuousMinimum, A, B);
//
//                // todo: optimization
//                for (int j = d; j < continuousMinimum.getColumnDimension()-d-1; ++j){
//        continuousMinimum = Evaluator.fixToNearestInteger(continuousMinimum, j);
//        }
//
//        // todo: optimization
//        if (Evaluator.evaluateExpression(continuousMinimum, A, B) < upperBound){
//        upperBound = Evaluator.evaluateExpression(continuousMinimum, A, B);
//        }
//
//        if (lowerBound < upperBound){
//        ++d;
//
//        }
//
//        }