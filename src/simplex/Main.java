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
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Simplex simplex = new Simplex();
        Problema problema = new Problema();
        problema.setObjetivo("max");
        problema.setFuncao(new Expressao(6, 2));
        problema.addRestricao(new Expressao("<=", 33, 3, 1));
        problema.addRestricao(new Expressao("<=", 13, 1, 1));
        simplex.setProblema(problema);
        simplex.calcula();
    }
}
