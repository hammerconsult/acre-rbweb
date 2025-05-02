package br.com.webpublico.controle;

import br.com.webpublico.entidades.Funeraria;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FunerariaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@ManagedBean(name = "funerariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFuneraria", pattern = "/funeraria/novo/", viewId = "/faces/funeral/funeraria/edita.xhtml"),
    @URLMapping(id = "editarFuneraria", pattern = "/funeraria/editar/#{funerariaControlador.id}/", viewId = "/faces/funeral/funeraria/edita.xhtml"),
    @URLMapping(id = "listarFuneraria", pattern = "/funeraria/listar/", viewId = "/faces/funeral/funeraria/lista.xhtml"),
    @URLMapping(id = "verFuneraria", pattern = "/funeraria/ver/#{funerariaControlador.id}/", viewId = "/faces/funeral/funeraria/visualizar.xhtml")
})
public class FunerariaControlador extends PrettyControlador<Funeraria> implements Serializable, CRUD {

    @EJB
    private FunerariaFacade funerariaFacade;

    public FunerariaControlador() {
        super(Funeraria.class);
    }

    @URLAction(mappingId = "novoFuneraria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verFuneraria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarFuneraria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/funeraria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return funerariaFacade;
    }
}
