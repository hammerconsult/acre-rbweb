package br.com.webpublico.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel
 *         Date:   25/09/13 19:43
 *         Classe Utilitária específica para utilização em Entidades.
 *         Não incluir dependências da visão e controle, como Java EE e PrimeFaces
 */
public class UtilEntidades implements Serializable {

    public static String formatarCnpj(String cnpj) {
        try {
            if (cnpj != null && (!cnpj.contains(".") || !cnpj.contains("-") || !cnpj.contains("/"))) {
                Pattern pattern = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})");
                Matcher matcher = pattern.matcher(cnpj);
                if (matcher.matches()) {
                    cnpj = matcher.replaceAll("$1.$2.$3/$4-$5");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnpj;
    }

    public static String formatarCpf(String cpf) {
        try {
            if (cpf != null && (!cpf.contains(".") || !cpf.contains("-"))) {
                Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{2})");
                Matcher matcher = pattern.matcher(cpf);
                if (matcher.matches()) {
                    cpf = matcher.replaceAll("$1.$2.$3-$4");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cpf;
    }
}
