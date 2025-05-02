/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import java.math.BigDecimal;
import java.util.Date;

public class QueryUtil {


    public static Date toDate(Object[] objeto, int posicao) {
        return objeto[posicao] != null ? (Date) objeto[posicao] : null;
    }

    public static Boolean toBoolean(Object[] objeto, int posicao) {
        return objeto[posicao] != null && toLong(objeto, posicao) == 1 ? true : false;
    }

    public static Integer toInteger(Object[] objeto, int posicao) {
        return objeto[posicao] != null ? ((Number) objeto[posicao]).intValue() : null;
    }


    public static Long toLong(Object[] objeto, int posicao) {
        return objeto[posicao] != null ? ((Number) objeto[posicao]).longValue() : null;
    }

    public static String toString(Object[] objeto, int posicao) {
        return objeto[posicao] != null ? (String) objeto[posicao] : null;
    }

    public static BigDecimal toBigDecimal(Object[] objeto, int posicao) {
        return objeto[posicao] != null ? new BigDecimal(((Number) objeto[posicao]).doubleValue()) : null;
    }

}
