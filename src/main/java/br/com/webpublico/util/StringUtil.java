/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @author daniel
 */
public class StringUtil {

    public static String removeZerosEsquerda(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replaceAll("^0+", "");
    }

    public static String preencheString(String texto, int tamanho, char caractereAInserir) {
        //Utilizado no carnê do IPTU - NÃO ALTERAR A FUNCIONALIDADE
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < tamanho) {
            sb.insert(0, caractereAInserir);
        }
        return sb.toString();
    }

    public static String retornaApenasNumeros(String original) {
        if (original != null) {
            //Utilizado no carnê do IPTU e E-SOCIAL- NÃO ALTERAR A FUNCIONALIDADE
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < original.length(); i++) {
                if ((original.charAt(i) == '0') || (original.charAt(i) == '1') || (original.charAt(i) == '2')
                    || (original.charAt(i) == '3') || (original.charAt(i) == '4') || (original.charAt(i) == '5')
                    || (original.charAt(i) == '6') || (original.charAt(i) == '7') || (original.charAt(i) == '8')
                    || (original.charAt(i) == '9')) {
                    sb.append(original.charAt(i));
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static String retornaApenasNumerosComPontoVirgula(String original) {
        if (original != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < original.length(); i++) {
                List<Character> caracteresPermitidos = Lists.newArrayList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ',');
                if (caracteresPermitidos.contains(original.charAt(i))) {
                    sb.append(original.charAt(i));
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Corta a string no tamanho desejado ou se for menor que o tamanho
     * informado completa a direita
     *
     * @param str
     * @param size
     * @param parte
     * @return string formatada
     */
    public static String cortaOuCompletaDireita(String str, int size, String parte) {
        if (str == null) {
            return StringUtils.rightPad("", size, parte);
        }

        if (str.length() > size) {
            return str = str.substring(0, size);
        }
        return StringUtils.rightPad(str, size, parte);
    }

    /**
     * Corta a string no tamanho desejado ou se for menor que o tamanho
     * informado completa a esquerda
     *
     * @param aCompletar
     * @param size
     * @param comOqueCompletar
     * @return string formatada
     */
    @Deprecated
    public static String cortaOuCompletaEsquerda(String aCompletar, int size, String comOqueCompletar) {
        if (aCompletar == null) {
            aCompletar = "";
        }
        if (aCompletar.length() > size) {
            //TODO verificar lógica do corte a esquerda, está retornando valor errado.
            return aCompletar = aCompletar.substring(0, size);
        }
        return StringUtils.leftPad(aCompletar, size, comOqueCompletar);
    }

    /**
     * Corta a string no tamanho desejado ou se for menor que o tamanho
     * informado completa a esquerda
     *
     * @param aCompletar
     * @param size
     * @param comOqueCompletar
     * @return string formatada
     */
    public static String cortarOuCompletarEsquerda(String aCompletar, int size, String comOqueCompletar) {
        if (aCompletar == null) {
            aCompletar = "";
        }
        if (aCompletar.length() > size) {
            return aCompletar.substring(aCompletar.length() - size, aCompletar.length());
        }
        return StringUtils.leftPad(aCompletar, size, comOqueCompletar);
    }

    public static String removeCaracteresEspeciais(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*\\=", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\,\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ").replaceAll("§", " ").toUpperCase();
    }

    public static String converterAcentosERemoverCarateresEspecais(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*\\=", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\,\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ").replaceAll("§", " ").replaceAll("[^a-zA-Z0-9]", " ").toUpperCase();
    }

    public static String removerApenasAcentuacao(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').toLowerCase();
    }

    public static String removeCaracteresEspeciaisExcetoPonto(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*\\=", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\,\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ").replaceAll("§", " ").toUpperCase();
    }

    public static String removeAcentuacao(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ").replaceAll("§", " ").toUpperCase();
    }

    public static String removeCaracteresEspeciaisENumeros(String s) {
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\,\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ").replaceAll("§", " ").replaceAll("\\d", "").toUpperCase();
    }

    public static String removeCaracteresEspeciaisSemEspaco(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return s;
        }
        return s.replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*", "").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", "").replaceAll("[\\,\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", "").replaceAll("§", "").toUpperCase();
    }

    public static String substituirTravessaoPorHifen(String s) {
        return s.replace("–", "-");
    }

    public static String removeEspacos(String s) {
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s;
    }

    public static String primeiroCaracterMaiusculo(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String primeiroCaracterMinusculo(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public static String cortaEsquerda(int size, String parte) {
        if (parte.length() > size) {
            return parte = parte.substring(parte.length() - size, parte.length());
        }
        return parte;
    }

    public static String cortaDireita(int size, String parte) {
        if (parte.length() > size) {
            return parte = parte.substring(0, size);
        }
        return parte;
    }

    public static String centralizaTexto(String parte, int tamanho, String caractereAPreencher) {
        String retorno = "";
        int espacosEmBranco = (tamanho - parte.length()) / 2;
        if (espacosEmBranco >= 0) {
            retorno = cortaOuCompletaDireita("", espacosEmBranco, caractereAPreencher) + parte + cortaOuCompletaDireita("", espacosEmBranco, caractereAPreencher);
        } else {
            retorno = StringUtil.cortaOuCompletaDireita(parte, tamanho, caractereAPreencher);
        }
        return retorno;
    }

    public static int contadorLinhas(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }


    public static String removerQuebraDeLinha(String string) {
        string = string.replaceAll("\r", "");
        string = string.replaceAll("\n", "");
        string = string.replaceAll("\t", "");
        return string;
    }

    public static List<String> tokenizer(String conteudo, String delimitador) {
        List<String> partes = Lists.newLinkedList();
        if (conteudo == null || (conteudo != null && "".equals(conteudo))) {
            return partes;
        }
        StringTokenizer tokenizer = new StringTokenizer(conteudo, delimitador);
        while (tokenizer.hasMoreTokens()) {
            String parte = tokenizer.nextToken();
            partes.add(parte);
        }
        return partes;
    }

    public static String tratarCampoVazio(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }

    public static String convertVazioEmNull(String texto) {
        if (texto == null) {
            return texto;
        }
        if (texto != null && texto.isEmpty()) {
            return null;
        }
        return texto.trim();
    }

    public static String removerEspacoEmBranco(String s) {
        while (s.contains(" ")) {
            s = s.replace(" ", "");
        }
        return s;
    }

    public static boolean isNumerico(String texto) {
        if (texto == null) {
            return false;
        }
        return texto.matches("[0-9]*");
    }

    public static String somentePrimeiroCaracterMaiusculo(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String concatenar(String hash, String campo, String separador) {
        if (hash == null) {
            hash = "";
        }
        return hash.concat(campo).concat(separador);
    }
}
