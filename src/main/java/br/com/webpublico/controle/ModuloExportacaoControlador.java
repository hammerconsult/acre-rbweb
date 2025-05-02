/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ModuloExportacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModuloExportacaoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "moduloExportacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoModuloExportacao", pattern = "/modulo-exportacao/novo/", viewId = "/faces/rh/administracaodepagamento/moduloexportacao/edita.xhtml"),
        @URLMapping(id = "editarModuloExportacao", pattern = "/modulo-exportacao/editar/#{moduloExportacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/moduloexportacao/edita.xhtml"),
        @URLMapping(id = "listarModuloExportacao", pattern = "/modulo-exportacao/listar/", viewId = "/faces/rh/administracaodepagamento/moduloexportacao/lista.xhtml"),
        @URLMapping(id = "verModuloExportacao", pattern = "/modulo-exportacao/ver/#{moduloExportacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/moduloexportacao/visualizar.xhtml")
})
public class ModuloExportacaoControlador extends PrettyControlador<ModuloExportacao> implements Serializable, CRUD {

    @EJB
    private ModuloExportacaoFacade moduloExportacaoFacade;

    public ModuloExportacaoControlador() {
        super(ModuloExportacao.class);
    }

    public ModuloExportacaoFacade getFacade() {
        return moduloExportacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return moduloExportacaoFacade;
    }

    @URLAction(mappingId = "editarModuloExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }

    @URLAction(mappingId = "verModuloExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoModuloExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        ModuloExportacao moduloSelecionado = (ModuloExportacao) selecionado;
        if (moduloExportacaoFacade.existeCodigo(moduloSelecionado)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Código informado já está cadastrado em outro Módulo de Exportação !");
            FacesContext.getCurrentInstance().addMessage("msg", msg);
            return false;
        }
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modulo-exportacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
