/*
 * Codigo gerado automaticamente em Tue Apr 17 09:33:08 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioAvaliacaoSolicitacaoCompraControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AvaliacaoSolicitacaoDeCompraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "avaliacaoSolicitacaoDeCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-avaliacao-solicitacao-compra", pattern = "/avaliacao-solicitacao-compra/novo/",
        viewId = "/faces/administrativo/licitacao/avaliacaosolicitacao/edita.xhtml"),
    @URLMapping(id = "editar-avaliacao-solicitacao-compra", pattern = "/avaliacao-solicitacao-compra/editar/#{avaliacaoSolicitacaoDeCompraControlador.id}/",
        viewId = "/faces/administrativo/licitacao/avaliacaosolicitacao/edita.xhtml"),
    @URLMapping(id = "ver-avaliacao-solicitacao-compra", pattern = "/avaliacao-solicitacao-compra/ver/#{avaliacaoSolicitacaoDeCompraControlador.id}/",
        viewId = "/faces/administrativo/licitacao/avaliacaosolicitacao/visualizar.xhtml"),
    @URLMapping(id = "list-aravaliacao-solicitacao-compra", pattern = "/avaliacao-solicitacao-compra/listar/",
        viewId = "/faces/administrativo/licitacao/avaliacaosolicitacao/lista.xhtml")
})
public class AvaliacaoSolicitacaoDeCompraControlador extends PrettyControlador<AvaliacaoSolicitacaoDeCompra> implements Serializable, CRUD {

    @EJB
    private AvaliacaoSolicitacaoDeCompraFacade facade;
    private List<FonteDespesaORC> fontesDespesaOrc;
    private List<LoteSolicitacaoMaterial> lotesSolicitacao;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public AvaliacaoSolicitacaoDeCompraControlador() {
        super(AvaliacaoSolicitacaoDeCompra.class);
    }

    @URLAction(mappingId = "novo-avaliacao-solicitacao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataAvaliacao(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        fontesDespesaOrc = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-avaliacao-solicitacao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarSolicitacao();
    }

    @URLAction(mappingId = "editar-avaliacao-solicitacao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        try {
            validarSolicitacaoUtilizadaProcessoCompra();
            recuperarSolicitacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao-solicitacao-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AvaliacaoSolicitacaoDeCompraFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<SelectItem> getStatus() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoStatusSolicitacao.APROVADA, TipoStatusSolicitacao.APROVADA.getDescricao()));
        toReturn.add(new SelectItem(TipoStatusSolicitacao.REJEITADA, TipoStatusSolicitacao.REJEITADA.getDescricao()));
        return toReturn;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            boolean situacaoAlterada = facade.isSituacaoDaAvaliacaoAlterada(selecionado);
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            if (situacaoAlterada) {
                facade.gerarNotificacaoAvaliacaoSolicitacaoMaterialAvaliada(selecionado);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception exc) {
            FacesUtil.addErrorGenerico(exc);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.isSolicitacaoAprovada() && (selecionado.getMotivo().length() == 0 || selecionado.getMotivo() == null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quando uma solicitação for rejeitada, deve haver um motivo!");
        }
        if (selecionado.getSolicitacao() != null && TipoNaturezaDoProcedimentoLicitacao.ELETRONICO.equals(selecionado.getSolicitacao().getTipoNaturezaDoProcedimento())) {
            if (fontesDespesaOrc == null || fontesDespesaOrc.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Quando a solicitação for do tipo pregão eletrônico, é obrigatório fazer a reserva de dotação.");
            }
        }
        validarSolicitacaoUtilizadaProcessoCompra();
        validarDataAvaliacao(ve);
        ve.lancarException();
    }

    private void validarDataAvaliacao(ValidacaoException ve) {
        DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial = facade.getSolicitacaoMaterialFacade().buscarDotacaoSolicitacaoMaterial(selecionado.getSolicitacao());
        if (dotacaoSolicitacaoMaterial != null &&
            Util.getDataHoraMinutoSegundoZerado(selecionado.getDataAvaliacao()).before(Util.getDataHoraMinutoSegundoZerado(dotacaoSolicitacaoMaterial.getRealizadaEm()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data da Avaliação deve ser maior ou igual a Data da Reserva de Dotação " +
                Util.dateToString(dotacaoSolicitacaoMaterial.getRealizadaEm()) + ".");
        }
        AvaliacaoSolicitacaoDeCompra ultimaAvaliacao = facade.getSolicitacaoMaterialFacade().getUltimaAvaliacaoDaSolicitacao(selecionado.getSolicitacao());
        if (ultimaAvaliacao != null &&
            (Util.getDataHoraMinutoSegundoZerado(ultimaAvaliacao.getDataAvaliacao()).after(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataAvaliacao())))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A 'Data da Avaliação' deve ser maior que a 'Data do Status Atual' da solicitação. Data do Status Atual: " +
                Util.dateHourToString(ultimaAvaliacao.getDataAvaliacao()) + " Status Atual '" + ultimaAvaliacao.getTipoStatusSolicitacao().getDescricao() + "'.");
        }
    }

    @Override
    public void excluir() {
        try {
            validarSolicitacaoUtilizadaProcessoCompra();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSolicitacaoUtilizadaProcessoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getSolicitacaoMaterialFacade().temVinculoComProcessoDeCompra(selecionado.getSolicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação selecionada possui vínculo com processo de compra.");
        }
        ve.lancarException();
    }

    public Boolean habilitarBotaoExcluir() {
        return selecionado.getMotivo() == null
            || (!selecionado.getMotivo().equals(SolicitacaoMaterial.getMsgRegistroNovo())
            && (!selecionado.getMotivo().equals(SolicitacaoMaterial.getMsgRegistroAlterado())));
    }

    private void recuperarSolicitacao() {
        if (selecionado.getSolicitacao() != null) {
            lotesSolicitacao = facade.getSolicitacaoMaterialFacade().buscarLotesWithItens(selecionado.getSolicitacao());
        }
    }

    public List<SolicitacaoMaterial> completarSolicitacaoMaterial(String parte) {
        return facade.getSolicitacaoMaterialFacade().buscarSolicitacaoMaterialNaoAvaliada(parte.trim().toLowerCase());
    }

    public void listenerSolicitacao() {
        recuperarSolicitacao();
        buscarFontesDespesaORCDaDotacaoDaSolicitacaoMaterial();
    }

    public void definirSituacao() {
        selecionado.setMotivo(null);
    }

    public boolean mostraMotivo() {
        return selecionado.getSolicitacao() != null && selecionado.getTipoStatusSolicitacao() != null && selecionado.isRejeitada();
    }

    private List<FonteDespesaORC> buscarFontesDespesaORCDaDotacaoDaSolicitacaoMaterial() {
        DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial = facade.getSolicitacaoMaterialFacade().buscarDotacaoSolicitacaoMaterial(selecionado.getSolicitacao());
        if (dotacaoSolicitacaoMaterial != null) {
            for (DotacaoSolicitacaoMaterialItem item : dotacaoSolicitacaoMaterial.getItens()) {
                for (DotacaoSolicitacaoMaterialItemFonte detalhe : item.getFontes()) {
                    if (fontesDespesaOrc.contains(detalhe.getFonteDespesaORC())) {
                        continue;
                    }
                    fontesDespesaOrc.add(detalhe.getFonteDespesaORC());
                }
            }
        }
        return fontesDespesaOrc;
    }

    public String getStatusAtualDaSolicitacao() {
        try {
            return facade.getSolicitacaoMaterialFacade().getStatusAtualDaSolicitacao(selecionado.getSolicitacao()).getDescricao();
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Status não localizado.");
        }
        return "";
    }

    public List<FonteDespesaORC> getFontesDespesaOrc() {
        return fontesDespesaOrc;
    }

    public void setFontesDespesaOrc(List<FonteDespesaORC> fontesDespesaOrc) {
        this.fontesDespesaOrc = fontesDespesaOrc;
    }

    public List<LoteSolicitacaoMaterial> getLotesSolicitacao() {
        return lotesSolicitacao;
    }

    public void setLotesSolicitacao(List<LoteSolicitacaoMaterial> lotesSolicitacao) {
        this.lotesSolicitacao = lotesSolicitacao;
    }

    public void gerarRelatorio(AvaliacaoSolicitacaoDeCompra avaliacao) {
        if (avaliacao != null) {
            selecionado = avaliacao;
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            FiltroRelatorioAdministrativo filtroRelatorio = new FiltroRelatorioAdministrativo();
            filtroRelatorio.setIdObjeto(selecionado.getId());
            HierarquiaOrganizacional secretaria = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getSolicitacao().getUnidadeOrganizacional(), selecionado.getDataAvaliacao());

            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de Avaliação da Solicitação de Compra");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), true);
            dto.adicionarParametro("WHERE", RelatorioAvaliacaoSolicitacaoCompraControlador.montarCondicao(filtroRelatorio));
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltros());
            dto.adicionarParametro("SECRETARIA", secretaria.toString());
            dto.setNomeRelatorio("Relatório de Avaliação da Solicitação de Compra");
            dto.setApi("administrativo/avaliacao-solicitacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.AVALIACAO_SOLICITACAO_COMPRA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

}
