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
 * Created by mga on 15/03/2018.
 */
@ManagedBean(name = "solicitacaoAjusteBemMovelDepreciacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-ajuste-bem-movel-depreciacao", pattern = "/solicitacao-ajuste-bem-movel-depreciacao/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-depreciacao/edita.xhtml"),
    @URLMapping(id = "edita-solicitacao-ajuste-bem-movel-depreciacao", pattern = "/solicitacao-ajuste-bem-movel-depreciacao/editar/#{solicitacaoAjusteBemMovelDepreciacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-depreciacao/edita.xhtml"),
    @URLMapping(id = "ver-solicitacao-ajuste-bem-movel-depreciacao", pattern = "/solicitacao-ajuste-bem-movel-depreciacao/ver/#{solicitacaoAjusteBemMovelDepreciacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-depreciacao/visualizar.xhtml"),
    @URLMapping(id = "lista-solicitacao-ajuste-bem-movel-depreciacao", pattern = "/solicitacao-ajuste-bem-movel-depreciacao/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-depreciacao/lista.xhtml")
})
public class SolicitacaoAjusteBemMovelDepreciacaoControlador extends SolicitacaoAjusteBemMovelSuperControlador {

    @URLAction(mappingId = "novo-solicitacao-ajuste-bem-movel-depreciacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoAjusteBemMovel(TipoAjusteBemMovel.DEPRECIACAO);
    }

    @URLAction(mappingId = "ver-solicitacao-ajuste-bem-movel-depreciacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-solicitacao-ajuste-bem-movel-depreciacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-ajuste-bem-movel-depreciacao/";
    }

    public void redirecionarParaVer(SolicitacaoAjusteBemMovel entity) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + entity.getId() + "/");
    }
}
