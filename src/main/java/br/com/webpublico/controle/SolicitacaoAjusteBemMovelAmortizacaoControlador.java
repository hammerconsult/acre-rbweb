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

@ManagedBean(name = "solicitacaoAjusteBemMovelAmortizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-ajuste-bem-movel-amortizacao", pattern = "/solicitacao-ajuste-bem-movel-amortizacao/novo/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-amortizacao/edita.xhtml"),
    @URLMapping(id = "edita-solicitacao-ajuste-bem-movel-amortizacao", pattern = "/solicitacao-ajuste-bem-movel-amortizacao/editar/#{solicitacaoAjusteBemMovelAmortizacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-amortizacao/edita.xhtml"),
    @URLMapping(id = "ver-solicitacao-ajuste-bem-movel-amortizacao", pattern = "/solicitacao-ajuste-bem-movel-amortizacao/ver/#{solicitacaoAjusteBemMovelAmortizacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-amortizacao/visualizar.xhtml"),
    @URLMapping(id = "lista-solicitacao-ajuste-bem-movel-amortizacao", pattern = "/solicitacao-ajuste-bem-movel-amortizacao/listar/", viewId = "/faces/administrativo/patrimonio/solicitacao-ajuste-movel-amortizacao/lista.xhtml")
})
public class SolicitacaoAjusteBemMovelAmortizacaoControlador extends SolicitacaoAjusteBemMovelSuperControlador {

    @URLAction(mappingId = "novo-solicitacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoAjusteBemMovel(TipoAjusteBemMovel.AMORTIZACAO);
    }

    @URLAction(mappingId = "ver-solicitacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-solicitacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-ajuste-bem-movel-amortizacao/";
    }

    public void redirecionarParaVer(SolicitacaoAjusteBemMovel entity) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + entity.getId() + "/");
    }
}
