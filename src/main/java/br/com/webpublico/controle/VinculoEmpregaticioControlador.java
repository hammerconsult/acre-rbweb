/*
 * Codigo gerado automaticamente em Tue Feb 07 11:11:43 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.VinculoEmpregaticio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VinculoEmpregaticioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "vinculoEmpregaticioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoVinculoEmpregaticio", pattern = "/vinculoempregaticio/novo/", viewId = "/faces/rh/administracaodepagamento/vinculoempregaticio/edita.xhtml"),
    @URLMapping(id = "editarVinculoEmpregaticio", pattern = "/vinculoempregaticio/editar/#{vinculoEmpregaticioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/vinculoempregaticio/edita.xhtml"),
    @URLMapping(id = "listarVinculoEmpregaticio", pattern = "/vinculoempregaticio/listar/", viewId = "/faces/rh/administracaodepagamento/vinculoempregaticio/lista.xhtml"),
    @URLMapping(id = "verVinculoEmpregaticio", pattern = "/vinculoempregaticio/ver/#{vinculoEmpregaticioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/vinculoempregaticio/visualizar.xhtml")
})
public class VinculoEmpregaticioControlador extends PrettyControlador<VinculoEmpregaticio> implements Serializable, CRUD {

    @EJB
    private VinculoEmpregaticioFacade vinculoEmpregaticioFacade;

    public VinculoEmpregaticioControlador() {
        super(VinculoEmpregaticio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return vinculoEmpregaticioFacade;
    }

    @URLAction(mappingId="novoVinculoEmpregaticio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId="verVinculoEmpregaticio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId="editarVinculoEmpregaticio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        VinculoEmpregaticio tipoSelecionado = (VinculoEmpregaticio) selecionado;
        if (vinculoEmpregaticioFacade.existeCodigo(tipoSelecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Vínculo Empregatício");
            return false;
        }
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vinculoempregaticio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
