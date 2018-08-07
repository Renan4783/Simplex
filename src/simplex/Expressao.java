/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplex;

import java.util.Arrays;

/**
 *
 * @author Renan
 */
public class Expressao {

    private String tipo;
    private double[] values;
    private double b;

    public Expressao(double... i) {
        values = i;
    }

    public Expressao(String tipo, double b, double... i) {
        this.tipo = tipo;
        this.values = i;
        this.b = b;
    }

    void addValue(double d) {
        setValues(Arrays.copyOf(getValues(), getValues().length + 1));
        values[getValues().length - 1] = d;
    }

    int size() {
        return getValues().length;
    }

    public Expressao clone() {
        return new Expressao(tipo, b, getValues());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Double value : getValues()) {
            sb.append(value).append(" \t");
        }

        sb.append(" ").append(getTipo()).append(" ").append(getB());
        return sb.toString();
    }

    public String getTipo() {
        return tipo;
    }

    public double getB() {
        return b;
    }

    /**
     * @return the values
     */
    public double[] getValues() {
        return values;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @param values the values to set
     */
    public void setValues(double[] values) {
        this.values = values;
    }

    /**
     * @param b the b to set
     */
    public void setB(double b) {
        this.b = b;
    }
}
