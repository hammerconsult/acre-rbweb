package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProfissionalConfea;
import br.com.webpublico.enums.GrupoConfea;
import br.com.webpublico.enums.ModalidadeConfea;
import br.com.webpublico.enums.NivelConfea;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProfissionalConfeaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by venom on 03/11/14.
 */
@ManagedBean(name = "profissionalConfeaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-profissionalconfea", pattern = "/profissionalconfea/novo/", viewId = "/faces/administrativo/obras/profissionalconfea/edita.xhtml"),
        @URLMapping(id = "editar-profissionalconfea", pattern = "/profissionalconfea/editar/#{profissionalConfeaControlador.id}/", viewId = "/faces/administrativo/obras/profissionalconfea/edita.xhtml"),
        @URLMapping(id = "ver-profissionalconfea", pattern = "/profissionalconfea/ver/#{profissionalConfeaControlador.id}/", viewId = "/faces/administrativo/obras/profissionalconfea/visualizar.xhtml"),
        @URLMapping(id = "listar-profissionalconfea", pattern = "/profissionalconfea/listar/", viewId = "/faces/administrativo/obras/profissionalconfea/lista.xhtml")
})
public class ProfissionalConfeaControlador extends PrettyControlador<ProfissionalConfea> implements Serializable, CRUD {

    @EJB
    private ProfissionalConfeaFacade profissionalConfeaFacade;

    public ProfissionalConfeaControlador() {
        super(ProfissionalConfea.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return profissionalConfeaFacade;
    }

    @URLAction(mappingId = "novo-profissionalconfea", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-profissionalconfea", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-profissionalconfea", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getGruposConfea() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (GrupoConfea gc : GrupoConfea.values()) {
            lista.add(new SelectItem(gc, gc.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getModalidadesConfea() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (ModalidadeConfea mc : ModalidadeConfea.values()) {
            lista.add(new SelectItem(mc, mc.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getNiveisConfea() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (NivelConfea nc : NivelConfea.values()) {
            lista.add(new SelectItem(nc, nc.getDescricao()));
        }
        return lista;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/profissionalconfea/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
