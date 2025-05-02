package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.TipoCumprimentoAvisoPrevio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.cadastrofuncional.AvisoPrevioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by William on 01/10/2018.
 */
@ManagedBean(name = "avisoPrevioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-aviso-previo", pattern = "/aviso-previo/novo/", viewId = "/faces/rh/cadastrofuncional/aviso-previo/edita.xhtml"),
    @URLMapping(id = "editar-aviso-previo", pattern = "/aviso-previo/editar/#{avisoPrevioControlador.id}/", viewId = "/faces/rh/cadastrofuncional/aviso-previo/edita.xhtml"),
    @URLMapping(id = "listar-aviso-previo", pattern = "/aviso-previo/listar/", viewId = "/faces/rh/cadastrofuncional/aviso-previo/lista.xhtml"),
    @URLMapping(id = "ver-aviso-previo", pattern = "/aviso-previo/ver/#{avisoPrevioControlador.id}/", viewId = "/faces/rh/cadastrofuncional/aviso-previo/visualizar.xhtml")
})
public class AvisoPrevioControlador extends PrettyControlador<AvisoPrevio> implements CRUD {

    @EJB
    private AvisoPrevioFacade avisoPrevioFacade;

    public AvisoPrevioControlador() {
        super(AvisoPrevio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return avisoPrevioFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aviso-previo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-aviso-previo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-aviso-previo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-aviso-previo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTipoAvisoPrevio() {
        return Util.getListSelectItem(TipoAvisoPrevio.values());
    }


    public List<VinculoFP> completaContrato(String s) {
        return avisoPrevioFacade.getContratoFPFacade().buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    @Override
    public void salvar() {
        try {
            validarAvisoPrevio();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAvisoPrevio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataAviso() != null && selecionado.getDataDesligamento() != null) {
            if (selecionado.getDataAviso().after(selecionado.getDataDesligamento())) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("A Data do Aviso Prévio não pode ser superior a Data do Desligamento");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoCumprimentoAvisoPrevio() {
        return Util.getListSelectItem(TipoCumprimentoAvisoPrevio.values(), false);
    }
}
