/*
 * Codigo gerado automaticamente em Tue Dec 06 10:05:41 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.FormaCobranca;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FormaCobrancaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.List;

@ManagedBean(name = "formaCobrancaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaFormaCobranca", pattern = "/forma-de-cobranca/novo/", viewId = "/faces/tributario/formacobranca/edita.xhtml"),
        @URLMapping(id = "editarFormaCobranca", pattern = "/forma-de-cobranca/editar/#{formaCobrancaControlador.id}/", viewId = "/faces/tributario/formacobranca/edita.xhtml"),
        @URLMapping(id = "listarFormaCobranca", pattern = "/forma-de-cobranca/listar/", viewId = "/faces/tributario/formacobranca/lista.xhtml"),
        @URLMapping(id = "verFormaCobranca", pattern = "/forma-de-cobranca/ver/#{formaCobrancaControlador.id}/", viewId = "/faces/tributario/formacobranca/visualizar.xhtml")
})
public class FormaCobrancaControlador extends PrettyControlador<FormaCobranca> implements Serializable, CRUD {

    @EJB
    private FormaCobrancaFacade formaCobrancaFacade;
    private ConverterAutoComplete converterContaBancariaEntidade;

    public FormaCobrancaControlador() {
        super(FormaCobranca.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return formaCobrancaFacade;
    }

    public void validaEdicao() {
        if (formaCobrancaFacade.emUso(selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível alterar", "Parcelas existentes nesta Forma de Cobrança"));
        } else {
            redireciona();
        }
    }

    @Override
    public void excluir() {
        if (formaCobrancaFacade.emUso(selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível excluir", "Parcelas existentes nesta Forma de Cobrança"));
        } else {
            super.excluir();
        }

    }

    public ConverterAutoComplete getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, formaCobrancaFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    public List<ContaBancariaEntidade> completeContaBancariaEntidade(String parte) {
        return formaCobrancaFacade.getContaBancariaEntidadeFacade().listaFiltrando(parte.trim(), "numeroConta", "agencia");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/forma-de-cobranca/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaFormaCobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verFormaCobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarFormaCobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
