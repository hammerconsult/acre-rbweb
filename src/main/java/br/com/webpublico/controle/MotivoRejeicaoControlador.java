package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoRejeicao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoRejeicaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "motivoRejeicaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoRejeicao", pattern = "/rh/motivo-de-rejeicao/novo/", viewId = "/faces/rh/administracaodepagamento/motivorejeicao/edita.xhtml"),
        @URLMapping(id = "listaMotivoRejeicao", pattern = "/rh/motivo-de-rejeicao/listar/", viewId = "/faces/rh/administracaodepagamento/motivorejeicao/lista.xhtml"),
        @URLMapping(id = "verMotivoRejeicao", pattern = "/rh/motivo-de-rejeicao/ver/#{motivoRejeicaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivorejeicao/visualizar.xhtml"),
        @URLMapping(id = "editarMotivoRejeicao", pattern = "/rh/motivo-de-rejeicao/editar/#{motivoRejeicaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivorejeicao/edita.xhtml"),
})
public class MotivoRejeicaoControlador extends PrettyControlador<MotivoRejeicao> implements Serializable, CRUD {

    @EJB
    private MotivoRejeicaoFacade motivoRejeicaoFacade;

    public MotivoRejeicaoControlador() {
        super(MotivoRejeicao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoRejeicaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/motivo-de-rejeicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (motivoRejeicaoFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Motivo de Rejeição !");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoMotivoRejeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMotivoRejeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMotivoRejeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
