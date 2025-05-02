package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAtoPotencial;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoPotencialFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ato-potencial", pattern = "/ato-potencial/novo/", viewId = "/faces/financeiro/orcamentario/atopotencial/edita.xhtml"),
    @URLMapping(id = "edita-ato-potencial", pattern = "/ato-potencial/editar/#{atoPotencialControlador.id}/", viewId = "/faces/financeiro/orcamentario/atopotencial/edita.xhtml"),
    @URLMapping(id = "listar-ato-potencial", pattern = "/ato-potencial/listar/", viewId = "/faces/financeiro/orcamentario/atopotencial/lista.xhtml"),
    @URLMapping(id = "ver-ato-potencial", pattern = "/ato-potencial/ver/#{atoPotencialControlador.id}/", viewId = "/faces/financeiro/orcamentario/atopotencial/visualizar.xhtml")
})
public class AtoPotencialControlador extends PrettyControlador<AtoPotencial> implements Serializable, CRUD {

    @EJB
    private AtoPotencialFacade facade;

    public AtoPotencialControlador() {
        super(AtoPotencial.class);
    }

    @URLAction(mappingId = "novo-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        selecionado.setData(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);

        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "edita-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<TipoLancamento> getTiposDeLancamentos() {
        return Arrays.asList(TipoLancamento.values());
    }

    public List<SelectItem> getTiposDeAtoPotencial() {
        return Util.getListSelectItem(TipoAtoPotencial.values(), false);
    }

    public List<SelectItem> getTiposDeOperacoesAtoPotencial() {
        return Util.getListSelectItem(TipoOperacaoAtoPotencial.buscarOperacoesPorTipoAtoPotencial(selecionado.getTipoAtoPotencial()), false);
    }

    public void atualizarOperacaoAndEvento() {
        selecionado.setTipoOperacaoAtoPotencial(null);
        selecionado.setContrato(null);
        selecionado.setConvenioDespesa(null);
        selecionado.setConvenioReceita(null);
        atualizarEvento();
    }

    public void atualizarEvento() {
        try {
            if (selecionado.getTipoOperacaoAtoPotencial() != null) {
                selecionado.setEventoContabil(null);
                EventoContabil eventoContabil = facade.buscarEventoContabil(selecionado);
                if (eventoContabil != null) {
                    selecionado.setEventoContabil(eventoContabil);
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }

    public List<ConvenioReceita> completarConveniosDeReceita(String filtro) {
        return facade.getConvenioReceitaFacade().buscarFiltrandoConvenioReceita(filtro);
    }

    public List<ConvenioDespesa> completarConveniosDeDespesa(String filtro) {
        return facade.getConvenioDespesaFacade().listaFiltrando(filtro.trim(), "numConvenio", "objeto");
    }

    public List<Contrato> completarContratos(String filtro) {
        return facade.getContratoFacade().listaFiltrandoContrato(
            filtro.trim(),
            facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Evento Contábil deve ser informado.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor dever ser maior que zero(0).");
        }
        ve.lancarException();
        validarCamposConvenioOrContrato();
    }

    private void validarCamposConvenioOrContrato() {
        ValidacaoException ve  = new ValidacaoException();
        if (selecionado.getTipoAtoPotencial().isConvenioReceita() && selecionado.getConvenioReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Convênio de Receita deve ser informado.");
        } else if (selecionado.getTipoAtoPotencial().isConvenioDespesa() && selecionado.getConvenioDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Convênio de Despesa deve ser informado.");
        } else if (selecionado.getTipoAtoPotencial().isContrato() && selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ato-potencial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
