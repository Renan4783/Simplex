/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Renan
 */
public class Problema {

    private String objetivo;

    private Expressao funcao;
    private Expressao funcaoPadrao;

    private List<Expressao> restricoes = new ArrayList<>();
    private List<Integer> vBasica = new ArrayList<>();
    private List<Integer> vNBasica = new ArrayList<>();

    public void setFuncao(Expressao expressao) {
        if (getObjetivo().equals("max")) {
            for (int i = 0; i < expressao.getValues().length; i++) {
                expressao.getValues()[i] *= -1;
            }
        }

        this.funcao = expressao;
        this.setFuncaoPadrao(expressao.clone());
    }

    public void addRestricao(Expressao expressao) {
        getRestricoes().add(expressao);
    }

    public void addBasica(int value) {
        getvBasica().add(value);
    }

    public void addNBasica(int value) {
        getvNBasica().add(value);
    }

    public double[][] getMatriz() {
        double[][] matriz = new double[getRestricoes().size()][getRestricoes().get(0).size()];

        for (int i = 0; i < getRestricoes().size(); i++) {
            for (int j = 0; j < getRestricoes().get(0).size(); j++) {
                matriz[i][j] = getRestricoes().get(i).getValues()[j];
            }
        }

        return matriz;
    }

    public double[][] getMatrizBasicas() {
        double[][] matrizBasica = new double[getvBasica().size()][getvBasica().size()];

        int basicas = this.getvBasica().size();
      
        System.out.println("Basicas: " + basicas);
        for (int i = 0; i < basicas; i++) {
            for (int j = 0; j < basicas; j++) {
                matrizBasica[i][j] = this.getRestricoes().get(i).getValues()[getvBasica().get(j)-1];
            }
        }

        return matrizBasica;
    }

    public double[][] getMatrizNaoBasicas() {
        double[][] matrizNaoBasicas = new double[getvBasica().size()][getvNBasica().size()];

        int Basicas = this.getvBasica().size();
        int naoBasicas = this.getvNBasica().size();
      
        System.out.println("Nao Basicas: " + naoBasicas);
        for (int i = 0; i < Basicas; i++) {
            for (int j = 0; j < naoBasicas; j++) {
                matrizNaoBasicas[i][j] = this.getRestricoes().get(i).getValues()[getvBasica().get(j) - 1];
            }
        }

        return matrizNaoBasicas;
    }
    
    double[] getCustosRestricoes() {
        double[] custos = new double[getRestricoes().size()];

        for (int i = 0; i < getRestricoes().size(); i++) {
            custos[i] = getRestricoes().get(i).getB();
        }

        return custos;
    }

    double[] getCustosBasicas() {
        double[] custos = new double[getvBasica().size()];

        for (int i = 0; i < getvBasica().size(); i++) {
            custos[i] = getFuncaoPadrao().getValues()[getvBasica().get(i) - 1];
        }

        return custos;
    }

    public double[] getXb() {
        double[][] a = getMatrizBasicas();
        double[] b = getCustosRestricoes();

        double[] xB = Gauss.gauss(a, b);
        return xB;
    }

    double getCusto(int i) {
        return getFuncaoPadrao().getValues()[i - 1];
    }

    double[] getColuna(int value) {
        double[][] matrix = getMatriz();
        double[] coluna = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            coluna[i] = matrix[i][value - 1];
        }

        return coluna;
    }

    public void addArtificial(int pos) {
        for (int i = 0; i < getRestricoes().size(); i++) {
            if (i == pos) {
                getRestricoes().get(i).addValue(1);
            } else {
                getRestricoes().get(i).addValue(0);
            }

        }

    }

    /**
     * @return the restricoes
     */
    public List<Expressao> getRestricoes() {
        return restricoes;
    }

    /**
     * @return the objetivo
     */
    public String getObjetivo() {
        return objetivo;
    }

    /**
     * @param objetivo the objetivo to set
     */
    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    /**
     * @return the funcao
     */
    public Expressao getFuncao() {
        return funcao;
    }

    /**
     * @return the funcaoPadrao
     */
    public Expressao getFuncaoPadrao() {
        return funcaoPadrao;
    }

    /**
     * @param funcaoPadrao the funcaoPadrao to set
     */
    public void setFuncaoPadrao(Expressao funcaoPadrao) {
        this.funcaoPadrao = funcaoPadrao;
    }

    /**
     * @param restricoes the restricoes to set
     */
    public void setRestricoes(List<Expressao> restricoes) {
        this.restricoes = restricoes;
    }

    /**
     * @return the vBasica
     */
    public List<Integer> getvBasica() {
        return vBasica;
    }

    /**
     * @param vBasica the vBasica to set
     */
    public void setvBasica(List<Integer> vBasica) {
        this.vBasica = vBasica;
    }

    /**
     * @return the vNBasica
     */
    public List<Integer> getvNBasica() {
        return vNBasica;
    }

    /**
     * @param vNBasica the vNBasica to set
     */
    public void setvNBasica(List<Integer> vNBasica) {
        this.vNBasica = vNBasica;
    }
    

    protected Problema clone() {
        Expressao funcaoArtificial = new Expressao();
        Problema problemaArtificial = new Problema();
        /*
        for (int i=0; i< getFuncao().size(); i++){
            funcaoArtificial.addValue(0);
        }
        */
        problemaArtificial.setObjetivo(getObjetivo());
        problemaArtificial.setFuncaoPadrao(funcaoArtificial);
        problemaArtificial.setFuncao(funcaoArtificial);
        problemaArtificial.setRestricoes(getRestricoes());
        return problemaArtificial;
    }
    

}
