package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoAjusteBemMovel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by mga on 08/03/2018.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-efetivacao-ajuste-bem-movel-original", pattern = "/efetivacao-ajuste-bem-movel-original/novo/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-original/edita.xhtml"),
    @URLMapping(id = "edita-efetivacao-ajuste-bem-movel-original", pattern = "/efetivacao-ajuste-bem-movel-original/editar/#{efetivacaoAjusteBemMovelOriginalControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-original/edita.xhtml"),
    @URLMapping(id = "lista-efetivacao-ajuste-bem-movel-original", pattern = "/efetivacao-ajuste-bem-movel-original/listar/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-original/lista.xhtml"),
    @URLMapping(id = "ver-efetivacao-ajuste-bem-movel-original", pattern = "/efetivacao-ajuste-bem-movel-original/ver/#{efetivacaoAjusteBemMovelOriginalControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacao-ajuste-movel-original/visualizar.xhtml")
})
public class EfetivacaoAjusteBemMovelOriginalControlador extends EfetivacaoAjusteBemMovelSuperControlador {

    @URLAction(mappingId = "novo-efetivacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoAjusteBemMovel(TipoAjusteBemMovel.ORIGINAL);
    }

    @URLAction(mappingId = "ver-efetivacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-efetivacao-ajuste-bem-movel-original", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-ajuste-bem-movel-original/";
    }
}
