/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Header {

    ZEROS("Zeros"),
    DATA_GERACAO("Data de quando for gerado o Arquivo ddmmaaaa"),
    HORA_GERACAO("Hora de quando foi gerado o arquivo"),
    NUMERO_REMESSA_CONSECUTIVO("Numero da remessa consecutivo"),
    UMZEROEZEROZEROUM("10E001"),
    NUMERO_CONTRATO_BANCO_CLIENTE("Número do contrato / banco X cliente"),
    BRANCOS("Brancos"),
    NUMERO_SEQUENCIAL_ARQUIVO("Numero seqüencial no arquivo, iniciando em 0000001");

    private TipoRegistroOBN600Header(String descricao) {
        this.descricao = descricao;
    }
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
