package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ParametroLicencaPremio;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.ParametroLicencaPremioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
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
import java.util.List;

@ManagedBean(name = "parametroLicencaPremioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroLicencaPremioControlador", pattern = "/parametro-licenca-premio/novo/", viewId = "/faces/rh/administracaodepagamento/parametrolicencapremio/edita.xhtml"),
    @URLMapping(id = "editarParametroLicencaPremioControlador", pattern = "/parametro-licenca-premio/editar/#{parametroLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametrolicencapremio/edita.xhtml"),
    @URLMapping(id = "listarParametroLicencaPremioControlador", pattern = "/parametro-licenca-premio/listar/", viewId = "/faces/rh/administracaodepagamento/parametrolicencapremio/lista.xhtml"),
    @URLMapping(id = "verParametroLicencaPremioControlador", pattern = "/parametro-licenca-premio/ver/#{parametroLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametrolicencapremio/visualizar.xhtml")
})
public class ParametroLicencaPremioControlador extends PrettyControlador<ParametroLicencaPremio> implements Serializable, CRUD {

    @EJB
    private ParametroLicencaPremioFacade parametroLicencaPremioFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterEventoFP;

    public ParametroLicencaPremioControlador() {
        super(ParametroLicencaPremio.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-licenca-premio/";
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroLicencaPremioFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setQuantidadeDiasPerdaPeriodo(0);
    }

    @URLAction(mappingId = "verParametroLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            setaCodigoDoParametro();
            if (validaRegraDeNegocio()) {
                if (isOperacaoNovo()) {
                    parametroLicencaPremioFacade.salvarNovo(selecionado);
                    FacesUtil.addInfo("Operação Realizada! ", " Registro salvo com sucesso.");
                    redireciona();

                } else {
                    parametroLicencaPremioFacade.salvar(selecionado);
                    FacesUtil.addInfo("Operação Realizada! ", " Registro alterado com sucesso.");
                    redireciona();
                }
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Operação não Realizada! " + ex.getMessage());
        }
    }

    private void setaCodigoDoParametro() {
        if (isOperacaoNovo()) {
            selecionado.setCodigoLicenca(getProximoCodigo());
        }
    }

    private Integer getProximoCodigo() {
        List<ParametroLicencaPremio> parametros = parametroLicencaPremioFacade.lista();
        if (parametros != null && !parametros.isEmpty()) {
            return parametros.size() + 1;
        }
        return 1;
    }

    private boolean validaRegraDeNegocio() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!validaEventoFP()) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(selecionado, parametroLicencaPremioFacade.lista())) {
            return false;
        }
        return true;
    }

    public boolean validaEventoFP() {
        if (selecionado.getRemunerada() && (selecionado.getEventoFP() == null || selecionado.getEventoFP().getId() == null)) {
            FacesUtil.addCampoObrigatorio("O campo Evento FP é obrigatório!");
            return false;
        }
        return true;
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoAutoComplete(parte.trim(), "codigo", "descricao", "descricaoReduzida");
    }

    public ConverterAutoComplete getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public void salvarEncerrandoVigencia() {
        ParametroLicencaPremio parametroLicencaPremio = parametroLicencaPremioFacade.recuperaVigente();
        parametroLicencaPremio.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getInicioVigencia()));
        parametroLicencaPremioFacade.salvar(parametroLicencaPremio);
        salvar();
    }

    public List<SelectItem> getReferenciaFP() {
        return Util.getListSelectItem(parametroLicencaPremioFacade.getReferenciaFPFacade().listaDecrescente());
    }
}
