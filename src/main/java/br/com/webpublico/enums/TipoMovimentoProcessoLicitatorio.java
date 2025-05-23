/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum TipoMovimentoProcessoLicitatorio {

    IRP("Intenção Registro de Preço", "/intencao-de-registro-de-preco/ver/"),
    PARTICIPANTE_IRP("Participante IRP", "/participante-intencao-registro-de-preco/ver/"),
    FORMULARIO_COTACAO("Formulário de Cotação", "/formulario-cotacao/ver/"),
    COTACAO("Cotação", "/licitacao/cotacao/ver/"),
    VALOR_COTACAO("Valor Cotação", "/licitacao/cotacao/ver/"),
    SOLICITACAO_COMPRA("Solicitação de Compra", "/solicitacao-de-compra/ver/"),
    AVALIACAO_SOLICITACAO_COMPRA("Avaliação da Solicitação de Compra", "/avaliacao-solicitacao-compra/ver/"),
    RESERVA_SOLICITACAO_COMPRA("Reserva da Solicitação de Compra", "/dotacao-de-solicitacao-de-compra/ver/"),
    PROCESSO_COMPRA("Processo de Compra", "/processo-compra/ver/"),
    LICITACAO("Licitação", "/licitacao/ver/"),
    PARTICIPANTE_LICITACAO("Paticipante Licitação", "/licitacao/participantes/ver/"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação", "/dispensa-licitacao/ver/"),
    CREDENCIAMENTO("Credenciamento", "/credenciamento/ver/"),
    CREDENCIADO("Credenciado", "/licitacao/proposta-fornecedor/ver/"),
    PARECER_LICITACAO("Parecer da Licitação", "/parecer-licitacao/ver/"),
    PREGAO_POR_ITEM("Pregão por Item", "/pregao/por-item/ver/"),
    PREGAO_POR_LOTE("Pregão por Lote", "/pregao/por-lote/ver/"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço", "/ata-registro-preco/ver/"),
    CONTRATO("Contrato", "/contrato-adm/ver/"),
    CONTRATO_VIGENTE("Contrato", "/contrato-adm/ver/"),
    SOLICITACAO_ADESAO_INTERNA("Solicitação a Ata Registro de Preço Interna", "/solicitacao-adesao-ata-registro-preco-interna/ver/"),
    ADESAO_INTERNA("Adesão a Ata Interna", "/solicitacao-adesao-ata-registro-preco-interna/ver/"),
    SOLICITACAO_ADESAO_EXTERNA("Solicitação de Adesão a Ata Externa", "/solicitacao-adesao-ata-registro-preco-externa/ver/"),
    ADESAO_EXTERNA("Adesão a Ata Externa", "/adesao-a-ata-de-registro-de-preco-externo/ver/"),
    RECURSO("Recurso", "/recurso-licitacao/ver/"),
    MAPA_COMPARATVO("Mapa Comparativo", "/mapa-comparativo/ver/"),
    MAPA_COMPARATVO_TECNICA_PRECO("Mapa Comparativo Técnica e Preço", "/mapa-comparativo-tecnica-preco/ver/"),
    ADJUDICACAO("Adjudicação", "/adjudicacao-homologacao-licitacao/ver/"),
    HOMOLOGACAO("Homologação", "/adjudicacao-homologacao-licitacao/ver/"),
    REPACTUACAO_PRECO("Repactuação de Preço", "/repactuacao-preco/ver/"),
    PROXIMO_VENCEDOR_LICITACAO("Próximo Vencedor", "/proximo-vencedor-licitacao/ver/"),
    ADITIVO_CONTRATO("Aditivo do Contrato", "/aditivo-contrato/ver/"),
    APOSTILAMENTO_CONTRATO("Apostilameneto do Contrato", "/apostilamento-contrato/ver/"),
    PROPOSTA_FORNECEDOR("Proposta do Fornecedor", "/licitacao/proposta-fornecedor/ver/"),
    PROPOSTA_FORNECEDOR_DISPENSA("Proposta do Fornecedor - Dispensa", "/dispensa-licitacao/ver/"),
    FISCAL("Responsável Técnico Fiscal", "/fiscal/ver/"),
    EXECUCAO_CONTRATO("Execução Contrato", "/execucao-contrato-adm/ver/"),
    REQUISICAO_COMPRA("Requisição de Compra", "/requisicao-compra/ver/");
    String descricao;
    String url;

    TipoMovimentoProcessoLicitatorio(String descricao, String url) {
        this.descricao = descricao;
        this.url = url;
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

    public boolean isCredenciamento() {
        return CREDENCIAMENTO.equals(this);
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isAvaliacaoSoliciacaoCampora() {
        return AVALIACAO_SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isReservaSoliciacaoCampora() {
        return RESERVA_SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isParecerLicitacao() {
        return PARECER_LICITACAO.equals(this);
    }

    public boolean isProcessoCompra() {
        return PROCESSO_COMPRA.equals(this);
    }

    public boolean isAdesaoInterna() {
        return ADESAO_INTERNA.equals(this);
    }

    public boolean isSolAdesaoExterna() {
        return SOLICITACAO_ADESAO_EXTERNA.equals(this);
    }

    public boolean isAdesaoExterna() {
        return ADESAO_EXTERNA.equals(this);
    }

    public boolean isAtaRegistroPreco() {
        return ATA_REGISTRO_PRECO.equals(this);
    }

    public boolean isPregaoPorItem() {
        return PREGAO_POR_ITEM.equals(this);
    }

    public boolean isPregaoPorLote() {
        return PREGAO_POR_LOTE.equals(this);
    }

    public boolean isRecurso() {
        return RECURSO.equals(this);
    }

    public boolean isContratoVigente() {
        return CONTRATO_VIGENTE.equals(this);
    }

    public boolean isMapaComparativo() {
        return MAPA_COMPARATVO.equals(this);
    }

    public boolean isMapaComparativoTecnicaPreco() {
        return MAPA_COMPARATVO_TECNICA_PRECO.equals(this);
    }

    public boolean isRepactuacaoPreco() {
        return REPACTUACAO_PRECO.equals(this);
    }

    public boolean isProximoVencedor() {
        return PROXIMO_VENCEDOR_LICITACAO.equals(this);
    }


    public boolean isAtaRegisrtoPrecoExterna() {
        return isSolAdesaoExterna() || isAdesaoExterna();
    }

    public boolean isPregao() {
        return isPregaoPorItem() || isPregaoPorLote();
    }

    public boolean isParticipanteAndIrp() {
        return isIrp() || isParticipanteIrp();
    }

    public boolean isParticipanteIrp() {
        return PARTICIPANTE_IRP.equals(this);
    }

    public boolean isRegistroPrecoExterno() {
        return ADESAO_EXTERNA.equals(this);
    }

    public boolean isExecucaoContrato() {
        return EXECUCAO_CONTRATO.equals(this);
    }

    public boolean isRequisicaoCompra() {
        return REQUISICAO_COMPRA.equals(this);
    }

    public static List<TipoMovimentoProcessoLicitatorio> getTiposProcessoPorTipoAjuste(TipoAjusteProcessoCompra tipoAjuste) {
        List<TipoMovimentoProcessoLicitatorio> tipos = Lists.newArrayList();
        switch (tipoAjuste) {
            case SUBSTITUIR_FORNECEDOR:
                tipos.add(LICITACAO);
                tipos.add(DISPENSA_LICITACAO_INEXIGIBILIDADE);
                break;
            case SUBSTITUIR_CONTROLE_ITEM:
                tipos.add(LICITACAO);
                tipos.add(DISPENSA_LICITACAO_INEXIGIBILIDADE);
                tipos.add(CREDENCIAMENTO);
                break;
            case SUBSTITUIR_OBJETO_COMPRA:
                tipos.add(ADESAO_EXTERNA);
                tipos.add(LICITACAO);
                tipos.add(CREDENCIAMENTO);
                tipos.add(DISPENSA_LICITACAO_INEXIGIBILIDADE);
                tipos.add(SOLICITACAO_COMPRA);
                tipos.add(COTACAO);
                tipos.add(FORMULARIO_COTACAO);
                tipos.add(IRP);
                break;
            default:
                tipos.add(LICITACAO);
                break;
        }
        return tipos;
    }

    public static List<TipoMovimentoProcessoLicitatorio> getTiposMovimentosAnexoLicitacao() {
        List<TipoMovimentoProcessoLicitatorio> toReturn = Lists.newArrayList();
        toReturn.add(TipoMovimentoProcessoLicitatorio.ATA_REGISTRO_PRECO);
        toReturn.add(TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE);
        toReturn.add(TipoMovimentoProcessoLicitatorio.CREDENCIAMENTO);
        toReturn.add(TipoMovimentoProcessoLicitatorio.SOLICITACAO_COMPRA);
        toReturn.add(TipoMovimentoProcessoLicitatorio.PARECER_LICITACAO);
        toReturn.add(TipoMovimentoProcessoLicitatorio.SOLICITACAO_ADESAO_EXTERNA);
        toReturn.add(TipoMovimentoProcessoLicitatorio.CONTRATO);
        toReturn.add(TipoMovimentoProcessoLicitatorio.LICITACAO);
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
