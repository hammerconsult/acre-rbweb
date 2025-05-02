/*
 * Codigo gerado automaticamente em Thu Oct 06 11:09:26 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.FaixaReferenciaFP;
import br.com.webpublico.entidades.ItemFaixaReferenciaFP;
import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.ValorReferenciaFP;
import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "referenciaFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReferenciaFP", pattern = "/referenciafp/novo/", viewId = "/faces/rh/administracaodepagamento/referenciafp/edita.xhtml"),
    @URLMapping(id = "editarReferenciaFP", pattern = "/referenciafp/editar/#{referenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/referenciafp/edita.xhtml"),
    @URLMapping(id = "listarReferenciaFP", pattern = "/referenciafp/listar/", viewId = "/faces/rh/administracaodepagamento/referenciafp/lista.xhtml"),
    @URLMapping(id = "verReferenciaFP", pattern = "/referenciafp/ver/#{referenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/referenciafp/visualizar.xhtml")
})
public class ReferenciaFPControlador extends PrettyControlador<ReferenciaFP> implements Serializable, CRUD {

    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private ValorReferenciaFPFacade valorReferenciaFPFacade;
    @EJB
    private FaixaReferenciaFPFacade faixaReferenciaFPFacade;
    @EJB
    private ItemFaixaReferenciaFPFacade itemFaixaReferenciaFPFacade;
    private FaixaReferenciaFP faixaReferenciaSelecionada;
    private ItemFaixaReferenciaFP itemFaixaReferenciaSelecionado;
    private ValorReferenciaFP valorReferenciaSelecionado;

    public FaixaReferenciaFP getFaixaReferenciaSelecionada() {
        return faixaReferenciaSelecionada;
    }

    public void setFaixaReferenciaSelecionada(FaixaReferenciaFP faixaReferenciaSelecionada) {
        this.faixaReferenciaSelecionada = faixaReferenciaSelecionada;
    }

    public ReferenciaFPControlador() {
        super(ReferenciaFP.class);
    }

    public ReferenciaFPFacade getFacade() {
        return referenciaFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return referenciaFPFacade;
    }

    public FaixaReferenciaFPFacade getFaixaReferenciaFPFacade() {
        return faixaReferenciaFPFacade;
    }

    public void setFaixaReferenciaFPFacade(FaixaReferenciaFPFacade faixaReferenciaFPFacade) {
        this.faixaReferenciaFPFacade = faixaReferenciaFPFacade;
    }

    public ItemFaixaReferenciaFP getItemFaixaReferenciaSelecionado() {
        return itemFaixaReferenciaSelecionado;
    }

    public void setItemFaixaReferenciaSelecionado(ItemFaixaReferenciaFP itemFaixaReferenciaSelecionado) {
        this.itemFaixaReferenciaSelecionado = itemFaixaReferenciaSelecionado;
    }

    public ValorReferenciaFP getValorReferenciaSelecionado() {
        return valorReferenciaSelecionado;
    }

    public void setValorReferenciaSelecionado(ValorReferenciaFP valorReferenciaSelecionado) {
        this.valorReferenciaSelecionado = valorReferenciaSelecionado;
    }

    public ItemFaixaReferenciaFPFacade getItemFaixaReferenciaFPFacade() {
        return itemFaixaReferenciaFPFacade;
    }

    public void setItemFaixaReferenciaFPFacade(ItemFaixaReferenciaFPFacade itemFaixaReferenciaFPFacade) {
        this.itemFaixaReferenciaFPFacade = itemFaixaReferenciaFPFacade;
    }

    public ReferenciaFPFacade getReferenciaFPFacade() {
        return referenciaFPFacade;
    }

    public void setReferenciaFPFacade(ReferenciaFPFacade referenciaFPFacade) {
        this.referenciaFPFacade = referenciaFPFacade;
    }

    public ValorReferenciaFPFacade getValorReferenciaFPFacade() {
        return valorReferenciaFPFacade;
    }

    public void setValorReferenciaFPFacade(ValorReferenciaFPFacade valorReferenciaFPFacade) {
        this.valorReferenciaFPFacade = valorReferenciaFPFacade;
    }

    public List<SelectItem> getTipoReferenciaFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoReferenciaFP object : TipoReferenciaFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/referenciafp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoReferenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ReferenciaFP rfp = ((ReferenciaFP) selecionado);
        rfp.setValoresReferenciasFPs(new ArrayList<ValorReferenciaFP>());

        rfp.setFaixasReferenciasFPs(new ArrayList<FaixaReferenciaFP>());

        faixaReferenciaSelecionada = null;
    }

    @URLAction(mappingId = "editarReferenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        faixaReferenciaSelecionada = null;
    }

    @URLAction(mappingId = "verReferenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarReferencia();
            if (selecionado.isTipoFaixa()) {
                selecionado.getValoresReferenciasFPs().clear();
            } else {
                selecionado.getFaixasReferenciasFPs().clear();
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public Boolean validaCodigoAlfaNumerico(String codigo) {
        return codigo.matches("[a-zA-Z0-9]*");
    }

    public void validarReferencia() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        String codigo = selecionado.getCodigo();
        if (!validaCodigoAlfaNumerico(codigo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser Alfanumérico(O Código deve possuir apenas letras e/ou números)!");
        }
        if (referenciaFPFacade.existeCodigo(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código <b>" + codigo + "</b> já está cadastrado em outra Referência FP!");
        }
        if (selecionado.getFaixasReferenciasFPs().isEmpty() && selecionado.getValoresReferenciasFPs().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("A referência fp deve ter pelo menos 1(uma) faixa ou 1(um) valor adiciocionados!");
        }
        for (FaixaReferenciaFP faixaReferenciaFP : selecionado.getFaixasReferenciasFPs()) {
            if (faixaReferenciaFP.getItensFaixaReferenciaFP().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Todas faixas de referência deve ter pelo menos 1(um) item adicionado!");
            }
        }
        ve.lancarException();
    }

    public void novaFaixaReferencia() {
        faixaReferenciaSelecionada = new FaixaReferenciaFP();
        faixaReferenciaSelecionada.setReferenciaFP(selecionado);
    }

    public void confirmarFaixaReferencia() {
        try {
            validarFaixaReferencia();
            if (DataUtil.isVigenciaValida(faixaReferenciaSelecionada, selecionado.getFaixasReferenciasFPs())) {
                selecionado.adicionarFaixaReferencia(faixaReferenciaSelecionada);
                setFaixaReferenciaSelecionada(null);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarFaixaReferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (faixaReferenciaSelecionada.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado!");
        }
        ve.lancarException();
    }

    public void selecionarFaixaReferencia(FaixaReferenciaFP faixaReferenciaFP) {
        faixaReferenciaSelecionada = faixaReferenciaFP;
    }

    public void removerFaixaReferencia(FaixaReferenciaFP faixaReferenciaFP) {
        selecionado.removerFaixaReferencia(faixaReferenciaFP);
    }

    public void novoItemFaixaReferencia(FaixaReferenciaFP faixaReferenciaFP) {
        itemFaixaReferenciaSelecionado = new ItemFaixaReferenciaFP();
        itemFaixaReferenciaSelecionado.setFaixaReferenciaFP(faixaReferenciaFP);
        faixaReferenciaSelecionada = faixaReferenciaFP;
    }

    public void confirmarItemFaixaReferencia() {
        faixaReferenciaSelecionada.adicionarItem(itemFaixaReferenciaSelecionado);
        setItemFaixaReferenciaSelecionado(null);
        setFaixaReferenciaSelecionada(null);
    }

    public void selecionarItemFaixaReferencia(ItemFaixaReferenciaFP itemFaixaReferenciaFP) {
        itemFaixaReferenciaSelecionado = itemFaixaReferenciaFP;
        faixaReferenciaSelecionada = itemFaixaReferenciaSelecionado.getFaixaReferenciaFP();
    }

    public void removerItemFaixaReferencia(ItemFaixaReferenciaFP itemFaixaReferenciaFP) {
        FaixaReferenciaFP faixaReferenciaFP = itemFaixaReferenciaFP.getFaixaReferenciaFP();
        faixaReferenciaFP.removerItem(itemFaixaReferenciaFP);
    }

    public void cancelarItemFaixaReferencia() {
        setItemFaixaReferenciaSelecionado(null);
        setFaixaReferenciaSelecionada(null);
    }

    public void novoValorReferencia() {
        valorReferenciaSelecionado = new ValorReferenciaFP();
        valorReferenciaSelecionado.setReferenciaFP(selecionado);
    }

    public void confirmarValorReferencia() {
        try {
            validarValorReferencia();
            if (DataUtil.isVigenciaValida(valorReferenciaSelecionado, selecionado.getValoresReferenciasFPs())) {
                selecionado.adicionarValorReferencia(valorReferenciaSelecionado);
                setValorReferenciaSelecionado(null);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarValorReferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (valorReferenciaSelecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado!");
        }
        ve.lancarException();
    }

    public void selecionarValorReferencia(ValorReferenciaFP valorReferenciaFP) {
        valorReferenciaSelecionado = valorReferenciaFP;
    }

    public void removerValorReferencia(ValorReferenciaFP valorReferenciaFP) {
        selecionado.removerValorReferencia(valorReferenciaFP);
    }
}
