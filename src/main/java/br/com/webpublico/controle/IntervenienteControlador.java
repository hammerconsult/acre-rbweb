/*
 * Codigo gerado automaticamente em Thu Apr 05 14:41:35 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Interveniente;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.TipoInterveniente;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IntervenienteFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "intervenienteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novointerveniente", pattern = "/interveniente/novo/", viewId = "/faces/financeiro/convenios/interveniente/edita.xhtml"),
        @URLMapping(id = "editainterveniente", pattern = "/interveniente/editar/#{intervenienteControlador.id}/", viewId = "/faces/financeiro/convenios/interveniente/edita.xhtml"),
        @URLMapping(id = "verinterveniente", pattern = "/interveniente/ver/#{intervenienteControlador.id}/", viewId = "/faces/financeiro/convenios/interveniente/visualizar.xhtml"),
        @URLMapping(id = "listarinterveniente", pattern = "/interveniente/listar/", viewId = "/faces/financeiro/convenios/interveniente/lista.xhtml")
})
public class IntervenienteControlador extends PrettyControlador<Interveniente> implements Serializable, CRUD {

    @EJB
    private IntervenienteFacade intervenienteFacade;
    private ConverterAutoComplete converterTipoInterveniente;
    private ConverterAutoComplete converterPessoa;
    private ClasseCredor classeCredor;
    private ConverterAutoComplete converterClasseCredor;

    public IntervenienteControlador() {
        super(Interveniente.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return intervenienteFacade;
    }

    @URLAction(mappingId = "novointerveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verinterveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editainterveniente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public IntervenienteFacade getFacade() {
        return intervenienteFacade;
    }

    @Override
    public Interveniente getSelecionado() {
        try {
            return super.getSelecionado();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            logger.error(e.getMessage());
            return null;
        }
    }

    public void setaClasseCredor(SelectEvent evt) {
        if (classeCredor != null && classeCredor.getId() != null) {
            ((Interveniente) selecionado).setClasseCredor(this.classeCredor);
        }
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, intervenienteFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return intervenienteFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), ((Interveniente) selecionado).getPessoa());
    }

    public List<TipoInterveniente> completaTipoInterveniente(String parte) {
        return intervenienteFacade.getTipoIntervenienteFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterTipoInterveniente() {
        if (converterTipoInterveniente == null) {
            converterTipoInterveniente = new ConverterAutoComplete(TipoInterveniente.class, intervenienteFacade.getTipoIntervenienteFacade());
        }
        return converterTipoInterveniente;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return intervenienteFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, intervenienteFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public void setaPessoa(SelectEvent evt) {
        Interveniente inter = ((Interveniente) selecionado);
        Pessoa p = (Pessoa) evt.getObject();
        ((Interveniente) selecionado).setPessoa(p);
        ((Interveniente) selecionado).setClasseCredor(null);
        //inter.setPessoa(p);
        //inter.setClasseCredor(null);
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/interveniente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
