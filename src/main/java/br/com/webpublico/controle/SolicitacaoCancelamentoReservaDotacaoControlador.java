package br.com.webpublico.controle;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.SaldoFonteDespesaORC;
import br.com.webpublico.entidades.SolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.SituacaoSolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoCancelamentoReservaDotacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/06/14
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "solicitacaoCancelamentoReservaDotacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-cancelamento-reserva-dotacao", pattern = "/solicitacao-cancelamento-reserva-dotacao/novo/", viewId = "/faces/financeiro/orcamentario/solicitacaocancelamentoreservadotacao/edita.xhtml"),
    @URLMapping(id = "editar-solicitacao-cancelamento-reserva-dotacao", pattern = "/solicitacao-cancelamento-reserva-dotacao/editar/#{solicitacaoCancelamentoReservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/solicitacaocancelamentoreservadotacao/edita.xhtml"),
    @URLMapping(id = "ver-solicitacao-cancelamento-reserva-dotacao", pattern = "/solicitacao-cancelamento-reserva-dotacao/ver/#{solicitacaoCancelamentoReservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/solicitacaocancelamentoreservadotacao/visualizar.xhtml"),
    @URLMapping(id = "listar-solicitacao-cancelamento-reserva-dotacao", pattern = "/solicitacao-cancelamento-reserva-dotacao/listar/", viewId = "/faces/financeiro/orcamentario/solicitacaocancelamentoreservadotacao/lista.xhtml")
})
public class SolicitacaoCancelamentoReservaDotacaoControlador extends PrettyControlador<SolicitacaoCancelamentoReservaDotacao> implements Serializable, CRUD {

    @EJB
    private SolicitacaoCancelamentoReservaDotacaoFacade solicitacaoCancelamentoReservaDotacaoFacade;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private ConverterAutoComplete converterFonteDespesaORC;
    private SaldoFonteDespesaORC saldoFonteDespesaORC;

    public SolicitacaoCancelamentoReservaDotacaoControlador() {
        super(SolicitacaoCancelamentoReservaDotacao.class);
    }

    @URLAction(mappingId = "novo-solicitacao-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(getSistemaControlador().getDataOperacao());
        selecionado.setUnidadeOrganizacional(getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUsuarioSolicitante(getSistemaControlador().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoSolicitacaoCancelamentoReservaDotacao.ABERTA);
        limparComponenteTreeDespesaORC();
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "ver-solicitacao-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-solicitacao-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        componenteTreeDespesaORC.setCodigo(selecionado.getFonteDespesaORC().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getFonteDespesaORC().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(solicitacaoCancelamentoReservaDotacaoFacade.getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getFonteDespesaORC().getDespesaORC().getId()).getConta());
        buscarSaldoOrcamentario();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-cancelamento-reserva-dotacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoCancelamentoReservaDotacaoFacade;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, solicitacaoCancelamentoReservaDotacaoFacade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        if (componenteTreeDespesaORC != null) {
            if (componenteTreeDespesaORC.getDespesaORCSelecionada() != null) {
                return solicitacaoCancelamentoReservaDotacaoFacade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte.trim(), componenteTreeDespesaORC.getDespesaORCSelecionada());
            }
        }
        return new ArrayList<FonteDespesaORC>();
    }

//    GETTER E SETTER

    public SaldoFonteDespesaORC getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(SaldoFonteDespesaORC saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public List<SelectItem> getListaOrigensRecursos() {
        return Util.getListSelectItem(OrigemReservaFonte.values());
    }

    public void limparDespesaOrcEFonte() {
        limparComponenteTreeDespesaORC();
        selecionado.setFonteDespesaORC(null);
        saldoFonteDespesaORC = null;
    }

    private void limparComponenteTreeDespesaORC() {
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(null);
        componenteTreeDespesaORC.setDespesaSTR("");
    }

    public void buscarSaldoOrcamentario() {
        if (selecionado.getFonteDespesaORC() != null) {
            saldoFonteDespesaORC = solicitacaoCancelamentoReservaDotacaoFacade.getSaldoFonteDespesaORCFacade().
                recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(selecionado.getFonteDespesaORC(), selecionado.getData(), selecionado.getUnidadeOrganizacional());
        }
    }

    public BigDecimal getSaldo() {
        if (saldoFonteDespesaORC != null && selecionado.getOrigemReservaFonte() != null) {
            if (selecionado.getOrigemReservaFonte().equals(OrigemReservaFonte.LICITACAO)) {
                return saldoFonteDespesaORC.getReservadoPorLicitacao();
            } else {
                return saldoFonteDespesaORC.getReservado();
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (selecionado.getValor().compareTo(getSaldo()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O Valor a ser cancelado não pode ser maior que o saldo reservado.");
            retorno = false;
        }
        return retorno;
    }
}
