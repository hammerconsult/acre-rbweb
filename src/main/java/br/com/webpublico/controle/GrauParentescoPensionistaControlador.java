package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.entidades.GrauParentescoPensionista;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrauDeParentescoFacade;
import br.com.webpublico.negocios.GrauParentescoPensionistaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ViewScoped;

/**
 * @author Claudio
 */
@ManagedBean(name = "grauParentescoPensionistaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novograuparentescopensionista", pattern = "/grauparentescopensionista/novo/", viewId = "/faces/rh/administracaodepagamento/graudeparentescopensionista/edita.xhtml"),
    @URLMapping(id = "editargrauparentescopensionista", pattern = "/grauparentescopensionista/editar/#{grauParentescoPensionistaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/graudeparentescopensionista/edita.xhtml"),
    @URLMapping(id = "vergrauparentescopensionista", pattern = "/grauparentescopensionista/ver/#{grauParentescoPensionistaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/graudeparentescopensionista/visualizar.xhtml"),
    @URLMapping(id = "listargrauparentescopensionista", pattern = "/grauparentescopensionista/listar/", viewId = "/faces/rh/administracaodepagamento/graudeparentescopensionista/lista.xhtml")
})
public class GrauParentescoPensionistaControlador extends PrettyControlador<GrauParentescoPensionista> implements Serializable, CRUD {

    @EJB
    private GrauParentescoPensionistaFacade GrauParentescoPensionistaFacade;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    private ConverterAutoComplete converterGrauDeParentesco;

    @Override
    public AbstractFacade getFacede() {
        return this.GrauParentescoPensionistaFacade;
    }

    public GrauParentescoPensionistaControlador() {
        super(GrauParentescoPensionista.class);
    }

    @URLAction(mappingId = "novograuparentescopensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "vergrauparentescopensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editargrauparentescopensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCodigo()) {
            super.salvar();
        }
    }

    public boolean validaCodigo() {
        boolean retorno = true;
        if(((GrauParentescoPensionista) selecionado).getCodigo() == null){
            FacesUtil.addError("", "O campo Código é obrigatório.");
            retorno = false;
        }
        else if (GrauParentescoPensionistaFacade.existeCodigo((GrauParentescoPensionista) selecionado)) {
            FacesUtil.addWarn("Atenção!", "O código informado pertence a outro registro.");
            retorno = false;
        }
        return retorno;
    }

    public List<GrauDeParentesco> completaGrauDeParentesco(String parte) {
        return grauDeParentescoFacade.listaFiltrando(parte.trim());
    }

    public Converter getConverterGrauDeParentesco() {
        if (converterGrauDeParentesco == null) {
            converterGrauDeParentesco = new ConverterAutoComplete(GrauDeParentesco.class, grauDeParentescoFacade);
        }
        return converterGrauDeParentesco;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grauparentescopensionista/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
