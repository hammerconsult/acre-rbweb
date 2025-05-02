/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.TipoDivDiversaTributoTaxa;
import br.com.webpublico.entidades.TipoDividaDiversa;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;
import br.com.webpublico.enums.SituacaoTipoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDividaDiversaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoDividaDiversas", pattern = "/tipo-de-dividas-diversas/novo/",
        viewId = "/faces/tributario/taxasdividasdiversas/tipodividasdiversas/edita.xhtml"),
    @URLMapping(id = "editarTipoDividaDiversas", pattern = "/tipo-de-dividas-diversas/editar/#{tipoDividaDiversaControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/tipodividasdiversas/edita.xhtml"),
    @URLMapping(id = "listarTipoDividaDiversas", pattern = "/tipo-de-dividas-diversas/listar/",
        viewId = "/faces/tributario/taxasdividasdiversas/tipodividasdiversas/lista.xhtml"),
    @URLMapping(id = "verTipoDividaDiversas", pattern = "/tipo-de-dividas-diversas/ver/#{tipoDividaDiversaControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/tipodividasdiversas/visualizar.xhtml")
})
public class TipoDividaDiversaControlador extends PrettyControlador<TipoDividaDiversa> implements Serializable, CRUD {

    @EJB
    private TipoDividaDiversaFacade tipoDividaDiversaFacade;
    private ConverterAutoComplete converterTributo;
    private TributoTaxaDividasDiversas tributo;

    public TipoDividaDiversaControlador() {
        super(TipoDividaDiversa.class);
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(TributoTaxaDividasDiversas.class, tipoDividaDiversaFacade.getTributoTaxasDividasDiversasFacade());
        }
        return converterTributo;
    }

    public List<TributoTaxaDividasDiversas> completaTributos(String filtro) {
        return tipoDividaDiversaFacade.getTributoTaxasDividasDiversasFacade().listaTributosAtivos(filtro.trim());
    }

    public TributoTaxaDividasDiversas getTributo() {
        return tributo;
    }

    public void setTributo(TributoTaxaDividasDiversas tributo) {
        this.tributo = tributo;
    }

    public boolean podeAdicionarTributo() {
        boolean retorno = true;
        if (tributo == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Selecione o Tributo.");
        } else if (itemJaAdicionado()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Tributo já adicionado.");
        }
        return retorno;
    }

    private boolean itemJaAdicionado() {
        if (selecionado.getTributosTaxas() != null) {
            for (TipoDivDiversaTributoTaxa tipoDividaTributoTaxa : selecionado.getTributosTaxas()) {
                if (tipoDividaTributoTaxa.getTributoTaxaDividasDiversas().equals(tributo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addTributo() {
        if (!podeAdicionarTributo()) {
            return;
        }
        if (selecionado.getTributosTaxas() == null) {
            selecionado.setTributosTaxas(new ArrayList<TipoDivDiversaTributoTaxa>());
        }
        TipoDivDiversaTributoTaxa tipoDivDiversaTributoTaxa = new TipoDivDiversaTributoTaxa();
        tipoDivDiversaTributoTaxa.setTipoDividaDiversa(selecionado);
        tipoDivDiversaTributoTaxa.setTributoTaxaDividasDiversas(tributo);
        selecionado.getTributosTaxas().add(tipoDivDiversaTributoTaxa);
        tributo = null;
    }

    public void removeItem(ActionEvent e) {
        TipoDivDiversaTributoTaxa tributo = (TipoDivDiversaTributoTaxa) e.getComponent().getAttributes().get("objeto");
        selecionado.getTributosTaxas().remove(tributo);
    }

    public boolean podeExcluir(TipoDividaDiversa tipoDividaDiversa) {
        boolean retorno = true;
        String relacionamentos = tipoDividaDiversaFacade.possuiRelacionamento(tipoDividaDiversa);
        if (!relacionamentos.trim().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não é possível excluir o registro!", "O mesmo possui relacionamentos com: " + relacionamentos.trim());
        }
        return retorno;
    }

    public Boolean validaCampos() {
        Boolean retorno = true;
        geraCodigoAutomatico(selecionado);
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Código.");
        }
        if (selecionado.getDescricao().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a Descrição.");
        }
        if (selecionado.getDescricaoCurta().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a Descrição Curta.");
        }
        if (selecionado.getSituacao() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a Situação.");
        }
        if (selecionado.getTributosTaxas() == null || selecionado.getTributosTaxas().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Nenhum Tributo foi adicionado.");
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-dividas-diversas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getSituacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (SituacaoTipoDividaDiversa sit : SituacaoTipoDividaDiversa.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }


    //---------------------------- editar ---------------------------------------------
    @URLAction(mappingId = "editarTipoDividaDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    //---------------------------- Novo ---------------------------------------------
    @URLAction(mappingId = "novoTipoDividaDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        tributo = null;
        geraCodigoAutomatico(selecionado);
    }

    //---------------------------- ver ---------------------------------------------
    @URLAction(mappingId = "verTipoDividaDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDividaDiversaFacade;
    }

    private void geraCodigoAutomatico(TipoDividaDiversa tdd) {
        BigDecimal recuperMaiorCodigo = tipoDividaDiversaFacade.recuperMaiorCodigo();
        if (recuperMaiorCodigo != null) {
            boolean verificaSeExiste = tipoDividaDiversaFacade.verificaSeExisteCodigo(recuperMaiorCodigo.intValue());
            if (verificaSeExiste == false) {
                tdd.setCodigo(recuperMaiorCodigo.intValue());
            }
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }

    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        tributo = (TributoTaxaDividasDiversas) e.getComponent().getAttributes().get("objeto");
    }

    public List<Divida> completaDividaCadastroImobiliario(String parte) {
        return tipoDividaDiversaFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.IMOBILIARIO);
    }

    public List<Divida> completaDividaCadastroEconomico(String parte) {
        return tipoDividaDiversaFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.ECONOMICO);
    }

    public List<Divida> completaDividaCadastroRural(String parte) {
        return tipoDividaDiversaFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.RURAL);
    }

    public List<Divida> completaDividaContribuinteGeral(String parte) {
        return tipoDividaDiversaFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.PESSOA);
    }

    public List<TipoDividaDiversa> completarTipoDividaDiversa(String parte) {
        return tipoDividaDiversaFacade.buscarTipoDividaDiversaAtivo(parte);
    }
}
