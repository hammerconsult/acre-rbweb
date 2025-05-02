/*
 * Codigo gerado automaticamente em Thu Aug 04 11:21:10 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HorarioDeTrabalho;
import br.com.webpublico.enums.rh.esocial.TipoHorarioFlexivelESocial;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HorarioDeTrabalhoFacade;
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

@ManagedBean(name = "horarioDeTrabalhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novohorariodetrabalho", pattern = "/horariodetrabalho/novo/", viewId = "/faces/rh/administracaodepagamento/horariodetrabalho/edita.xhtml"),
    @URLMapping(id = "editarhorariodetrabalho", pattern = "/horariodetrabalho/editar/#{horarioDeTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/horariodetrabalho/edita.xhtml"),
    @URLMapping(id = "verhorariodetrabalho", pattern = "/horariodetrabalho/ver/#{horarioDeTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/horariodetrabalho/visualizar.xhtml"),
    @URLMapping(id = "listarhorariodetrabalho", pattern = "/horariodetrabalho/listar/", viewId = "/faces/rh/administracaodepagamento/horariodetrabalho/lista.xhtml")
})
public class HorarioDeTrabalhoControlador extends PrettyControlador<HorarioDeTrabalho> implements Serializable, CRUD {

    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;

    public HorarioDeTrabalhoControlador() {
        super(HorarioDeTrabalho.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return horarioDeTrabalhoFacade;

    }

    @URLAction(mappingId = "novohorariodetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verhorariodetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarhorariodetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public boolean validaCampos() {
        HorarioDeTrabalho horario = (HorarioDeTrabalho) selecionado;

        if (horario.getIntervalo() != null) {
            if (horario.getRetornoIntervalo() == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Para que o início do intervalo possa ser informado é necessário informar também o horário de retorno do intervalo !"));
                return false;
            }
        } else if (horario.getRetornoIntervalo() != null) {
            if (horario.getIntervalo() == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Para que o retorno do intervalo possa ser informado é necessário informar também o horário de início do intervalo !"));
                return false;
            }
        }

        return true;
    }

    public List<SelectItem> getTiposIntervalo() {
        return Util.getListSelectItem(TipoHorarioFlexivelESocial.values(), false);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/horariodetrabalho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
