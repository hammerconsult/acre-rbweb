package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.TipoSextaParte;
import br.com.webpublico.entidades.TipoSextaPartePeriodoExcludente;
import br.com.webpublico.entidades.TipoSextaParteUnidadeOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoSextaParteFacade;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 27/07/2016.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoSextaParte", pattern = "/tiposextaparte/novo/", viewId = "/faces/rh/administracaodepagamento/tiposextaparte/editar.xhtml"),
    @URLMapping(id = "editarTipoSextaParte", pattern = "/tiposextaparte/editar/#{tipoSextaParteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiposextaparte/editar.xhtml"),
    @URLMapping(id = "listarTipoSextaParte", pattern = "/tiposextaparte/listar/", viewId = "/faces/rh/administracaodepagamento/tiposextaparte/listar.xhtml"),
    @URLMapping(id = "verTipoSextaParte", pattern = "/tiposextaparte/ver/#{tipoSextaParteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiposextaparte/visualizar.xhtml")
})
public class TipoSextaParteControlador extends PrettyControlador<TipoSextaParte> implements CRUD, Serializable {

    @EJB
    private TipoSextaParteFacade tipoSextaParteFacade;
    private TipoSextaPartePeriodoExcludente tipoSextaPartePeriodoExcludente;
    private TipoSextaParteUnidadeOrganizacional tipoSextaParteUnidadeOrganizacional;

    public TipoSextaParteControlador() {
        super(TipoSextaParte.class);
    }

    public List<SelectItem> getEventosFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP eventoFP : tipoSextaParteFacade.getEventoFPFacade().buscarFiltrandoEventosAtivosOrdenadoPorCodigo("")) {
            toReturn.add(new SelectItem(eventoFP, eventoFP.toString()));
        }
        return toReturn;
    }

    @Override
    @URLAction(mappingId = "novoTipoSextaParte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verTipoSextaParte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarTipoSextaParte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tiposextaparte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoSextaParteFacade;
    }

    public TipoSextaPartePeriodoExcludente getTipoSextaPartePeriodoExcludente() {
        return tipoSextaPartePeriodoExcludente;
    }

    public void setTipoSextaPartePeriodoExcludente(TipoSextaPartePeriodoExcludente tipoSextaPartePeriodoExcludente) {
        this.tipoSextaPartePeriodoExcludente = tipoSextaPartePeriodoExcludente;
    }

    public TipoSextaParteUnidadeOrganizacional getTipoSextaParteUnidadeOrganizacional() {
        return tipoSextaParteUnidadeOrganizacional;
    }

    public void setTipoSextaParteUnidadeOrganizacional(TipoSextaParteUnidadeOrganizacional tipoSextaParteUnidadeOrganizacional) {
        this.tipoSextaParteUnidadeOrganizacional = tipoSextaParteUnidadeOrganizacional;
    }

    public void validarPeriodoExcludente() {
        tipoSextaPartePeriodoExcludente.realizarValidacoes();
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(tipoSextaPartePeriodoExcludente.getInicio(), tipoSextaPartePeriodoExcludente.getFim())) {
            throw new ValidacaoException("O Início não deve ser superior ao Fim.");
        }
        if (DataUtil.isPeriodoConcomitante(tipoSextaPartePeriodoExcludente, selecionado.getPeriodosExcludente())) {
            throw new ValidacaoException("Não é possível a inclusão de registros com períodos concomitantes.");
        }
    }

    public void salvarPeriodoExcludente() {
        try {
            validarPeriodoExcludente();
            tipoSextaPartePeriodoExcludente.setTipoSextaParte(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getPeriodosExcludente(), tipoSextaPartePeriodoExcludente);
            tipoSextaPartePeriodoExcludente = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarPeriodoExcludente() {
        tipoSextaPartePeriodoExcludente = null;
    }

    public void novoPeriodoExcludente() {
        this.tipoSextaPartePeriodoExcludente = new TipoSextaPartePeriodoExcludente();
    }

    public void alterarPeriodoExcludente(TipoSextaPartePeriodoExcludente tipoSextaPartePeriodoExcludente) {
        this.tipoSextaPartePeriodoExcludente = (TipoSextaPartePeriodoExcludente) Util.clonarObjeto(tipoSextaPartePeriodoExcludente);
    }

    public void removerPeriodoExcludente(TipoSextaPartePeriodoExcludente tipoSextaPartePeriodoExcludente) {
        selecionado.getPeriodosExcludente().remove(tipoSextaPartePeriodoExcludente);
    }

    public void validarUnidadeOrganizacional() {
        tipoSextaParteUnidadeOrganizacional.realizarValidacoes();
    }

    public void salvarUnidadeOrganizacional() {
        try {
            validarUnidadeOrganizacional();
            tipoSextaParteUnidadeOrganizacional.setTipoSextaParte(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidadesOganizacional(), tipoSextaParteUnidadeOrganizacional);
            tipoSextaParteUnidadeOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarUnidadeOrganizacional() {
        tipoSextaParteUnidadeOrganizacional = null;
    }

    public void novoUnidadeOrganizacional() {
        this.tipoSextaParteUnidadeOrganizacional = new TipoSextaParteUnidadeOrganizacional();
    }

    public void alterarUnidadeOrganizacional(TipoSextaParteUnidadeOrganizacional tipoSextaParteUnidadeOrganizacional) {
        this.tipoSextaParteUnidadeOrganizacional = (TipoSextaParteUnidadeOrganizacional) Util.clonarObjeto(tipoSextaParteUnidadeOrganizacional);
    }

    public void removerUnidadeOrganizacional(TipoSextaParteUnidadeOrganizacional tipoSextaParteUnidadeOrganizacional) {
        selecionado.getUnidadesOganizacional().remove(tipoSextaParteUnidadeOrganizacional);
    }

    public Date getDataOperacao() {
        return tipoSextaParteFacade.getSistemaFacade().getDataOperacao();
    }

}
