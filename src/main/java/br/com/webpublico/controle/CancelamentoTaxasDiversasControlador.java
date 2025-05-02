/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalculoTaxasDiversasFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "cancelamentoTaxasDiversasControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarCancelamentoTaxasDiversas", pattern = "/cancelamento-taxas-diversas/listar/", viewId = "/faces/tributario/taxasdividasdiversas/cancelamentotaxasdiversas/lista.xhtml"),
        @URLMapping(id = "verCancelamentoTaxasDiversas", pattern = "/cancelamento-taxas-diversas/ver/#{cancelamentoTaxasDiversasControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/cancelamentotaxasdiversas/visualizar.xhtml")
})
public class CancelamentoTaxasDiversasControlador extends PrettyControlador<CalculoTaxasDiversas> implements Serializable, CRUD {

    @EJB
    private CalculoTaxasDiversasFacade calculoTaxasDiversasFacade;
    private CancelamentoTaxasDiversas cancelamentoTaxasDiversas;
    private boolean selecionouSim;
    private List<CalculoTaxasDiversas> lista;
    private PesquisaTaxas pesquisaTaxas;
    List<ValorDivida> listaValorDivida;

    public CancelamentoTaxasDiversasControlador() {
        super(CalculoTaxasDiversas.class);
        limpaConsulta();
    }

    @Override
    @URLAction(mappingId = "verCancelamentoTaxasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionouSim = false;
        listaValorDivida = calculoTaxasDiversasFacade.getValorDividaPorCalculo(selecionado);
    }

    @URLAction(mappingId = "listarCancelamentoTaxasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listaCancelamento() {
        super.novo();
        limpaConsulta();
    }


    public List<ValorDivida> getListaValorDivida() {
        return listaValorDivida;
    }

    public boolean isSelecionouSim() {
        return selecionouSim;
    }

    public void setSelecionouSim(boolean selecionouSim) {
        this.selecionouSim = selecionouSim;
    }

    public CancelamentoTaxasDiversas getCancelamentoTaxasDiversas() {
        return cancelamentoTaxasDiversas;
    }

    public void setCancelamentoTaxasDiversas(CancelamentoTaxasDiversas cancelamentoTaxasDiversas) {
        this.cancelamentoTaxasDiversas = cancelamentoTaxasDiversas;
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoTaxasDiversasFacade;
    }

    public void filtrarEmitido() {
        pesquisaTaxas.setSituacaoCalculo(SituacaoCalculo.EMITIDO);
        lista = calculoTaxasDiversasFacade.listaPorFiltro(pesquisaTaxas);
    }

    private boolean validaCamposPreenchidos() {
        if (cancelamentoTaxasDiversas.getMotivo() == null || cancelamentoTaxasDiversas.getMotivo().trim().length() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar cancelar a Taxa Diversa!", "O campo motivo é obrigatório."));
            return false;
        }
        return true;
    }

    public void cancelarCertidoes() {
        if (validaCamposPreenchidos()) {
            selecionado.setSituacao(SituacaoCalculo.CANCELADO);
            selecionado.setCancelamentoTaxasDiversas(cancelamentoTaxasDiversas);
            calculoTaxasDiversasFacade.salva(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso! ", "Taxa Diversa cancelada."));
        }
    }

    public void limpaConsulta() {
        novaPesquisa();
        lista = new ArrayList<>();
        listaValorDivida = new ArrayList<>();
    }

    public void novaPesquisa() {
        pesquisaTaxas = new PesquisaTaxas();
    }

    public String montaDescricaoCadastro(CalculoTaxasDiversas calculoTaxasDiversas) {
        String retorno = "";
        if (calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)
                || calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)
                || calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            retorno = calculoTaxasDiversasFacade.getCadastroFacade().montaDescricaoCadastro(calculoTaxasDiversas.getTipoCadastroTributario(), calculoTaxasDiversas.getCadastro());
        } else if (calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            retorno = calculoTaxasDiversas.getContribuinte().getNomeCpfCnpj();
        }
        return retorno;
    }

    public List<SelectItem> tiposCadastrosTributarios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "TODOS"));
        for (TipoCadastroTributario tpcadtrib : TipoCadastroTributario.values()) {
            if (isTipoCadastroPermitido(tpcadtrib)) {
                lista.add(new SelectItem(tpcadtrib, tpcadtrib.getDescricao()));
            }
        }
        return lista;
    }

    public boolean isTipoCadastroPermitido(TipoCadastroTributario tipoCadastroTributario) {
        return true;
    }

    public void preparaCancelamento() {
        this.cancelamentoTaxasDiversas = new CancelamentoTaxasDiversas();
        this.cancelamentoTaxasDiversas.setDataCancelamento(new Date());
        this.cancelamentoTaxasDiversas.setUsuarioSistema(calculoTaxasDiversasFacade.getSistemaFacade().getUsuarioCorrente());
        selecionouSim = true;
    }

    public void cancelarTaxaDiversa() {
        if (validaCamposCancelamento()) {
            try {
                selecionado.setCancelamentoTaxasDiversas(cancelamentoTaxasDiversas);
                selecionado.setSituacao(SituacaoCalculo.CANCELADO);
                cancelaParcelaDaListaDeValorDivida();
                calculoTaxasDiversasFacade.salvar(selecionado);
                FacesUtil.addInfo("Sucesso! ", "Taxa Diversa cancelada.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError("Erro ao tentar cancelar a Taxa Diversa: ", e.getMessage());
            }
        }
    }

    private boolean validaCamposCancelamento() {
        boolean retorno = true;
        if (cancelamentoTaxasDiversas.getMotivo() == null || cancelamentoTaxasDiversas.getMotivo().trim().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o motivo do cancelamento.");
        } else if (cancelamentoTaxasDiversas.getMotivo().length() > 3000) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Descrição do motivo muito longa.");
        }
        return retorno;
    }

    private void cancelaParcelaDaListaDeValorDivida() {
        for (ValorDivida vd : listaValorDivida) {
            calculoTaxasDiversasFacade.cancelaParcelasValorDivida(vd);
        }
    }

    public void verificaPermissaoCancelamento() {
        boolean permitido = false;
        for (ValorDivida vd : listaValorDivida) {
            permitido = calculoTaxasDiversasFacade.permiteCancelamento(vd);
        }
        if (permitido) {
            FacesUtil.executaJavaScript("widgetDialogCancelamento.show()");
            RequestContext.getCurrentInstance().execute("");
        } else {
            FacesUtil.addError("Não foi possível continuar!", "A situação da(s) parcela(s) desta Taxa Diversa não permite cancelamento.");
        }
    }

    public BigDecimal getTotalReais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoTaxasDiversas itemRec : selecionado.getItens()) {
            total = total.add(itemRec.getValorReal());
        }
        return total;
    }

    public BigDecimal getTotalGeralReais() {
        BigDecimal total = BigDecimal.ZERO;
        Integer quantidade = 0;
        for (ItemCalculoTaxasDiversas itemRec : selecionado.getItens()) {
            quantidade = itemRec.getQuantidadeTributoTaxas();
            total = total.add(itemRec.getValorReal().multiply(new BigDecimal(quantidade)));
        }
        return total;
    }

    public BigDecimal getTotalUFM() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoTaxasDiversas itemRec : selecionado.getItens()) {
            total = total.add(itemRec.getValorUFM());
        }
        return total;
    }

    public List<CalculoTaxasDiversas> getLista() {
        return lista;
    }

    public void setLista(List<CalculoTaxasDiversas> lista) {
        this.lista = lista;
    }

    public PesquisaTaxas getPesquisaTaxas() {
        return pesquisaTaxas;
    }

    public void setPesquisaTaxas(PesquisaTaxas pesquisaTaxas) {
        this.pesquisaTaxas = pesquisaTaxas;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cancelamento-taxas-diversas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public class PesquisaTaxas {

        private Date dataLancamento;
        private Date dataVencimento;
        private String cadastro;
        private TipoCadastroTributario tipoCadastroTributario;
        private SituacaoCalculo situacaoCalculo;
        private Integer exercicio;
        private Long numero;

        public PesquisaTaxas() {
            dataLancamento = null;
            dataVencimento = null;
            exercicio = null;
            numero = null;
            situacaoCalculo = null;
            tipoCadastroTributario = null;
            cadastro = null;
        }

        public Integer getExercicio() {
            return exercicio;
        }

        public void setExercicio(Integer exercicio) {
            this.exercicio = exercicio;
        }

        public Long getNumero() {
            return numero;
        }

        public void setNumero(Long numero) {
            this.numero = numero;
        }

        public Date getDataLancamento() {
            return dataLancamento;
        }

        public void setDataLancamento(Date dataLancamento) {
            this.dataLancamento = dataLancamento;
        }

        public Date getDataVencimento() {
            return dataVencimento;
        }

        public void setDataVencimento(Date dataVencimento) {
            this.dataVencimento = dataVencimento;
        }

        public String getCadastro() {
            return cadastro;
        }

        public void setCadastro(String cadastro) {
            this.cadastro = cadastro;
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
        }

        public SituacaoCalculo getSituacaoCalculo() {
            return situacaoCalculo;
        }

        public void setSituacaoCalculo(SituacaoCalculo situacaoCalculo) {
            this.situacaoCalculo = situacaoCalculo;
        }
    }


}
