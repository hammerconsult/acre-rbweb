package br.com.webpublico.controle.rh.saudeservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.saudeservidor.SituacaoAcidente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.SituacaoAcidenteFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Alex
 * @since 22/09/2016 16:46
 */
@ManagedBean(name = "situacaoAcidenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSituacaoAcidente", pattern = "/situacao-acidente/novo/", viewId = "/faces/rh/saudeservidor/situacao-acidente/edita.xhtml"),
    @URLMapping(id = "editarSituacaoAcidente", pattern = "/situacao-acidente/editar/#{situacaoAcidenteControlador.id}/", viewId = "/faces/rh/saudeservidor/situacao-acidente/edita.xhtml"),
    @URLMapping(id = "listarSituacaoAcidente", pattern = "/situacao-acidente/listar/", viewId = "/faces/rh/saudeservidor/situacao-acidente/lista.xhtml"),
    @URLMapping(id = "verSituacaoAcidente", pattern = "/situacao-acidente/ver/#{situacaoAcidenteControlador.id}/", viewId = "/faces/rh/saudeservidor/situacao-acidente/visualizar.xhtml")
})
public class SituacaoAcidenteControlador extends PrettyControlador<SituacaoAcidente> implements Serializable, CRUD {

    @EJB
    private SituacaoAcidenteFacade situacaoAcidenteFacade;

    public SituacaoAcidenteControlador() {
        super(SituacaoAcidente.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/situacao-acidente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return situacaoAcidenteFacade;
    }

    @URLAction(mappingId = "novaSituacaoAcidente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verSituacaoAcidente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarSituacaoAcidente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
