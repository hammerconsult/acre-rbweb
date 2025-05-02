/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoTributoTaxaDividasDiversas;
import br.com.webpublico.enums.TipoValorTributoTaxaDividasDiversas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TributoFacade;
import br.com.webpublico.negocios.TributoTaxasDividasDiversasFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author julio
 */
@ManagedBean(name = "tributoTaxasDividasDiversasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTributoTaxas", pattern = "/tributo-taxas-dividas-diversas/novo/", viewId = "/faces/tributario/taxasdividasdiversas/tributotaxasdividasdiversas/edita.xhtml"),
    @URLMapping(id = "editarTributoTaxas", pattern = "/tributo-taxas-dividas-diversas/editar/#{tributoTaxasDividasDiversasControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/tributotaxasdividasdiversas/edita.xhtml"),
    @URLMapping(id = "listarTributoTaxas", pattern = "/tributo-taxas-dividas-diversas/listar/", viewId = "/faces/tributario/taxasdividasdiversas/tributotaxasdividasdiversas/lista.xhtml"),
    @URLMapping(id = "verTributoTaxas", pattern = "/tributo-taxas-dividas-diversas/ver/#{tributoTaxasDividasDiversasControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/tributotaxasdividasdiversas/visualizar.xhtml")
})
public class TributoTaxasDividasDiversasControlador extends PrettyControlador<TributoTaxaDividasDiversas> implements Serializable, CRUD {

    @EJB
    private TributoTaxasDividasDiversasFacade facade;
    private List<TributoTaxaDividasDiversas> lista;
    private Operacoes operacao;
    @EJB
    private TributoFacade tributoFacade;
    private ConverterAutoComplete converterTributo;

    public TributoTaxasDividasDiversasControlador() {
        super(TributoTaxaDividasDiversas.class);
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public List<TributoTaxaDividasDiversas> getLista() {
        if (lista == null) {
            lista = facade.lista();
        }
        return lista;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public TributoTaxaDividasDiversas getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TributoTaxaDividasDiversas selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    @URLAction(mappingId = "novoTributoTaxas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarTributoTaxas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verTributoTaxas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributo-taxas-dividas-diversas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            if (!facade.existeTributoParaTaxa(selecionado)) {
                lista = null;
                super.salvar();
            } else {
                FacesUtil.addOperacaoNaoPermitida("O Tributo informado já foi cadastrado como Taxa Diversa.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<Tributo> completaTributo(String parte) {
        return tributoFacade.listaImpostoTaxa(parte.trim());
    }

    private void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTributo() == null || selecionado.getTributo().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tributo referente à Taxa Diversa.");
        }
        if (selecionado.getTipoValorTributo().equals(TipoValorTributoTaxaDividasDiversas.FIXO)) {
            if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe um valor maior que zero.");
            }
        } else {
            if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor não pode ser negativo.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> tiposValoresTributoTaxaDividasDiversas() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (TipoValorTributoTaxaDividasDiversas tipo : TipoValorTributoTaxaDividasDiversas.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> situacoesTributoTaxaDividasDiversas() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (SituacaoTributoTaxaDividasDiversas situacao : SituacaoTributoTaxaDividasDiversas.values()) {
            lista.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return lista;
    }
}
