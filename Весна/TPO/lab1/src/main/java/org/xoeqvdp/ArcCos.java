package org.xoeqvdp;

import static java.lang.Math.PI;


public class ArcCos {

    public static void main(String[] args) {
        ArcCos custom = new ArcCos();

        System.out.println(Math.acos(0.5));
        System.out.println(custom.arccos(0.5));
    }

    private static final int MAX_ITERATIONS = 1000;

    private static final double EPSILON = 1e-10;

    public Double arccos(Double val){
        if (val.isNaN() || val > 1 || val < -1) {
            return null;
        }

        if (val ==1) {
            return 0.0;
        }

        if (val == -1) {
            return PI;
        }

        double result = PI / 2; // Начальное значение
        double term = val; // Первый член ряда
        int n = 0;

        while (Math.abs(term) > EPSILON && n < MAX_ITERATIONS) {
            result -= term;
            n++;
            term = (factorial(2 * n) * Math.pow(val, 2 * n + 1)) /
                    (Math.pow(4, n) * Math.pow(factorial(n), 2) * (2 * n + 1));
        }

        return result;
    }

    private static long factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}