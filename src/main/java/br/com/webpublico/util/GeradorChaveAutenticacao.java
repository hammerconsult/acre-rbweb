package br.com.webpublico.util;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 18/06/15
 * Time: 09:27
 * To change this template use File | Settings | File Templates.
 */
public class GeradorChaveAutenticacao implements Serializable {

    private static Integer TAMANHO_ASSINATURA = 32;


    public static String geraChaveDeAutenticacao(String chave, TipoAutenticacao tipoAutenticacao) {
        return StringUtil.cortaOuCompletaEsquerda(Seguranca.md5(chave+tipoAutenticacao), TAMANHO_ASSINATURA, "0").toUpperCase();
    }

    public static String formataChaveDeAutenticacao(String chave) {
        if(chave == null){
            return "";
        }
        String chaveFormatada = "";
        if (chave.length() == 32) {
            chaveFormatada += chave.substring(0, 4) + ".";
            chaveFormatada += chave.substring(4, 8) + ".";
            chaveFormatada += chave.substring(8, 12) + ".";
            chaveFormatada += chave.substring(12, 16) + ".";
            chaveFormatada += chave.substring(16, 20) + ".";
            chaveFormatada += chave.substring(20, 24) + ".";
            chaveFormatada += chave.substring(24, 28) + ".";
            chaveFormatada += chave.substring(28, 32) + ".";
        } else if (chave.length() == 44) {
            chaveFormatada += chave.substring(0, 4) + ".";
            chaveFormatada += chave.substring(4, 8) + ".";
            chaveFormatada += chave.substring(8, 12) + ".";
            chaveFormatada += chave.substring(12, 16) + ".";
            chaveFormatada += chave.substring(16, 20) + ".";
            chaveFormatada += chave.substring(20, 24) + ".";
            chaveFormatada += chave.substring(24, 28) + ".";
            chaveFormatada += chave.substring(28, 32) + ".";
            chaveFormatada += chave.substring(32, 36) + ".";
            chaveFormatada += chave.substring(36, 40) + ".";
            chaveFormatada += chave.substring(40, 44) + ".";
        } else {
            chaveFormatada = chave;
        }
        return chaveFormatada;
    }


    public enum TipoAutenticacao {
        ALVARA, DOCUMENTO_OFICIAL, NFSAVULSA, CONTRA_CHEQUE, CADASTRO_IMOBILIARIO,CADASTRO_ECONOMICO;
    }
}
