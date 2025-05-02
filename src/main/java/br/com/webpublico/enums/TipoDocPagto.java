/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author venon
 */
public enum TipoDocPagto implements EnumComDescricao {

    CHEQUE("Cheque", "0"),
    CAIXA("Caixa", "0"),
    ORDEMPAGAMENTO("Ordem de Pagamento", "0"),
    INATIVO("Inativo", "0"),
    DOC("Doc", "0"),
    TED("Ted", "0"),
    DEBITOEMCONTA("Débito em Conta", "0"),
    FATURA("Fatura/Boleto/Cobrança", "1"),
    CONVENIO("Convênio/Luz/Água/Telefone", "2"),
    GPS("GPS", "1"),
    DARF("DARF", "2"),
    DARF_SIMPLES("DARF SIMPLES", "3"),
    DEPJU("Depósito Judicial", "0"),
    GRU("GRU", "3");
    private String descricao;
    private String codigo;

    TipoDocPagto(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isDepju() {
        return DEPJU.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
