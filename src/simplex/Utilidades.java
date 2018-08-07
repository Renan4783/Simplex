/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplex;

/**
 *
 * @author Renan
 */
public class Utilidades {

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    public static void printMatrix(double[] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(matrix[i] + "\t");
        }
        System.out.println("");

    }

    /**
     * Calcula a matriz transposta de uma matriz.
     *
     * @param matriz a matriz que será transposta.
     * @return a matriz transposta da passada por parâmetro.
     */
    public static double[][] calcularTransposta(double[][] matriz) {
        int m = matriz.length;
        int n = matriz[0].length;
        double[][] transposta = new double[m][n];

        copiarMatriz(transposta, matriz);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transposta[i][j] = matriz[j][i];
            }
        }
        return transposta;
    }

    /**
     * Copia uma matriz para outra de mesmo tamanho.
     *
     * @param destino a matriz de destino.
     * @param origem a matriz de origem dos dados.
     */
    public static void copiarMatriz(double[][] destino, double[][] origem) {
        int m = origem.length;
        int n = origem[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                destino[i][j] = origem[i][j];
            }
        }
    }

    /**
     * Multiplica dois vetores.
     *
     * @param a vetor A de dados.
     * @param b vetor B de dados.
     * @return vetor composto pela multiplicação dos vetores A e B.
     */
    public static double multiplicarVetores(double a[], double b[]) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[array.length];
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }
}
