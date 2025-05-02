package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioLancePregao;
import br.com.webpublico.controlerelatorio.RelatorioSolicitacaoCotaFinanceiraControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.HistoricoProcessoLicitatorio;
import br.com.webpublico.enums.SituacaoContrato;
import br.com.webpublico.enums.SituacaoDispensaDeLicitacao;
import br.com.webpublico.enums.TipoStatusSolicitacao;
import br.com.webpublico.enums.administrativo.SituacaoCotacao;
import br.com.webpublico.negocios.HistoricoProcessoLicitatorioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class HistoricoProcessoLicitatorioControlador implements Serializable {

    @EJB
    private HistoricoProcessoLicitatorioFacade facade;
    private List<HistoricoProcessoLicitatorio> historicos;
    private FiltroHistoricoProcessoLicitatorio filtro;

    public void novo(FiltroHistoricoProcessoLicitatorio filtro) {
        if (filtro != null) {
            this.filtro = filtro;
            if (historicos == null || historicos.isEmpty()) {
                preencherHistorico();
            }
        }
    }

    public void gerarRelatorio(HistoricoProcessoLicitatorio historico) {
        switch (historico.getTipoMovimento()) {
            case IRP:
                IntencaoRegistroPrecoControlador irpControlador = (IntencaoRegistroPrecoControlador) Util.getControladorPeloNome("intencaoRegistroPrecoControlador");
                IntencaoRegistroPreco irp = (IntencaoRegistroPreco) facade.recuperarObjetoParaRelatorio(historico);
                irpControlador.gerarRelatorio(irp);
                break;
            case FORMULARIO_COTACAO:
                FormularioCotacaoControlador formularioControlador = (FormularioCotacaoControlador) Util.getControladorPeloNome("formularioCotacaoControlador");
                FormularioCotacao formulario = (FormularioCotacao) facade.recuperarObjetoParaRelatorio(historico);
                formularioControlador.gerarRelatorio(formulario);
                break;
            case COTACAO:
                CotacaoControlador cotacaoControlador = (CotacaoControlador) Util.getControladorPeloNome("cotacaoControlador");
                Cotacao cotacao = (Cotacao) facade.recuperarObjetoParaRelatorio(historico);
                cotacaoControlador.gerarRelatorio(cotacao);
                break;
            case SOLICITACAO_COMPRA:
                SolicitacaoMaterialControlador solicitcacaoControlador = (SolicitacaoMaterialControlador) Util.getControladorPeloNome("solicitacaoMaterialControlador");
                SolicitacaoMaterial solicitacao = (SolicitacaoMaterial) facade.recuperarObjetoParaRelatorio(historico);
                solicitcacaoControlador.gerarRelatorio(solicitacao);
                break;
            case AVALIACAO_SOLICITACAO_COMPRA:
                AvaliacaoSolicitacaoDeCompraControlador avaliacaoControlador = (AvaliacaoSolicitacaoDeCompraControlador) Util.getControladorPeloNome("avaliacaoSolicitacaoDeCompraControlador");
                AvaliacaoSolicitacaoDeCompra avaliacao = (AvaliacaoSolicitacaoDeCompra) facade.recuperarObjetoParaRelatorio(historico);
                avaliacaoControlador.gerarRelatorio(avaliacao);
                break;
            case PREGAO_POR_ITEM:
                RelatorioLancePregao relatorioPregaoPorItemControlador = (RelatorioLancePregao) Util.getControladorPeloNome("relatorioLancePregao");
                Pregao pregaoPorItem = (Pregao) facade.recuperarObjetoParaRelatorio(historico);
                relatorioPregaoPorItemControlador.gerarRelatorio(pregaoPorItem);
                break;
            case PREGAO_POR_LOTE:
                PregaoPorLoteControlador relatorioPregaoPorLoteControlador = (PregaoPorLoteControlador) Util.getControladorPeloNome("pregaoPorLoteControlador");
                Pregao pregaoPorLote = (Pregao) facade.recuperarObjetoParaRelatorio(historico);
                relatorioPregaoPorLoteControlador.gerarRelatorio(pregaoPorLote);
                break;
        }
    }

    public void preencherHistorico() {
        historicos = facade.buscarHistoricoProcesso(filtro);
        if (!historicos.isEmpty()) {
            FacesUtil.executaJavaScript("atualizaTabelaHistorico()");
        }
        filtro = null;
    }

    public List<HistoricoProcessoLicitatorio> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<HistoricoProcessoLicitatorio> historicos) {
        this.historicos = historicos;
    }

    public FiltroHistoricoProcessoLicitatorio getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroHistoricoProcessoLicitatorio filtro) {
        this.filtro = filtro;
    }
}
