/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 * @author reidocrime
 */
public class AlgorismoRomano {

    private static final int[] VALORES = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
    private static final String[] DIGITOS = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};


    public static String converteNumero(int numero) {
        String resposta = "";
        while (numero > 0) {
            for (int i = VALORES.length - 1; i >= 0; i--) {
                if (numero >= VALORES[i]) {
                    resposta += DIGITOS[i];
                    numero -= VALORES[i];
                    break;
                }
            }
        }
        return resposta;
    }

}
