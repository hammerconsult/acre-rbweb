/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.pncp.enums;

public enum TipoProcessoPncp {

    LICITACAO("Licitação"),
    CREDENCIAMENTO("Credenciamento"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa/Inexigibilidade"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço"),
    CONTRATO("Contrato"),
    EMPENHO("Empenho"),
    PLANO_CONTRATACAO_ANUAL("Plano Contratacao Anual - PCA");

    private String descricao;

    private TipoProcessoPncp(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isLicitacao(){
        return LICITACAO.equals(this);
    }

    public boolean isDispensa(){
        return DISPENSA_LICITACAO_INEXIGIBILIDADE.equals(this);
    }

    public boolean isCredenciamento(){
        return CREDENCIAMENTO.equals(this);
    }

    public boolean isContrato(){
        return CONTRATO.equals(this);
    }

    public boolean isEmpenho(){
        return EMPENHO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
