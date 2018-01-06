import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class MatrixReader {

    public static Matrix readMatrixFromFile(String fileName) {
        int x = 3, y = 3;
        int rowsIndex = 0;

        double readArray[][];
        BufferedReader bufferedReader = null;

        try {
            String line;

            bufferedReader = new BufferedReader(new FileReader(fileName));

            if ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                x = Integer.parseInt(stringTokenizer.nextToken());
                y = Integer.parseInt(stringTokenizer.nextToken());

                if (stringTokenizer.hasMoreElements())
                    System.exit(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        readArray = new double[x][y];

        try {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                int columnsIndex = 0;

                StringTokenizer stringTokenizer = new StringTokenizer(line, " ");

                while (stringTokenizer.hasMoreElements()) {
                    String token = stringTokenizer.nextToken();
                    Double val = Double.parseDouble(token);
                    readArray[rowsIndex][columnsIndex++] = val;
                }

                ++rowsIndex;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                bufferedReader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return new Matrix(readArray);
    }
}
