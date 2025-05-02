/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author reidocrime
 */
public class UtilArquivoBancario {

    public static String completaBrancoEsquera(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaEsquerda(valor, tamanho, " ");
    }

    public static String completaBrancoDireita(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaDireita(valor, tamanho, " ");
    }

    public static String completaZerosEsquera(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaEsquerda(valor, tamanho, "0");
    }

    public static String completaZerosDireita(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaDireita(valor, tamanho, "0");
    }

    public static String completaNovesEsquera(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaEsquerda(valor, tamanho, "9");
    }

    public static String completaNovesDireita(String valor, int tamanho) {
        return StringUtil.cortaOuCompletaDireita(valor, tamanho, "9");
    }

    public static Boolean soBrancos(String valor) {
        Pattern p = Pattern.compile(" *");
        Matcher m = p.matcher(valor);
        return m.matches();
    }

    public static Boolean soZeros(String valor) {
        Pattern p = Pattern.compile("0*");
        Matcher m = p.matcher(valor);
        return m.matches();
    }

    public static Boolean soNoves(String valor) {
        Pattern p = Pattern.compile("9*");
        Matcher m = p.matcher(valor);
        return m.matches();
    }

    public static Boolean soNumeros(String valor) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(valor);
        return m.matches();
    }

    public static Boolean soLetras(String valor) {
        Pattern p = Pattern.compile("[a-zA-Z]*");
        Matcher m = p.matcher(valor);
        return m.matches();
    }
}
