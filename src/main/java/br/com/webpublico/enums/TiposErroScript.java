/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gecen
 */
public enum TiposErroScript {


    ERRO_GENERICO_EXECUCAO_SCRIPT("Erro genérico executar o JavaScript"),
    ERRO_FUNCAO_NAO_ENCONTRADA("Erro a executar o JavaScript, função não encontrada"),
    ERRO_TIPO_RETORNO_INCOMPATIVEL("Erro a executar o JavaScript, o tipo da função de retorno não é um double"),
    ERRO_DESCONHECIDO("Erro desconhecido aa executar o JavaScript");


    private String message;

    public String getMessage() {
        return message;
    }

    private TiposErroScript(String message) {
        this.message = message;
    }


}
