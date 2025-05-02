/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pais;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PaisFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * @author terminal4
 */
@ManagedBean(name = "paisControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-pais",   pattern = "/pais/novo/", viewId = "/faces/tributario/cadastromunicipal/pais/edita.xhtml"),
        @URLMapping(id = "editar-pais", pattern = "/pais/editar/#{paisControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pais/edita.xhtml"),
        @URLMapping(id = "ver-pais",    pattern = "/pais/ver/#{paisControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pais/visualizar.xhtml"),
        @URLMapping(id = "listar-pais", pattern = "/pais/listar/", viewId = "/faces/tributario/cadastromunicipal/pais/lista.xhtml")
})
public class PaisControlador extends PrettyControlador<Pais> implements Serializable, CRUD {

    @EJB
    private PaisFacade paisFacade;

    public PaisControlador() {
        super(Pais.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return paisFacade;
    }

    public PaisFacade getFacade() {
        return paisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-pais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new Pais();
    }


    @URLAction(mappingId = "editar-pais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-pais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    paisFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Operação Realizada : ", " O País: " + selecionado.getCodigo() + " " + selecionado.getNome() + " foi salvo com sucesso "));
                } else {
                    paisFacade.salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Operação Realizada : ", " O País: " + selecionado.getCodigo() + " " + selecionado.getNome() + " foi alterado com sucesso "));
                }
                redireciona();
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Exceção do sistema ", ex.getMessage()));
            }
        } else {
            return;

        }

    }
}
