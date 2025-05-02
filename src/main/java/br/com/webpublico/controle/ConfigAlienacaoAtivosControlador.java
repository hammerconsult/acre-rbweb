package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAlienacaoAtivos;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Desenvolvimento
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-alienacao-ativos", pattern = "/configuracao-alienacao-ativos/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoalienacaoativos/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-alienacao-ativos", pattern = "/configuracao-alienacao-ativos/editar/#{configAlienacaoAtivosControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoalienacaoativos/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-alienacao-ativos", pattern = "/configuracao-alienacao-ativos/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoalienacaoativos/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-alienacao-ativos", pattern = "/configuracao-alienacao-ativos/ver/#{configAlienacaoAtivosControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoalienacaoativos/visualizar.xhtml")
})

public class ConfigAlienacaoAtivosControlador extends PrettyControlador<ConfigAlienacaoAtivos> implements Serializable, CRUD {

    private static List<String> tiposEventoContabil = Lists.newArrayList(TipoEventoContabil.BENS_ESTOQUE.name(),
        TipoEventoContabil.BENS_IMOVEIS.name(), TipoEventoContabil.BENS_INTANGIVEIS.name(), TipoEventoContabil.BENS_MOVEIS.name(), TipoEventoContabil.RECEITA_REALIZADA.name());
    @EJB
    private ConfigAlienacaoAtivosFacade configAlienacaoAtivosFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    private ConverterAutoComplete converterEventoContabil;

    public ConfigAlienacaoAtivosControlador() {
        super(ConfigAlienacaoAtivos.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configAlienacaoAtivosFacade;
    }

    @URLAction(mappingId = "novo-configuracao-alienacao-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "ver-configuracao-alienacao-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-configuracao-alienacao-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-alienacao-ativos/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipoGrupos() {
        return Util.getListSelectItem(TipoGrupo.values());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência.");
            }
        }
        ve.lancarException();
        configAlienacaoAtivosFacade.verificarConfiguracaoExistente(selecionado);
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return configAlienacaoAtivosFacade.getEventoContabilFacade().buscarEventosContabeisPorTiposEvento(parte, tiposEventoContabil);
    }

    public List<GrupoBem> buscarGrupoPatrimonial(String s) {
        switch (selecionado.getTipoGrupo()) {
            case BEM_MOVEL_PRINCIPAL:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.MOVEIS);
            case BEM_MOVEL_ALIENAR:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.MOVEIS);
            case BEM_MOVEL_INSERVIVEL:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.MOVEIS);
            case BEM_MOVEL_INTEGRACAO:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.MOVEIS);
            case BEM_IMOVEL_ALIENAR:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.IMOVEIS);
            case BEM_IMOVEL_INSERVIVEL:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.IMOVEIS);
            case BEM_IMOVEL_INTEGRACAO:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.IMOVEIS);
            case BEM_IMOVEL_PRINCIPAL:
                return grupoBemFacade.buscarGrupoBemPorTipoBem(s, TipoBem.IMOVEIS);
        }
        return null;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configAlienacaoAtivosFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void validaDataInicioVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataVigencia = (Date) value;
        Calendar dataInicioVigencia = Calendar.getInstance();
        dataInicioVigencia.setTime(dataVigencia);
        Integer ano = sistemaFacade.getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary("Data inválida! ");
            message.setDetail(" Ano diferente do exercício corrente!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public boolean hasFimDeVigenciaNullOrDataMaiorQueLogada() {
        if (selecionado.getFimVigencia() == null) {
            return true;
        }
        return Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao())) >= 0;
    }

    public void limparEvento() {
        selecionado.setEvento(null);
    }

    public void encerrarVigencia() {
        try {
            configAlienacaoAtivosFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
