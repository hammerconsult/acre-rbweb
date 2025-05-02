package br.com.webpublico.util;

import br.com.webpublico.entidades.ConfiguracaoDAM;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * @author daniel
 */
public class CarneUtil {

    public static String montaLinhaDigitavel(Date vencimento, BigDecimal valor, String numeroDAM, ConfiguracaoDAM config) {
        String codigoDeBarras = montaCodigoDeBarras(vencimento, valor, numeroDAM, config);
        return montaLinhaDigitavel(codigoDeBarras);
    }


    public static String montaCodigoDeBarras(Date vencimento, BigDecimal valor, String numeroDAM, ConfiguracaoDAM config) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(vencimento);
        String ano = "" + cal.get(Calendar.YEAR);
        ano = StringUtils.leftPad(ano, 4, "0");
        String mes = (cal.get(Calendar.MONTH) >= 9 ? "" : "0") + (cal.get(Calendar.MONTH) + 1);
        String dia = (cal.get(Calendar.DAY_OF_MONTH) >= 10 ? "" : "0") + cal.get(Calendar.DAY_OF_MONTH);
        String vencimentoString = ano + mes + dia;
        StringBuilder retornoSB = new StringBuilder("");
        retornoSB.append("8"); //constante para identificar arrecadação
        retornoSB.append(config.getIdentificacaoSegmento()); //1 - prefeituras
        retornoSB.append("6"); //6 - identifica que não permite atrazo
        retornoSB.append(preencheValor(valor, 11, 2));
        retornoSB.append(Util.zerosAEsquerda(config.getCodigoFebraban(),4)); //Identificação da Empresa órgão - Rio Branco = 3646
        //o tamanho total do campo é de 25 caracteres: uso 17 pois o vencimento tem 8 digitos: AAAAMMDD
        retornoSB.append(vencimentoString);
        retornoSB.append(StringUtil.preencheString(StringUtil.retornaApenasNumeros(numeroDAM), 14, '0'));
        retornoSB.append("000");//Não sei pq, no agata é asssim e tive de manter p funcionar os dados migrados
        retornoSB.insert(3, modulo10(StringUtil.retornaApenasNumeros(retornoSB.toString())));
        retiraNumeroDamCodigoBarras(retornoSB.toString());
        return retornoSB.toString();
    }

    private static String preencheValor(BigDecimal valor, int tamanhoGeral, int numeroCasasDecimais) {
        BigDecimal valorTemporario = valor.setScale(numeroCasasDecimais, RoundingMode.DOWN); //trunca as casas decimais excessivas
        return StringUtil.preencheString(StringUtil.retornaApenasNumeros(valorTemporario.toString()), tamanhoGeral, '0');
    }

    private static String calculaDigitoVerificadorModulo11(String texto) {
        int multiplicador = 2;
        int soma = 0;
        for (int i = texto.length() - 1; i >= 0; i--) {
            int numero = Integer.parseInt(((Character) texto.charAt(i)).toString());
            soma += numero * multiplicador++;
            if (multiplicador == 9) {
                multiplicador = 2;
            }
        }
        int resto = (soma % 11);
        switch (resto) {
            case 0: {
                return "0";
            }
            case 1: {
                return "0"; //quando for 1 retorna zero também
            }
            case 10: {
                return "1";
            }
            default: {
                return Integer.toString(resto);
            }
        }
    }

    public static int modulo10(String num) {
        //variáveis de instancia
        int soma = 0;
        int resto = 0;
        int dv = 0;
        String[] numeros = new String[num.length() + 1];
        int multiplicador = 2;
        String aux;
        String aux2;
        String aux3;

        for (int i = num.length(); i > 0; i--) {
            //Multiplica da direita pra esquerda, alternando os algarismos 2 e 1
            if (multiplicador % 2 == 0) {
                // pega cada numero isoladamente
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 2);
                multiplicador = 1;
            } else {
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 1);
                multiplicador = 2;
            }
        }

        // Realiza a soma dos campos de acordo com a regra
        for (int i = (numeros.length - 1); i > 0; i--) {
            aux = String.valueOf(Integer.valueOf(numeros[i]));

            if (aux.length() > 1) {
                aux2 = aux.substring(0, aux.length() - 1);
                aux3 = aux.substring(aux.length() - 1, aux.length());
                numeros[i] = String.valueOf(Integer.valueOf(aux2) + Integer.valueOf(aux3));
            } else {
                numeros[i] = aux;
            }
        }
        //Realiza a soma de todos os elementos do array e calcula o digito verificador
        //na base 10 de acordo com a regra.
        for (int i = numeros.length; i > 0; i--) {
            if (numeros[i - 1] != null) {
                soma += Integer.valueOf(numeros[i - 1]);
            }
        }
        resto = soma % 10;
        dv = 10 - resto;
        if (dv == 10) {
            dv = 0;
        }
        //retorna o digito verificador
        return dv;
    }


    public static void main(String[] args) {
        System.out.println(modulo10("81620000013"));
    }

    public static String montaLinhaDigitavel(String codigoDeBarras) {
        final StringBuilder sb = new StringBuilder("");
        String campo = codigoDeBarras.substring(00, 11);
        sb.append(campo).append("-").append(modulo10(campo)).append(" ");
        campo = codigoDeBarras.substring(11, 22);
        sb.append(campo).append("-").append(modulo10(campo)).append(" ");
        campo = codigoDeBarras.substring(22, 33);
        sb.append(campo).append("-").append(modulo10(campo)).append(" ");
        campo = codigoDeBarras.substring(33, 44);
        sb.append(campo).append("-").append(modulo10(campo)).append(" ");
        return sb.toString().trim();
    }

    public static String montarCodigoBarrasDaLinhaDigitavel(String linhaDigitavel) {
        final StringBuilder sb = new StringBuilder("");
        sb.append(linhaDigitavel.substring(00, 11));
        sb.append(linhaDigitavel.substring(14, 25));
        sb.append(linhaDigitavel.substring(28, 39));
        sb.append(linhaDigitavel.substring(42, 53));
        return sb.toString().trim();
    }

    public static String retiraNumeroDamCodigoBarras(String codigoBarras) {
        String numero = codigoBarras.substring(27, 41);
        numero = StringUtil.removeZerosEsquerda(numero);
        if (!numero.isEmpty()) {
            numero = numero.substring(0, numero.length() - 4) + "/" + numero.substring(numero.length() - 4, numero.length());
        }
        return numero;
    }
}
