package br.com.webpublico.controle;

import br.com.webpublico.entidades.SolicitacaoAjusteBemMovel;
import br.com.webpublico.enums.TipoAjusteBemMovel;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by mga on 06/03/2018.
 */

@ManagedBean(name = "solicitacaoAjusteBemMovelOriginalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-ajuste-bem-movel-original", pattern = "/solicitacao-ajuste-bem-movel-original/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-original/edita.xhtml"),
    @URLMapping(id = "edita-solicitacao-ajuste-bem-movel-original", pattern = "/solicitacao-ajuste-bem-movel-original/editar/#{solicitacaoAjusteBemMovelOriginalControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-original/edita.xhtml"),
    @URLMapping(id = "ver-solicitacao-ajuste-bem-movel-original", pattern = "/solicitacao-ajuste-bem-movel-original/ver/#{solicitacaoAjusteBemMovelOriginalControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-original/visualizar.xhtml"),
    @URLMapping(id = "lista-solicitacao-ajuste-bem-movel-original", pattern = "/solicitacao-ajuste-bem-movel-original/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-original/lista.xhtml")
})

public class SolicitacaoAjusteBemMovelOriginalControlador extends SolicitacaoAjusteBemMovelSuperControlador {

    @URLAction(mappingId = "novo-solicitacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoAjusteBemMovel(TipoAjusteBemMovel.ORIGINAL);
    }

    @URLAction(mappingId = "ver-solicitacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-solicitacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-ajuste-bem-movel-original/";
    }

    public void redirecionarParaVer(SolicitacaoAjusteBemMovel entity) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + entity.getId() + "/");
    }
}
