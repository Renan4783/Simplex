/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplex;

import java.util.List;

/**
 *
 * @author Renan
 */
public class Simplex {

    private Problema problema = new Problema();

    private boolean fase1 = false;

    private double[] xB;
    private double[] lambda;
    private int k;
    private double min;
    private double[] custos;
    private double[] y;
    private int e;
    private int tArtificial = 0;

    public void calcula() {
        int tamanho = 0;
        int numVariaveis = getProblema().getRestricoes().get(1).getValues().length;
        verificaRestricao();
        checaFase1();
        if (fase1) {
            resolveFase1();
            /*
            for (int i = 0; i < getProblema().getvBasica().size(); i++) {
                double valor = getProblema().getvBasica().get(i);
                if (valor >= getProblema().getFuncao().size() + 1) {
                    System.out.println("Problema infactivel porque ficou variavel artificial na base");
                    return;
                }
            }
            */
        }
        if (!fase1) {
            formaPadrao(tamanho, numVariaveis);
        } else {
            // Completar as nbasicas com as certas
            getProblema().getvNBasica().clear();

            for (int i = 1; i < getProblema().getFuncao().size(); i++) {
                if (!getProblema().getvBasica().contains(i)) {
                    getProblema().getvNBasica().add(i);
                }
            }
        }
        printFuncao();
        ResolveSimplex();
    }
    
    public void checaFase1() {
        if (fase1) {
            return;
        }

        // Checa se precisa da fase 1
        for (Expressao ex : problema.getRestricoes()) {
            if (ex.getTipo().equals(">=") || ex.getTipo().equals("=")) {
                System.out.println("O problema precisa passar pela fase 1" + " " + ex.getTipo());
                this.setFase1(true);
                return;
            }
        }

        System.out.println("O problema nao precisa da fase 1");
    }

    public void resolveFase1() {
        int tamanho, restricoes = 0;
        int numVariaveis = getProblema().getRestricoes().get(1).getValues().length;
        tamanho = getProblema().getRestricoes().size();
        List<Integer> artificialvBasica;
        List<Integer> artificialvNBasica;
        Problema problemaArtificial = problema.clone();
        Simplex simplex = new Simplex();
        simplex.setProblema(problemaArtificial);
        simplex.setFase1(true);
        simplex.calculaFaseI(tamanho, numVariaveis);
        simplex.printFuncao();
        artificialvBasica = problemaArtificial.getvBasica();
        artificialvNBasica = problemaArtificial.getvNBasica();
        for (int i = 0; i < restricoes; i++) {
            artificialvNBasica.remove(i);
        }
        problema.setvBasica(artificialvBasica);
        problema.setvNBasica(artificialvNBasica);
        System.out.println("\nFim da fase I \n");
        for (int v = 0; v < getProblema().getRestricoes().get(0).getValues().length - 2; v++) {
            problema.getFuncaoPadrao().addValue(0);
        }
    }

    public void calculaFaseI(int tamanho, int numVariaveis) {
        formaPadraoFaseI(tamanho, numVariaveis);
        formaPadraoArtificiais(tamanho, numVariaveis);
        printFuncao();
        ResolveSimplex();
    }

    private void verificaRestricao() {
        for (Expressao ex : getProblema().getRestricoes()) {
            if (ex.getB() < 0) {

                for (int i = 0; i < ex.getValues().length; i++) {
                    ex.getValues()[i] *= -1;
                }

                ex.setB(ex.getB() * -1);

                switch (ex.getTipo()) {
                    case ">=":
                        ex.setTipo("<=");
                        break;

                    case "<=":
                        ex.setTipo(">=");
                }
            }
        }
    }

    
    public void ResolveSimplex() {
        int i = 1;
        System.out.println("\nIteração " + i);
        System.out.println("");

        System.out.println("Matriz Base");
        printMatrix(getProblema().getMatrizBasicas());
        System.out.println("");

        calculaXb();
        acharLambda();

        calculaCustoRelativo();

        while (!checaParadaCusto()) {
            calculaDirecaoSimplex(); //Calcula o Y
            calculaCandidato(); //Verifica se o Y é negativo e verifica quem sai da base
            atualizaVariaveis(); //Atualiza as listas de acordo com E e k

            i++;
            System.out.println("\nIteracao " + i);
            System.out.println("\nPasso 1");
            System.out.println("Matriz Base");
            printMatrix(getProblema().getMatrizBasicas());
            System.out.println("");

            calculaXb();
            acharLambda();

            calculaCustoRelativo();
        }
    }
    
    /**
     * Passo 1
     *
     * @param fase1
     */
    public void calculaXb() {
        xB = getProblema().getXb();
        System.out.println("Valor Xb");
        printMatrix2(xB);
        System.out.println("");
    }

    /**
     * Passo 2.1
     */
    public void acharLambda() {
        double[][] transposto = Utilidades.calcularTransposta(getProblema().getMatrizBasicas());

        double[] b = getProblema().getCustosBasicas();

        lambda = Gauss.gauss(transposto, b);
        System.out.println("Passo 2.1");
        System.out.println("Valor Lambda");
        printMatrix2(lambda);
        System.out.println("");
    }

    /**
     * Passo 2.2 - Calcula o custo relativo das variaveis não básicas
     */
    public void calculaCustoRelativo() {
        System.out.println("Passo 2.2");
        setCustos(new double[getProblema().getvNBasica().size()]);
        setMin(Double.MAX_VALUE);

        // 2.2
        for (int i = 0; i < getProblema().getvNBasica().size(); i++) {
            int vb = getProblema().getvNBasica().get(i);

            getCustos()[i] = getProblema().getCusto(vb) - (Utilidades.multiplicarVetores(lambda, getProblema().getColuna(vb)));

            // 2.3
            if (getCustos()[i] < getMin()) {
                k = i + 1;
                setMin(getCustos()[i]);
            }

            System.out.println("Custo relativo da variavel nao basica " + (i + 1) + " (" + getCustos()[i] + ")");
        }

        System.out.println("Candidata a sair da base: k= " + k);
        System.out.println("");
    }

    /**
     * Passo 3 - Teste de otimalidade
     */
    public boolean checaParadaCusto() {
        boolean todosPositivos = true;

        for (double custo : getCustos()) {
            if (custo < 0) {
                todosPositivos = false;
            }
        }
        System.out.println("Passo 3");
        if (todosPositivos) {
            System.out.println("Esta na solução otima");

            System.out.println("");
            System.out.println("Solução: ");

            //Pega o xb atual e joga na solução, calculando f(x)
            double[] xBFinal = new double[getProblema().getFuncao().size()];
            double[] x = new double[getProblema().getFuncaoPadrao().size()];

            for (int i = 0; i < getProblema().getvBasica().size(); i++) {
                x[getProblema().getvBasica().get(i) - 1] = xB[i];
            }

            for (int i = 0; i < xBFinal.length; i++) {
                xBFinal[i] = x[i];
            }

            double fx = Utilidades.multiplicarVetores(getProblema().getFuncao().getValues(), xBFinal);

            if (getProblema().getObjetivo().equals("max")) {
                fx *= -1;
            }

            System.out.println("f(x) = " + fx);
            printMatrix2(xBFinal);
        } else {
            System.out.println("Não esta na soluçao otima, possui custos negativos");
        }

        return todosPositivos;
    }

    /**
     * Passo 4 - Calcula a direção simplex
     */
    public void calculaDirecaoSimplex() {
        System.out.println("\nPasso 4");

        System.out.println("Calculando y");
        printMatrix(getProblema().getMatrizBasicas());
        System.out.println("x");
        printMatrix2(getProblema().getColuna(getProblema().getvNBasica().get(k - 1)));

        setY(Gauss.gauss(getProblema().getMatrizBasicas(), getProblema().getColuna(getProblema().getvNBasica().get(k - 1))));

        System.out.println("");
        System.out.println("Valor do y");
        printMatrix2(getY());
        System.out.println("");
    }

    /**
     * Passo 5 - Calcular candidato a sair da base
     */
    public void calculaCandidato() {
        System.out.println("Passo 5");
        if (!checaYPositivo()) {
            System.out.println("O problema possui solucoes ilimitadas");
            System.exit(0);
        }

        double emin = Double.MAX_VALUE;
        setE(0);
        double min;

        for (int i = 0; i < getY().length; i++) {
            if (getY()[i] > 0) {
                min = getProblema().getXb()[i] / getY()[i];
                if (min < emin) {
                    emin = min;
                    setE(i + 1);
                }
            }
        }

        System.out.println("Menor e = " + getE() + " (" + emin + ") ");
    }

    /**
     * Passo 6 - Atualiza as variaveis
     */
    public void atualizaVariaveis() {
        //printVariaveis();

        System.out.println("\nPasso 6");
        System.out.println("Sai da base: " + getE());
        System.out.println("Entra na base: " + k);

        Integer saiBase = getProblema().getvBasica().get(getE() - 1);
        Integer entraBase = getProblema().getvNBasica().get(k - 1);

        getProblema().getvBasica().set(getE() - 1, entraBase);
        getProblema().getvNBasica().set(k - 1, saiBase);

        System.out.println("Novas variaveis");
        printVariaveis();
    }

    public boolean checaYPositivo() {
        boolean positivo = false;

        for (int i = 0; i < getY().length; i++) {
            if (getY()[i] > 0) {
                positivo = true;
            }
        }

        return positivo;
    }

    public void formaPadrao(int tamanho, int numVariaveis) {
        //Calcula o numero de variaveis
        System.out.println("Numero de variaveis: " + numVariaveis);
        //Calcula o numero de restricoes
        tamanho = getProblema().getRestricoes().size();
        System.out.println("Total de colunas que necessitam ser adicionadas: " + tamanho);

        if (getProblema().getvBasica().isEmpty()) {
            for (int i = 0; i < getProblema().getRestricoes().size(); i++) {
                for (int j = 0; j < getProblema().getRestricoes().size(); j++) {
                    if (i == j) {
                        if (getProblema().getRestricoes().get(i).getTipo().equals(">=")) {
                            getProblema().getRestricoes().get(i).addValue(-1); //se for >= o custo é -1
                        } else {
                            getProblema().getRestricoes().get(i).addValue(1); //senao é 1
                        }
                        getProblema().addBasica(numVariaveis + j + 1);
                    } else {
                        getProblema().getRestricoes().get(i).addValue(0);
                    }
                }
            }
        }

        for (int i = 0; i < numVariaveis; i++) {
            getProblema().addNBasica(i + 1);
        }

        for (int v = 0; v < getProblema().getRestricoes().get(0).getValues().length - 2; v++) {
            getProblema().getFuncaoPadrao().addValue(0);
        }
    }

    public void formaPadraoFaseI(int tamanho, int posI) {
        //Calcula o numero de variaveis
        System.out.println("Numero de variaveis: " + posI);
        //Calcula o numero de restricoes

        tamanho = getProblema().getRestricoes().size();
        System.out.println("Total de restricoes: " + tamanho);
        if (getProblema().getvBasica().isEmpty()) {
            for (int i = 0; i < tamanho; i++) {
                for (int j = 0; j < tamanho; j++) {
                    if (i == j) {
                        if (getProblema().getRestricoes().get(i).getTipo().equals(">=") || getProblema().getRestricoes().get(i).getTipo().equals(">")) {
                            getProblema().getRestricoes().get(i).addValue(-1); //se for >= o custo é -1
                        } else {
                            getProblema().getRestricoes().get(i).addValue(1); //senao é 1
                        }
                        getProblema().addNBasica(posI + j + 1);
                    } else {
                        getProblema().getRestricoes().get(i).addValue(0);
                    }
                }
            }
        }

        for (int i = 0; i < posI; i++) {
            getProblema().addNBasica(i + 1);
        }

        for (int v = 0; v < (tamanho+posI); v++) {
            getProblema().getFuncaoPadrao().addValue(0);
        }
    }

    private void formaPadraoArtificiais(int tamanho, int posI) {
        tamanho = getProblema().getRestricoes().size();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (i == j) {
                    getProblema().getRestricoes().get(i).addValue(1);
                    getProblema().addBasica((posI + tamanho) + j + (tamanho - (tamanho - 1)));
                } else {
                    getProblema().getRestricoes().get(i).addValue(0);
                }
            }
        }
        for (int v = 0; v < getProblema().getRestricoes().get(0).getValues().length - (tamanho + posI); v++) {
            getProblema().getFuncaoPadrao().addValue(1);
        }
    }
    
    public void printFuncao() {
        System.out.println("");
        System.out.print(getProblema().getObjetivo() + " f(x) = ");
        int i = 1;
        for (Double v : getProblema().getFuncaoPadrao().getValues()) {
            System.out.print(v + "x" + i + "\t");
            i++;
        }
        System.out.println("");
        System.out.println("");

        for (Expressao e : getProblema().getRestricoes()) {
            i = 1;
            for (Double v : e.getValues()) {
                System.out.print(v + "x" + i + "\t");
                i++;
            }

            System.out.print(e.getTipo() + "\t");

            System.out.println(e.getB());
        }

        printVariaveis();
    }

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

    public static void printMatrix2(double[] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(matrix[i]);
        }
    }

    public void printVariaveis() {
        int i = 1;
        for (int b : getProblema().getvBasica()) {
            System.out.print("B" + i + ": " + b + " ");
            i++;
        }

        System.out.println("");
        i = 1;
        for (int b : getProblema().getvNBasica()) {
            System.out.print("N" + i + ": " + b + " ");
            i++;
        }

        System.out.println("");
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the custos
     */
    public double[] getCustos() {
        return custos;
    }

    /**
     * @param custos the custos to set
     */
    public void setCustos(double[] custos) {
        this.custos = custos;
    }

    /**
     * @return the y
     */
    public double[] getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double[] y) {
        this.y = y;
    }

    /**
     * @return the e
     */
    public int getE() {
        return e;
    }

    /**
     * @param e the e to set
     */
    public void setE(int e) {
        this.e = e;
    }

    /**
     * @return the tArtificial
     */
    public int gettArtificial() {
        return tArtificial;
    }

    /**
     * @param tArtificial the tArtificial to set
     */
    public void settArtificial(int tArtificial) {
        this.tArtificial = tArtificial;
    }

    /**
     * @param problema the problema to set
     */
    public void setProblema(Problema problema) {
        this.problema = problema;
    }

    /**
     * @return the problema
     */
    public Problema getProblema() {
        return problema;
    }

    /**
     * @param fase1 the fase1 to set
     */
    public void setFase1(boolean fase1) {
        this.fase1 = fase1;
    }
}
