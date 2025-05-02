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
 * Date: 23/10/14
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "depreciacaoPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaDepreciacaoMovel", pattern = "/depreciacao-movel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/movel/edita.xhtml"),
    @URLMapping(id = "novaDepreciacaoImovel", pattern = "/depreciacao-imovel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/imovel/edita.xhtml"),
    @URLMapping(id = "novaDepreciacaoIntangivel", pattern = "/depreciacao-intangivel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/edita.xhtml"),

    @URLMapping(id = "editarDepreciacaoMovel", pattern = "/depreciacao-movel/editar/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/movel/edita.xhtml"),
    @URLMapping(id = "editarDepreciacaoImovel", pattern = "/depreciacao-imovel/editar/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/imovel/edita.xhtml"),
    @URLMapping(id = "editarDepreciacaoIntangivel", pattern = "/depreciacao-intangivel/editar/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/edita.xhtml"),

    @URLMapping(id = "verDepreciacaoMovel", pattern = "/depreciacao-movel/ver/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/movel/visualizar.xhtml"),
    @URLMapping(id = "verDepreciacaoImovel", pattern = "/depreciacao-imovel/ver/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/imovel/visualizar.xhtml"),
    @URLMapping(id = "verDepreciacaoIntangivel", pattern = "/depreciacao-intangivel/ver/#{depreciacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/visualizar.xhtml"),

    @URLMapping(id = "listarDepreciacaoMovel", pattern = "/depreciacao-movel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/movel/lista.xhtml"),
    @URLMapping(id = "listarDepreciacaoImovel", pattern = "/depreciacao-imovel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/imovel/lista.xhtml"),
    @URLMapping(id = "listarDepreciacaoIntangivel", pattern = "/depreciacao-intangivel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/lista.xhtml"),
})
public class DepreciacaoPatrimonialControlador extends AbstractReducaoValorBemControlador {

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS: {
                return "/depreciacao-movel/";
            }
            case IMOVEIS: {
                return "/depreciacao-imovel/";
            }
            case INTANGIVEIS: {
                return "/depreciacao-intangivel/";
            }
        }
        return "/depreciacao-movel/";
    }

    @URLAction(mappingId = "novaDepreciacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMovel() {
        super.novo();
        selecionado.setTipoReducao(TipoReducaoValorBem.DEPRECIACAO);
        selecionado.setTipoBem(TipoBem.MOVEIS);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/movel/edita.xhtml");
    }

    @URLAction(mappingId = "novaDepreciacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        super.novo();
        selecionado.setCodigo(null);
        selecionado.setTipoReducao(TipoReducaoValorBem.DEPRECIACAO);
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/imovel/edita.xhtml");
    }

    @URLAction(mappingId = "novaDepreciacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoIntangivel() {
        super.novo();
        selecionado.setTipoReducao(TipoReducaoValorBem.DEPRECIACAO);
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/edita.xhtml");
    }

    @URLAction(mappingId = "verDepreciacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verMovel() {
        super.ver();
    }

    @URLAction(mappingId = "verDepreciacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verImovel() {
        super.ver();
    }

    @URLAction(mappingId = "verDepreciacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verIntangivel() {
        super.ver();
    }

    @URLAction(mappingId = "editarDepreciacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarMovel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/movel/edita.xhtml");
    }

    @URLAction(mappingId = "editarDepreciacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarImovel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/imovel/edita.xhtml");
    }

    @URLAction(mappingId = "editarDepreciacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarIntangivel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/depreciacao/intangivel/edita.xhtml");
    }

    @Override
    public boolean futureEstornoConcluida() {
        return super.futureEstornoConcluida();
    }
}
