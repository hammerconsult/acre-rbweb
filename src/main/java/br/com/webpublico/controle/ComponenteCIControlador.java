/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Penhora;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean
@SessionScoped
public class ComponenteCIControlador implements Serializable {

    private CadastroImobiliario cadastroImobiliario;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    private String update;
    private Penhora penhora;

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public void novo(CadastroImobiliario ci, String update) {
        this.cadastroImobiliario = ci == null || ci.getId() == null ? new CadastroImobiliario() : ci;
        penhora = cadastroImobiliarioFacade.retornaPenhoraDoImovel(cadastroImobiliario);
        this.update = update == null || update.isEmpty() || "".equals(update) ? "Formulario" : update;
    }

    public Penhora getPenhora() {
        return penhora;
    }

    public void setPenhora(Penhora penhora) {
        this.penhora = penhora;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public boolean isImovelBloqueado() {
        return cadastroImobiliarioFacade.imovelComPenhoraComBloqueio(cadastroImobiliario);
    }

    public boolean isImovelPenhorado() {
        return cadastroImobiliarioFacade.imovelComPenhora(cadastroImobiliario);
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public void setaCI(SelectEvent e) {
        cadastroImobiliario = (CadastroImobiliario) e.getObject();
        if (isImovelPenhorado()) {
            RequestContext.getCurrentInstance().execute("existePenhora.show()");
            penhora = cadastroImobiliarioFacade.retornaPenhoraDoImovel(cadastroImobiliario);
        }
        //RequestContext.getCurrentInstance().update(update + " btComponenteCI");
    }
}
