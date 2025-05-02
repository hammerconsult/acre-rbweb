package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrauEscolaridadeSiprev;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrauEscolaridadeSiprevFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "grauEscolaridadeSiprevControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoGrauEscolaridadeSiprev", pattern = "/escolaridade-siprev/novo/", viewId = "/faces/rh/administracaodepagamento/grauescolaridadesiprev/edita.xhtml"),
        @URLMapping(id = "editarGrauEscolaridadeSiprev", pattern = "/escolaridade-siprev/editar/#{grauEscolaridadeSiprevControlador.id}/", viewId = "/faces/rh/administracaodepagamento/grauescolaridadesiprev/edita.xhtml"),
        @URLMapping(id = "listarGrauEscolaridadeSiprev", pattern = "/escolaridade-siprev/listar/", viewId = "/faces/rh/administracaodepagamento/grauescolaridadesiprev/lista.xhtml"),
        @URLMapping(id = "verGrauEscolaridadeSiprev", pattern = "/escolaridade-siprev/ver/#{grauEscolaridadeSiprevControlador.id}/", viewId = "/faces/rh/administracaodepagamento/grauescolaridadesiprev/visualizar.xhtml")
})
public class GrauEscolaridadeSiprevControlador extends PrettyControlador<GrauEscolaridadeSiprev> implements Serializable, CRUD {

    @EJB
    private GrauEscolaridadeSiprevFacade grauEscolaridadeSiprevFacade;

    public GrauEscolaridadeSiprevControlador() {
        super(GrauEscolaridadeSiprev.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return grauEscolaridadeSiprevFacade;
    }

    @URLAction(mappingId = "novoGrauEscolaridadeSiprev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verGrauEscolaridadeSiprev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarGrauEscolaridadeSiprev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        if(grauEscolaridadeSiprevFacade.validaCodigo(this.selecionado)){
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "O código já foi utilizado em outro registro!");
            return false;
        }
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/escolaridade-siprev/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
