/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoDesligamentoRAIS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoDesligamentoRAISFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "motivoDesligamentoRAISControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoDesligamentoRAIS", pattern = "/rh/motivo-desligamento-rais/novo/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentorais/edita.xhtml"),
        @URLMapping(id = "listaMotivoDesligamentoRAIS", pattern = "/rh/motivo-desligamento-rais/listar/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentorais/lista.xhtml"),
        @URLMapping(id = "verMotivoDesligamentoRAIS", pattern = "/rh/motivo-desligamento-rais/ver/#{motivoDesligamentoRAISControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentorais/visualizar.xhtml"),
        @URLMapping(id = "editarMotivoDesligamentoRAIS", pattern = "/rh/motivo-desligamento-rais/editar/#{motivoDesligamentoRAISControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentorais/edita.xhtml"),
})
public class MotivoDesligamentoRAISControlador extends PrettyControlador<MotivoDesligamentoRAIS> implements Serializable, CRUD {

    @EJB
    private MotivoDesligamentoRAISFacade motivoDesligamentoRAISFacade;

    public MotivoDesligamentoRAISControlador() {
        super(MotivoDesligamentoRAIS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoDesligamentoRAISFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/motivo-desligamento-rais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (motivoDesligamentoRAISFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Motivo de desligamento da RAIS !");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoMotivoDesligamentoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMotivoDesligamentoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMotivoDesligamentoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
}
