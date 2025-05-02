package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoIsencao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoIsencaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by venom on 16/10/14.
 */
@ManagedBean(name = "motivoIsencaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoIsencao", pattern = "/motivo-isencao/novo/", viewId = "/faces/rh/concursos/isencao/edita.xhtml"),
        @URLMapping(id = "editarMotivoIsencao", pattern = "/motivo-isencao/editar/#{motivoIsencaoControlador.id}/", viewId = "/faces/rh/concursos/isencao/edita.xhtml"),
        @URLMapping(id = "verMotivoIsencao", pattern = "/motivo-isencao/ver/#{motivoIsencaoControlador.id}/", viewId = "/faces/rh/concursos/isencao/visualizar.xhtml"),
        @URLMapping(id = "listarMotivoIsencao", pattern = "/motivo-isencao/listar/", viewId = "/faces/rh/concursos/isencao/lista.xhtml")
})
public class MotivoIsencaoControlador extends PrettyControlador<MotivoIsencao> implements Serializable, CRUD {

    @EJB
    private MotivoIsencaoFacade motivoIsencaoFacade;

    public MotivoIsencaoControlador() {
        super(MotivoIsencao.class);
    }

    @URLAction(mappingId = "novoMotivoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMotivoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMotivoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoIsencaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/motivo-isencao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
