/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.controle;

import br.com.webpublico.entidades.Nacionalidade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.previdencia.NacionalidadeBBPrev;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NacionalidadeFacade;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author Usuario
 */

@ManagedBean(name = "nacionalidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-nacionalidade", pattern = "/nacionalidade/novo/", viewId = "/faces/tributario/cadastromunicipal/nacionalidade/edita.xhtml"),
    @URLMapping(id = "editar-nacionalidade", pattern = "/nacionalidade/editar/#{nacionalidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/nacionalidade/edita.xhtml"),
    @URLMapping(id = "ver-nacionalidade", pattern = "/nacionalidade/ver/#{nacionalidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/nacionalidade/visualizar.xhtml"),
    @URLMapping(id = "listar-nacionalidade", pattern = "/nacionalidade/listar/", viewId = "/faces/tributario/cadastromunicipal/nacionalidade/lista.xhtml")
})

public class NacionalidadeControlador extends PrettyControlador<Nacionalidade> implements Serializable, CRUD {

    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    @EJB
    private PaisFacade paisFacade;

    public NacionalidadeControlador(){
        super(Nacionalidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return nacionalidadeFacade;
    }

    public NacionalidadeFacade getFacade() {
        return nacionalidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nacionalidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-nacionalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        //System.out.println("Selecionado" + selecionado);
        selecionado = new Nacionalidade();
    }

    @URLAction(mappingId = "editar-nacionalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-nacionalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    nacionalidadeFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada ", "A Nacionalidade: " + selecionado.getDescricao() + " foi salva com sucesso"));
                } else {
                    nacionalidadeFacade.salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada ", "A Nacionalidade: " + selecionado.getDescricao() + " foi alterada com sucesso"));
                }
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
                return;
            }
        } else {
            return;
        }
    }

    public List<SelectItem> getPaises(){
        return Util.getListSelectItem(paisFacade.lista(), true);
    }

    public List<SelectItem> getNacionalidadesBBPrev() {
        return Util.getListSelectItem(NacionalidadeBBPrev.values());
    }
}
