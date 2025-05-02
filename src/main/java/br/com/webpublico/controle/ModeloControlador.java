/*
 * Codigo gerado automaticamente em Tue Oct 18 14:56:33 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Marca;
import br.com.webpublico.entidades.Modelo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MarcaFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.negocios.ModeloFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "modeloControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoModelo", pattern = "/modelo/novo/", viewId = "/faces/administrativo/materiais/modelo/edita.xhtml"),
    @URLMapping(id = "editarModelo", pattern = "/modelo/editar/#{modeloControlador.id}/", viewId = "/faces/administrativo/materiais/modelo/edita.xhtml"),
    @URLMapping(id = "listarModelo", pattern = "/modelo/listar/", viewId = "/faces/administrativo/materiais/modelo/lista.xhtml"),
    @URLMapping(id = "verModelo", pattern = "/modelo/ver/#{modeloControlador.id}/", viewId = "/faces/administrativo/materiais/modelo/visualizar.xhtml")
})
public class ModeloControlador extends PrettyControlador<Modelo> implements Serializable, CRUD {

    @EJB
    private ModeloFacade modeloFacade;
    @EJB
    private MarcaFacade marcaFacade;
    @EJB
    private MaterialFacade materialFacade;
    private Boolean permitirAlterarMarca;

    public ModeloControlador() {
        super(Modelo.class);
    }

    public ModeloFacade getFacade() {
        return modeloFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return modeloFacade;
    }

    @Override
    public void excluir() {
        if (validaExclusao()) {
            super.excluir();
        }
    }

    private boolean validaExclusao() {
        boolean validou = true;

        if (!validaSeModeloPossuiVinculoComMaterial()) {
            validou = false;
        }

        return validou;
    }

    private boolean validaSeModeloPossuiVinculoComMaterial() {
        if (selecionadoPossuiVinculoComMaterial()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O modelo selecionado está vinculado a um ou mais materiais.");
            return false;
        }

        return true;
    }

    private boolean selecionadoPossuiVinculoComMaterial() {
        if (materialFacade.buscarMateriaisPorModelo(selecionado).isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modelo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoModelo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        permitirAlterarMarca = false;
        Marca marca = (Marca) Web.pegaDaSessao(Marca.class);
        if (marca != null) {
            selecionado.setMarca(marca);
            permitirAlterarMarca = true;
        }
    }

    @URLAction(mappingId = "editarModelo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verModelo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public Boolean getPermitirAlterarMarca() {
        return permitirAlterarMarca;
    }

    public void setPermitirAlterarMarca(Boolean permitirAlterarMarca) {
        this.permitirAlterarMarca = permitirAlterarMarca;
    }

}
