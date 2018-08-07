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
public class Gauss {

    public static double[] gauss(double[][] matriz, double b[]) {

        if (matriz.length != b.length) {
            System.out.println("Não é possível resolver esse sistema porque não é um sistema quadrado!");
            return null;
        }

        double mult, temp;
        double[] vetorResposta = new double[b.length];
        /* Método de gauss */
        for (int k = 0; k < matriz.length; k++) {
            /* Pivoteamento parcial */
            double maxEl = Math.abs(matriz[k][k]);
            int linhaDoMax = k;

            // Procura o maximo em modulo na coluna //
            for (int i = k + 1; i < matriz.length; i++) {
                if (Math.abs(matriz[i][k]) > maxEl) {
                    maxEl = Math.abs(matriz[i][k]);
                    linhaDoMax = i;
                }
            }

            // Se o maximo for zero nao tem como calcular a resposta
            if (maxEl == 0) {
                return null;
            }

            // Troca linha atual com a linha de maior pivo //
            for (int i = 0; i < matriz.length; i++) {
                double temporaria = matriz[linhaDoMax][i];
                matriz[linhaDoMax][i] = matriz[k][i];
                matriz[k][i] = temporaria;
            }

            // Trocar também no vetor resposta
            double temporaria = b[linhaDoMax];
            b[linhaDoMax] = b[k];
            b[k] = temporaria;

            /* Triangularizacao */
            for (int i = k + 1; i < matriz.length; i++) {
                mult = matriz[i][k] / matriz[k][k];
                matriz[i][k] = 0;
                b[i] -= mult * b[k];
                for (int j = k + 1; j <= matriz.length - 1; j++) {
                    matriz[i][j] -= mult * matriz[k][j];
                }
            }
        }

        /* Substituição de cima para baixo */
        vetorResposta[b.length - 1] = b[b.length - 1] / matriz[matriz.length - 1][matriz.length - 1];
        for (int i = (matriz.length - 2); i >= 0; i--) {
            temp = b[i];
            for (int j = (i + 1); j < matriz.length; j++) {
                temp -= (matriz[i][j] * vetorResposta[j]);
            }
            vetorResposta[i] = temp / matriz[i][i];
        }

        return vetorResposta;
    }
}
