/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum OrigemSubstituicaoObjetoCompra {

    SOLICITACAO_REGISTRO_PRECO_EXTERNO("Solicitação Registro de Material Externo", "/solicitacao-adesao-ata-registro-preco-externa/ver/"),
    REGISTRO_PRECO_EXTERNO("Registro Preço Externo", "/adesao-a-ata-de-registro-de-preco-externo/ver/"),
    IRP("Intenção Registro de Preço", "/intencao-de-registro-de-preco/ver/"),
    FORMULARIO_COTACAO("Formulário de Cotação", "/formulario-cotacao/ver/"),
    COTACAO("Cotação", "/licitacao/cotacao/ver/"),
    SOLICITACAO_COMPRA("Solicitação de Compra", "/solicitacao-de-compra/ver/"),
    LICITACAO("Licitação", "/licitacao/ver/"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação", "/dispensa-licitacao/ver/"),
    CONTRATO("Contrato", "/contrato-adm/ver/"),
    EXECUCAO_CONTRATO("Execução Contrato", "/execucao-contrato-adm/ver/"),
    REQUISICAO_COMPRA("Requisição de Compra", "/requisicao-compra/ver/"),
    ADESAO_ATA_REGISTRO_PRECO_INTERNA("Adesão a Ata de Registro de Preço Interna", "/solicitacao-adesao-ata-registro-preco-interna/ver/");
    String descricao;
    String url;

    OrigemSubstituicaoObjetoCompra(String descricao, String url) {
        this.descricao = descricao;
        this.url = url;
    }

    public static List<OrigemSubstituicaoObjetoCompra> getTiposSubstituicao() {
        List<OrigemSubstituicaoObjetoCompra> tipos = Lists.newArrayList();
        tipos.add(OrigemSubstituicaoObjetoCompra.CONTRATO);
        tipos.add(OrigemSubstituicaoObjetoCompra.LICITACAO);
        tipos.add(OrigemSubstituicaoObjetoCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE);
        tipos.add(OrigemSubstituicaoObjetoCompra.REGISTRO_PRECO_EXTERNO);
        tipos.add(OrigemSubstituicaoObjetoCompra.SOLICITACAO_COMPRA);
        tipos.add(OrigemSubstituicaoObjetoCompra.COTACAO);
        tipos.add(OrigemSubstituicaoObjetoCompra.FORMULARIO_COTACAO);
        tipos.add(OrigemSubstituicaoObjetoCompra.IRP);
        return tipos;
    }

    public String getUrl() {
        return url;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isIrp() {
        return IRP.equals(this);
    }

    public boolean isFormularioCotacao() {
        return FORMULARIO_COTACAO.equals(this);
    }

    public boolean isCotacao() {
        return COTACAO.equals(this);
    }

    public boolean isSolicitacaoCompra() {
        return SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isLicitacao() {
        return LICITACAO.equals(this);
    }

    public boolean isDispensa() {
        return DISPENSA_LICITACAO_INEXIGIBILIDADE.equals(this);
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isSolicitacaoRegistroPrecoExterno() {
        return SOLICITACAO_REGISTRO_PRECO_EXTERNO.equals(this);
    }
    public boolean isAdesaoAtaRegistroPrecoInterna() {
        return ADESAO_ATA_REGISTRO_PRECO_INTERNA.equals(this);
    }

    public boolean isRegistroPrecoExterno() {
        return REGISTRO_PRECO_EXTERNO.equals(this);
    }

    public boolean isLicitacaoOrDispensa() {
        return isLicitacao() || isDispensa();
    }

    public boolean isRequisicaoCompra() {
        return REQUISICAO_COMPRA.equals(this);
    }

    public boolean isExecucaoContrato() {
        return EXECUCAO_CONTRATO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
