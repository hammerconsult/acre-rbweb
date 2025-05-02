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
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "amortizacaoPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAmortizacaoMovel", pattern = "/amortizacao-movel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/movel/edita.xhtml"),
    @URLMapping(id = "editarAmortizacaoMovel", pattern = "/amortizacao-movel/editar/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/movel/edita.xhtml"),
    @URLMapping(id = "verAmortizacaoMovel", pattern = "/amortizacao-movel/ver/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/movel/visualizar.xhtml"),
    @URLMapping(id = "listarAmortizacaoMovel", pattern = "/amortizacao-movel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/movel/lista.xhtml"),

    @URLMapping(id = "novaAmortizacaoImovel", pattern = "/amortizacao-imovel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/imovel/edita.xhtml"),
    @URLMapping(id = "editarAmortizacaoImovel", pattern = "/amortizacao-imovel/editar/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/imovel/edita.xhtml"),
    @URLMapping(id = "verAmortizacaoImovel", pattern = "/amortizacao-imovel/ver/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/imovel/visualizar.xhtml"),
    @URLMapping(id = "listarAmortizacaoImovel", pattern = "/amortizacao-imovel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/imovel/lista.xhtml"),

    @URLMapping(id = "novaAmortizacaoIntangivel", pattern = "/amortizacao-intangivel/novo/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/edita.xhtml"),
    @URLMapping(id = "editarAmortizacaoIntangivel", pattern = "/amortizacao-intangivel/editar/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/edita.xhtml"),
    @URLMapping(id = "verAmortizacaoIntangivel", pattern = "/amortizacao-intangivel/ver/#{amortizacaoPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/visualizar.xhtml"),
    @URLMapping(id = "listarAmortizacaoIntangivel", pattern = "/amortizacao-intangivel/listar/", viewId = "/faces/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/lista.xhtml")
})
public class AmortizacaoPatrimonialControlador extends AbstractReducaoValorBemControlador {

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS: {
                return "/amortizacao-movel/";
            }
            case IMOVEIS: {
                return "/amortizacao-imovel/";
            }
            case INTANGIVEIS: {
                return "/amortizacao-intangivel/";
            }
        }
        return "/amortizacao-movel/";
    }

    @URLAction(mappingId = "novaAmortizacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMovel() {
        super.novo();
        selecionado.setTipoBem(TipoBem.MOVEIS);
        selecionado.setTipoReducao(TipoReducaoValorBem.AMORTIZACAO);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/movel/edita.xhtml");
    }

    @URLAction(mappingId = "novaAmortizacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        super.novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        selecionado.setTipoReducao(TipoReducaoValorBem.AMORTIZACAO);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/imovel/edita.xhtml");
    }

    @URLAction(mappingId = "novaAmortizacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoIntangivel() {
        super.novo();
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
        selecionado.setTipoReducao(TipoReducaoValorBem.AMORTIZACAO);
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/edita.xhtml");
    }

    @URLAction(mappingId = "verAmortizacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verMovel() {
        super.ver();
    }

    @URLAction(mappingId = "verAmortizacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verImovel() {
        super.ver();
    }

    @URLAction(mappingId = "verAmortizacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verIntangivel() {
        super.ver();
    }

    @URLAction(mappingId = "editarAmortizacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarMovel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/movel/edita.xhtml");
    }

    @URLAction(mappingId = "editarAmortizacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarImovel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/imovel/edita.xhtml");
    }

    @URLAction(mappingId = "editarAmortizacaoIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarIntangivel() {
        super.editar();
        setXhtmlNovoEditar("/administrativo/patrimonio/reducaovalor/amortizacao/intangivel/edita.xhtml");
    }
}
