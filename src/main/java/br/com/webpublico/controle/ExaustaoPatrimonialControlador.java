package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoReducaoValorBem;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 24/10/14
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "exaustaoPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExaustaoImovel", pattern = "/exaustao-imovel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/exaustao/imovel/edita.xhtml"),
    @URLMapping(id = "editarExaustaoImovel", pattern = "/exaustao-imovel/editar/#{exaustaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/exaustao/imovel/edita.xhtml"),
    @URLMapping(id = "verExaustaoImovel", pattern = "/exaustao-imovel/ver/#{exaustaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/exaustao/imovel/visualizar.xhtml"),
    @URLMapping(id = "listarExaustaoImovel", pattern = "/exaustao-imovel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/exaustao/imovel/lista.xhtml"),
})
public class ExaustaoPatrimonialControlador extends AbstractReducaoValorBemControlador {

    @Override
    public String getCaminhoPadrao() {
        return "/exaustao-imovel/";
    }

    @URLAction(mappingId = "novaExaustaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        super.novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        selecionado.setTipoReducao(TipoReducaoValorBem.EXAUSTAO);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/exaustao/imovel/edita.xhtml");
    }

    @URLAction(mappingId = "verExaustaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verImovel() {
        super.ver();
    }

    @URLAction(mappingId = "editarExaustaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarImovel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/exaustao/imovel/edita.xhtml");
    }
}
