package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LOA;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoFonteDespesaOrc;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoSaldoFonteDespesaOrcFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 08/02/2015.
 */
@ManagedBean(name = "reprocessamentoSaldoFonteDespesaOrcControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-saldo-fonte-despesa-orc", pattern = "/reprocessamento-fonte-despesa-orcamentaria/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldofontedespesaorc/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-saldo-fonte-despesa-orc", pattern = "/reprocessamento-fonte-despesa-orcamentaria/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldofontedespesaorc/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-saldo-fonte-despesa-orc", pattern = "/reprocessamento-fonte-despesa-orcamentaria/ver/#{processamentoSaldoExtraControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldofontedespesaorc/visualizar.xhtml")
})
public class ReprocessamentoSaldoFonteDespesaOrcControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoFonteDespesaOrcFacade facade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterDespesaORC;
    private Date dataEfetivacaoLoa;

    public ReprocessamentoSaldoFonteDespesaOrcControlador() {
    }

    @URLAction(mappingId = "novo-reprocessamento-saldo-fonte-despesa-orc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
        assistente.setReprocessamentoSaldoFonteDespesaOrc(new ReprocessamentoSaldoFonteDespesaOrc());
        assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        dataEfetivacaoLoa = recuperarDataEfetivacaoloa();
    }

    @URLAction(mappingId = "ver-reprocessamento-saldo-fonte-despesa-orc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-fonte-despesa-orcamentaria/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_FONTE_DESPESA_ORC;
    }

    public Date recuperarDataEfetivacaoloa() {
        Date dataEfetivacaoLoa = null;
        LOA loa = facade.getLoaFacade().listaUltimaLoaPorExercicio(facade.getSistemaFacade().getExercicioCorrente());
        if (loa != null) {
            dataEfetivacaoLoa = loa.getDataEfetivacao();
        }
        return dataEfetivacaoLoa;
    }

    private void validarReprocessamentoSaldoInicial() {
        ValidacaoException ve = new ValidacaoException();
        if (dataEfetivacaoLoa == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Loa não efetivada, não é possível reprocessar.");
        }
        ve.lancarException();
        if (assistente.getDataInicial().compareTo(dataEfetivacaoLoa) <= 0 && !assistente.getReprocessamentoSaldoFonteDespesaOrc().getReprocessarSaldoInicial()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial é menor ou igual a data do saldo inicial " + DataUtil.getDataFormatada(dataEfetivacaoLoa) + ". Portanto, é obrigatório marcar a opção 'Reprocessar Saldo Inicial'.");
        } else if (assistente.getDataInicial().compareTo(dataEfetivacaoLoa) > 0 && assistente.getReprocessamentoSaldoFonteDespesaOrc().getReprocessarSaldoInicial()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial é maior que a data do saldo inicial " + DataUtil.getDataFormatada(dataEfetivacaoLoa) + ". Portanto, é obrigatório desmarcar a opção 'Reprocessar Saldo Inicial'.");
        }
        ve.lancarException();
    }

    @Override
    public void inicializarAssistente() {
        super.inicializarAssistente();
        assistente.setFiltro(getFiltrosUtilizados());
    }

    public void reprocessar() {
        try {
            inicializarAssistente();
            validarReprocessamentoSaldoInicial();
            future = facade.reprocessar(assistente);
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private String getFiltrosUtilizados() {
        String retorno = "Reprocessar Saldo Inicial? " + (assistente.getReprocessamentoSaldoFonteDespesaOrc().getReprocessarSaldoInicial() ? "Sim" : "Não");
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional() != null) {
            retorno += "; Unidade Organizacional: " + assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional();
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            retorno += "; Despesa Orçamentária: " + assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC();
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            retorno += "; Fonte de Recursos: " + assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return facade.getUnidadeOrganizacionalFacade().listaHierarquiasVigentes(facade.getSistemaFacade().getDataOperacao(), parte.trim());
    }

    public List<DespesaORC> completarDespesaORC(String parte) {
        return facade.getDespesaORCFacade().buscarDespesasOrcPorExercicioAndUnidade(parte.trim(), assistente.getExercicio(), assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional().getSubordinada(), null);
    }

    public List<SelectItem> getFontesDespesaORC() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            for (FonteDespesaORC fonteDespesaORC : facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC())) {
                toReturn.add(new SelectItem(fonteDespesaORC, "" + fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos()));
            }
        }
        return toReturn;
    }

    public Date getDataEfetivacaoLoa() {
        return dataEfetivacaoLoa;
    }

    public void setDataEfetivacaoLoa(Date dataEfetivacaoLoa) {
        this.dataEfetivacaoLoa = dataEfetivacaoLoa;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public ConverterAutoComplete getConverterDespesaORC() {
        if (converterDespesaORC == null) {
            converterDespesaORC = new ConverterAutoComplete(DespesaORC.class, facade.getDespesaORCFacade());
        }
        return converterDespesaORC;
    }
}
