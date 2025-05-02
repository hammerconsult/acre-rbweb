/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItensBordero;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.BorderoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Edi
 */
@ManagedBean(name = "borderoRemoveItemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-item-bordero", pattern = "/estornar-ordem-bancaria/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/removeItem.xhtml"),
})
public class BorderoRemoveItemControlador implements Serializable {

    @EJB
    private BorderoFacade borderoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterOrdemBancaria;
    private ItensBordero itensBordero;

    public BorderoRemoveItemControlador() {
    }

    public BorderoFacade getFacade() {
        return borderoFacade;
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/ordem-bancaria/listar/");
    }


    @URLAction(mappingId = "novo-item-bordero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        itensBordero = new ItensBordero();
    }

    public boolean validarOrdemBancaria() {
        Boolean controle = Boolean.TRUE;
        if (itensBordero.getBordero() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Ordem Bancária deve ser informado.");
            controle = Boolean.FALSE;
        }
        if (itensBordero.getBordero() != null) {
            if (itensBordero.getArrayPgto().length <= 0
                && itensBordero.getArrayLiberacaoCotaFinanceira().length <= 0
                && itensBordero.getArrayPgtoExtra().length <= 0
                && itensBordero.getArrayTransferenciaFinanceira().length <= 0
                && itensBordero.getArrayTransferenciaMesmaUnidade().length <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Selecione ao menos um movimento da ordem bancária para continuar a operação.");
                controle = Boolean.FALSE;
            }
        }
        return controle;
    }

    public void salvar() {
        if (validarOrdemBancaria()) {
            try {
                borderoFacade.salvarRemovendoItem(itensBordero);
                FacesUtil.addOperacaoRealizada(" A Ordem Bancária: " + itensBordero.getBordero().toStringAutoComplete() + " teve movimentos removidos com sucesso.");
                FacesUtil.redirecionamentoInterno("/ordem-bancaria/ver/" + itensBordero.getBordero().getId() + "/");
            } catch (Exception ex) {
                throw new ExcecaoNegocioGenerica("Operação não Realizada! " + ex.getMessage());
            }
        }
    }


    public void adicionarDespesaExtra() {
        itensBordero.setListaPagamentoExtra(new ArrayList<BorderoPagamentoExtra>());
        for (BorderoPagamentoExtra pag : itensBordero.getBordero().getListaPagamentosExtra()) {
            if (pag.getSituacaoItemBordero().equals(SituacaoItemBordero.BORDERO) || pag.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO) || pag.getSituacaoItemBordero().equals(SituacaoItemBordero.DEFERIDO)) {
                itensBordero.getListaPagamentoExtra().add(pag);
            }
        }
    }

    public void adicionarPagamento() {
        Bordero b = ((Bordero) itensBordero.getBordero());
        itensBordero.setListaPagamentos(new ArrayList<BorderoPagamento>());
        for (BorderoPagamento pag : itensBordero.getBordero().getListaPagamentos()) {
            if (pag.getSituacaoItemBordero().equals(SituacaoItemBordero.BORDERO) || pag.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO) || pag.getSituacaoItemBordero().equals(SituacaoItemBordero.DEFERIDO)) {
                itensBordero.getListaPagamentos().add(pag);
            }
        }
    }

    public void adicionarLiberacaoFianceira() {
        itensBordero.setListaLiberacoes(new ArrayList<BorderoLiberacaoFinanceira>());
        for (BorderoLiberacaoFinanceira lib : itensBordero.getBordero().getListaLiberacaoCotaFinanceira()) {
            if (lib.getSituacaoItemBordero().equals(SituacaoItemBordero.BORDERO) || lib.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO) || lib.getSituacaoItemBordero().equals(SituacaoItemBordero.DEFERIDO)) {
                itensBordero.getListaLiberacoes().add(lib);
            }
        }
    }

    public void adicionarTransferenciaFinanceira() {
        itensBordero.setListaTransferencias(new ArrayList<BorderoTransferenciaFinanceira>());
        for (BorderoTransferenciaFinanceira transf : itensBordero.getBordero().getListaTransferenciaFinanceira()) {
            if (transf.getSituacaoItemBordero().equals(SituacaoItemBordero.BORDERO) || transf.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO) || transf.getSituacaoItemBordero().equals(SituacaoItemBordero.DEFERIDO)) {
                itensBordero.getListaTransferencias().add(transf);
            }
        }
    }

    public void adicionarTransferenciaFinanceiraMesmaUnidade() {
        itensBordero.setListaTransferenciaMesmaUnidades(new ArrayList<BorderoTransferenciaMesmaUnidade>());
        for (BorderoTransferenciaMesmaUnidade transf : itensBordero.getBordero().getListaTransferenciaMesmaUnidade()) {
            if (transf.getSituacaoItemBordero().equals(SituacaoItemBordero.BORDERO) || transf.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO) || transf.getSituacaoItemBordero().equals(SituacaoItemBordero.DEFERIDO)) {
                itensBordero.getListaTransferenciaMesmaUnidades().add(transf);
            }
        }
    }

    public void adicionarMovimentos() {
        adicionarDespesaExtra();
        adicionarPagamento();
        adicionarTransferenciaFinanceira();
        adicionarTransferenciaFinanceiraMesmaUnidade();
        adicionarLiberacaoFianceira();
    }

    public List<Bordero> completarOrdemBancaria(String parte) {
        return borderoFacade.buscarOrdemBancariaDeferidaOrIndeferida(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterOrdemBancaria() {
        if (converterOrdemBancaria == null) {
            converterOrdemBancaria = new ConverterAutoComplete(Bordero.class, borderoFacade);
        }
        return converterOrdemBancaria;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public BigDecimal getValorTotalTransferenciaMesmaUnidade() {
        BigDecimal soma = BigDecimal.ZERO;
        if (itensBordero.getBordero().getListaTransferenciaMesmaUnidade() != null
            && itensBordero.getBordero().getListaTransferenciaMesmaUnidade().isEmpty()) {
            for (BorderoTransferenciaMesmaUnidade tra : itensBordero.getBordero().getListaTransferenciaMesmaUnidade()) {
                soma = soma.add(tra.getTransferenciaMesmaUnidade().getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalLiberacaFinanceira() {
        BigDecimal soma = BigDecimal.ZERO;
        if (itensBordero.getBordero().getListaLiberacaoCotaFinanceira() != null
            && !itensBordero.getBordero().getListaLiberacaoCotaFinanceira().isEmpty()) {
            for (BorderoLiberacaoFinanceira lcf : itensBordero.getBordero().getListaLiberacaoCotaFinanceira()) {
                soma = soma.add(lcf.getLiberacaoCotaFinanceira().getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalTransferenciaFinanceira() {
        BigDecimal soma = BigDecimal.ZERO;
        if (itensBordero.getBordero().getListaTransferenciaFinanceira() != null
            && !itensBordero.getBordero().getListaTransferenciaFinanceira().isEmpty()) {
            for (BorderoTransferenciaFinanceira tra : itensBordero.getBordero().getListaTransferenciaFinanceira()) {
                soma = soma.add(tra.getTransferenciaContaFinanceira().getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalPagamento() {
        BigDecimal soma = BigDecimal.ZERO;
        Bordero b = ((Bordero) itensBordero.getBordero());
        if (b.getListaPagamentos() != null
            && !b.getListaPagamentos().isEmpty()) {
            for (BorderoPagamento pag : b.getListaPagamentos()) {
                soma = soma.add(pag.getPagamento().getValorFinal());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalDespesaExtra() {
        BigDecimal soma = BigDecimal.ZERO;
        Bordero b = ((Bordero) itensBordero.getBordero());
        if (b.getListaPagamentosExtra() != null
            && !b.getListaPagamentosExtra().isEmpty()) {
            for (BorderoPagamentoExtra pagExtra : b.getListaPagamentosExtra()) {
                soma = soma.add(pagExtra.getPagamentoExtra().getValor());
            }
        }
        return soma;
    }

    public void selecionarBordero(ActionEvent event) {
        Bordero b = ((Bordero) itensBordero.getBordero());
        itensBordero.setBordero(b);
        itensBordero.setBordero((Bordero) event.getComponent().getAttributes().get("objeto"));
        adicionarMovimentos();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ItensBordero getItensBordero() {
        return itensBordero;
    }

    public void setItensBordero(ItensBordero itensBordero) {
        this.itensBordero = itensBordero;
    }
}
