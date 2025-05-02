package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoParcela;
import br.com.webpublico.enums.TipoPrazoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OpcaoPagamentoFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "opcaoPagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaOpcaoPagamento", pattern = "/opcao-de-pagamento/novo/", viewId = "/faces/tributario/opcaopagamento/edita.xhtml"),
    @URLMapping(id = "editarOpcaoPagamento", pattern = "/opcao-de-pagamento/editar/#{opcaoPagamentoControlador.id}/", viewId = "/faces/tributario/opcaopagamento/edita.xhtml"),
    @URLMapping(id = "listarOpcaoPagamento", pattern = "/opcao-de-pagamento/listar/", viewId = "/faces/tributario/opcaopagamento/lista.xhtml"),
    @URLMapping(id = "verOpcaoPagamento", pattern = "/opcao-de-pagamento/ver/#{opcaoPagamentoControlador.id}/", viewId = "/faces/tributario/opcaopagamento/visualizar.xhtml")
})
public class OpcaoPagamentoControlador extends PrettyControlador<OpcaoPagamento> implements Serializable, CRUD {

    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;
    private ParcelaFixa parcelaFixa;
    private ParcelaFixaPeriodica parcelaFixaPeriodica;
    private DescontoOpcaoPagamento descontoOpcaoPagamento;
    private ConverterGenerico converterTributo;

    public OpcaoPagamentoControlador() {
        super(OpcaoPagamento.class);
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterGenerico(Tributo.class, opcaoPagamentoFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public DescontoOpcaoPagamento getDescontoOpcaoPagamento() {
        return descontoOpcaoPagamento;
    }

    public void setDescontoOpcaoPagamento(DescontoOpcaoPagamento descontoOpcaoPagamento) {
        this.descontoOpcaoPagamento = descontoOpcaoPagamento;
    }

    public OpcaoPagamentoFacade getFacade() {
        return opcaoPagamentoFacade;
    }

    public ParcelaFixa getParcelaFixa() {
        return parcelaFixa;
    }

    public void setParcelaFixa(ParcelaFixa parcelaFixa) {
        this.parcelaFixa = parcelaFixa;
    }

    public ParcelaFixaPeriodica getParcelaFixaPeriodica() {
        return parcelaFixaPeriodica;
    }

    public void setParcelaFixaPeriodica(ParcelaFixaPeriodica parcelaFixaPeriodica) {
        this.parcelaFixaPeriodica = parcelaFixaPeriodica;
    }

    public AbstractFacade getFacede() {
        return opcaoPagamentoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/opcao-de-pagamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaOpcaoPagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setTipoParcela(TipoParcela.FIXA);
        parcelaFixa = new ParcelaFixa();
        parcelaFixaPeriodica = new ParcelaFixaPeriodica();
        descontoOpcaoPagamento = new DescontoOpcaoPagamento();
    }

    @URLAction(mappingId = "editarOpcaoPagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        parcelaFixaPeriodica = new ParcelaFixaPeriodica();
        parcelaFixa = new ParcelaFixa();
        descontoOpcaoPagamento = new DescontoOpcaoPagamento();
        if (selecionado.getTipoParcela().equals(TipoParcela.PERIODICA) && !selecionado.getParcelas().isEmpty()) {
            parcelaFixaPeriodica = (ParcelaFixaPeriodica) selecionado.getParcelas().get(0);
        }
    }

    @URLAction(mappingId = "verOpcaoPagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (isTipoParcelaPeriodica()) {
            limpaParcelas();
            adicionarParcelaFixaPeriodica();
        }
        try {
            validarDadosDaOpcaoDePagamento();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void adicionarParcelaFixa() {
        try {
            validarDadosDaParcelaFixa();
            parcelaFixa.setOpcaoPagamento(selecionado);
            selecionado.getParcelas().add(parcelaFixa);
            selecionado.getParcelas().sort(Comparator.comparing(o -> new Integer(o.getSequenciaParcela())));
            parcelaFixa = new ParcelaFixa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void adicionarParcelaFixaPeriodica() {
        try {
            validarDadosDaParcelaFixaPeriodica();
            ;
            parcelaFixaPeriodica.setPercentualValorTotal(new BigDecimal("100"));
            parcelaFixaPeriodica.setOpcaoPagamento(selecionado);
            selecionado.getParcelas().add(parcelaFixaPeriodica);
            parcelaFixaPeriodica = new ParcelaFixaPeriodica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    @Override
    public void excluir() {
        if (opcaoPagamentoFacade.emUso(selecionado)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "A opção de pagamento já está cadastrada e uma Dívida");
        } else {
            super.excluir();
        }
    }

    public void adicionarDesconto() {
        try {
            validarDadosDesconto(descontoOpcaoPagamento);
            descontoOpcaoPagamento.setOpcaoPagamento(selecionado);
            selecionado.getDescontos().add(descontoOpcaoPagamento);
            descontoOpcaoPagamento = new DescontoOpcaoPagamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void removerDesconto(DescontoOpcaoPagamento desconto) {
        selecionado.getDescontos().remove(desconto);
    }

    public void removerParcela(Parcela parcela) {
        selecionado.getParcelas().remove(parcela);
    }

    public void editarParcelaFixa(Parcela parcela) {
        this.parcelaFixa = (ParcelaFixa) parcela;
        this.selecionado.getParcelas().remove(parcela);
    }

    public String numeroParcela(Parcela parcela) {
        int index = selecionado.getParcelas().indexOf(parcela);
        return "" + (index + 1) + "/" + selecionado.getNumeroParcelas();
    }

    public List<SelectItem> getTributos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Tributo t : opcaoPagamentoFacade.getTributoFacade().lista()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getAcrescimos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ConfiguracaoAcrescimos c : opcaoPagamentoFacade.getConfiguracaoAcrescimosFacade().lista()) {
            toReturn.add(new SelectItem(c, c.getDescricao()));
        }
        return toReturn;
    }

    public BigDecimal getTotalPercentual() {
        BigDecimal total = new BigDecimal(0);
        for (Parcela p : selecionado.getParcelas()) {
            if (p.getPercentualValorTotal() != null) {
                total = total.add(p.getPercentualValorTotal());
            }
        }
        return total;
    }

    public boolean isCemPorcento() {
        return getTotalPercentual().compareTo(CEM) == 0;
    }

    private void validarDadosDesconto(DescontoOpcaoPagamento d) {
        ValidacaoException ve = new ValidacaoException();
        if (d.getTributo() == null || d.getTributo().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tributo deve ser informado.");
        }
        if (d.getPercentualDescontoAdimplente() == null || d.getPercentualDescontoAdimplente().doubleValue() < 0.0) {
            ve.adicionarMensagemDeCampoObrigatorio("O percentual de desconto para adimplentes deve ser igual ou maior que zero");
        }
        if (d.getPercentualDescontoInadimplente() == null || d.getPercentualDescontoInadimplente().doubleValue() < 0.0) {
            ve.adicionarMensagemDeCampoObrigatorio("O percentual de desconto para inadimplentes deve ser igual ou maior que zero");
        }
        for (DescontoOpcaoPagamento desc : selecionado.getDescontos()) {
            if (desc.getTributo().equals(d.getTributo())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O tributo informado já foi inserido.");
                break;
            }
        }
        ve.lancarException();
    }

    private void validarDadosDaOpcaoDePagamento() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (!ve.temMensagens()) {
            if (selecionado.getParcelas().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Opção de Pagamento deve conter ao menos uma parcela");
            }
            if (getTotalPercentual().doubleValue() != 100) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos percentuais das parcelas deve ser igual a 100%");
            }
        }
        ve.lancarException();
    }

    private void validarDadosDaParcelaFixa() {
        ValidacaoException ve = new ValidacaoException();
        if (parcelaFixa.getVencimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Vencimento deve ser informado.");
        }
        if (parcelaFixa.getSequenciaParcela() == null || parcelaFixa.getSequenciaParcela().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Sequência da Parcela deve ser informado.");
        }
        if (parcelaFixa.getPercentualValorTotal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser informado.");
        }
        if (parcelaFixa.getVencimento() != null || parcelaFixa != null) {
            if (existeParcelaComVencimento(parcelaFixa)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma parcela com este vencimento.");
            }
        }
        if (parcelaFixa.getVencimento() != null) {
            if (br.com.webpublico.util.DataUtil.ehDiaNaoUtil(parcelaFixa.getVencimento(), opcaoPagamentoFacade.getFeriadoFacade())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Vencimento deve ser um Dia útil.");
            }
        }
        if (parcelaFixa.getPercentualValorTotal() == null || parcelaFixa.getPercentualValorTotal().doubleValue() <= 0.0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O percentual deve ser maior que zero.");
        } else {
            BigDecimal totalPercentual = getTotalPercentual().add(parcelaFixa.getPercentualValorTotal());
            if (totalPercentual.compareTo(new BigDecimal(100)) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O percentual total ultrapassa 100%.");
            }
        }
        ve.lancarException();
    }

    private boolean existeParcelaComVencimento(Parcela parcela) {
        for (Parcela p : selecionado.getParcelas()) {
            if (p instanceof ParcelaFixa
                && (parcela.getId() == null || !parcela.getId().equals(p.getId()))
                && p.getVencimento().equals(parcela.getVencimento())) {
                return true;
            }
        }
        return false;
    }

    private void validarDadosDaParcelaFixaPeriodica() {
        ValidacaoException ve = new ValidacaoException();
        if (parcelaFixaPeriodica.getDiaVencimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dia do Vencimento deve ser informado.");
        } else if (parcelaFixaPeriodica.getDiaVencimento() > 31 || parcelaFixaPeriodica.getDiaVencimento() < 1) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Dia do Vencimento deve estar entre 1 e 31");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposParcelas() {
        List<SelectItem> tiposParcelas = new ArrayList<SelectItem>();
        for (TipoParcela t : TipoParcela.values()) {
            tiposParcelas.add(new SelectItem(t, t.getDescricao()));
        }
        return tiposParcelas;
    }

    public boolean isTipoParcelaFixa() {
        return selecionado.getTipoParcela() != null && selecionado.getTipoParcela().equals(TipoParcela.FIXA);
    }

    public boolean isTipoParcelaPeriodica() {
        return selecionado.getTipoParcela() != null && selecionado.getTipoParcela().equals(TipoParcela.PERIODICA);
    }

    public void limpaParcelas() {
        selecionado.getParcelas().clear();
    }
}
