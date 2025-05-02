/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoDesligamentoFGTS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoDesligamentoFGTSFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "motivoDesligamentoFGTSControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoDesligamentoFGTS", pattern = "/rh/motivo-desligamento-fgts/novo/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentofgts/edita.xhtml"),
        @URLMapping(id = "listaMotivoDesligamentoFGTS", pattern = "/rh/motivo-desligamento-fgts/listar/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentofgts/lista.xhtml"),
        @URLMapping(id = "verMotivoDesligamentoFGTS", pattern = "/rh/motivo-desligamento-fgts/ver/#{motivoDesligamentoFGTSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentofgts/visualizar.xhtml"),
        @URLMapping(id = "editarMotivoDesligamentoFGTS", pattern = "/rh/motivo-desligamento-fgts/editar/#{motivoDesligamentoFGTSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivodesligamentofgts/edita.xhtml"),
})
public class MotivoDesligamentoFGTSControlador extends PrettyControlador<MotivoDesligamentoFGTS> implements Serializable, CRUD {

    @EJB
    private MotivoDesligamentoFGTSFacade motivoDesligamentoFGTSFacade;

    public MotivoDesligamentoFGTSControlador() {
        super(MotivoDesligamentoFGTS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoDesligamentoFGTSFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/motivo-desligamento-fgts/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (motivoDesligamentoFGTSFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Motivo de desligamento do FGTS !");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoMotivoDesligamentoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMotivoDesligamentoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMotivoDesligamentoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
