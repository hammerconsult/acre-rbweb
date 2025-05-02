package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoAjusteBemMovel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by mga on 16/03/2018.
 */
@ManagedBean(name = "efetivacaoAjusteBemMovelAmortizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-efetivacao-ajuste-bem-movel-amortizacao", pattern = "/efetivacao-ajuste-bem-movel-amortizacao/novo/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-amortizacao/edita.xhtml"),
    @URLMapping(id = "edita-efetivacao-ajuste-bem-movel-amortizacao", pattern = "/efetivacao-ajuste-bem-movel-amortizacao/editar/#{efetivacaoAjusteBemMovelAmortizacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-amortizacao/edita.xhtml"),
    @URLMapping(id = "lista-efetivacao-ajuste-bem-movel-amortizacao", pattern = "/efetivacao-ajuste-bem-movel-amortizacao/listar/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-amortizacao/lista.xhtml"),
    @URLMapping(id = "ver-efetivacao-ajuste-bem-movel-amortizacao", pattern = "/efetivacao-ajuste-bem-movel-amortizacao/ver/#{efetivacaoAjusteBemMovelAmortizacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-amortizacao/visualizar.xhtml")
})
public class EfetivacaoAjusteBemMovelAmortizacaoControlador extends EfetivacaoAjusteBemMovelSuperControlador{

    @URLAction(mappingId = "novo-efetivacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoAjusteBemMovel(TipoAjusteBemMovel.AMORTIZACAO);
    }

    @URLAction(mappingId = "ver-efetivacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-efetivacao-ajuste-bem-movel-amortizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-ajuste-bem-movel-amortizacao/";
    }
}
