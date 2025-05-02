package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoProcesso implements EnumComDescricao {

    CONTRATO("Contrato", "/contrato-adm/ver/"),
    LICITACAO("Licitação", "/licitacao/ver/"),
    IRP("Intenção Registro de Preço", "/intencao-de-registro-de-preco/ver/"),
    PARTICIPANTE_IRP("Participante IRP", "/participante-intencao-registro-de-preco/ver/"),
    FORMULARIO_COTACAO("Formulário de Cotação", "/formulario-cotacao/ver/"),
    COTACAO("Cotação", "/licitacao/cotacao/ver/"),
    VALOR_COTACAO("Valor Cotação", "/licitacao/cotacao/ver/"),
    SOLICITACAO_COMPRA("Solicitação de Compra", "/solicitacao-de-compra/ver/"),
    PROCESSO_COMPRA("Processo de Compra", "/processo-compra/ver/"),
    PROPOSTA_FORNECEDOR("Proposta do Fornecedor", "/licitacao/proposta-fornecedor/ver/"),
    PREGAO_POR_ITEM("Pregão por Item", "/pregao/por-item/ver/"),
    PREGAO_POR_LOTE("Pregão por Lote", "/pregao/por-lote/ver/"),
    ADJUDICACAO("Adjudicação", "/adjudicacao-homologacao-licitacao/ver/"),
    HOMOLOGACAO("Homologação", "/adjudicacao-homologacao-licitacao/ver/");
    private String descricao;
    private String url;

    TipoProcesso(String descricao, String url) {
        this.descricao = descricao;
        this.url = url;
    }

    public static List<TipoProcesso> getTiposAlteracao() {
        List<TipoProcesso> tipos = Lists.newArrayList();
        tipos.add(TipoProcesso.CONTRATO);
        tipos.add(TipoProcesso.LICITACAO);
        return tipos;
    }

    public String getUrl() {
        return url;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isParticipanteAndIrp() {
        return isIrp() || isParticipanteIrp();
    }

    public boolean isIrp() {
        return IRP.equals(this);
    }

    public boolean isParticipanteIrp() {
        return PARTICIPANTE_IRP.equals(this);
    }

    public boolean isSolicitacaoCompra() {
        return SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isLicitacao() {
        return LICITACAO.equals(this);
    }
}
