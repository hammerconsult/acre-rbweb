package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoContaFinanceira;
import br.com.webpublico.enums.TipoRecursoSubConta;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SubContaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/07/14
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AutoCompleteContaFinanceiraControlador implements Serializable {
    @EJB
    private SubContaFacade subContaFacade;
    private ContaBancariaEntidade contaBancariaEntidade;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaDeDestinacao contaDeDestinacao;
    private DividaPublica dividaPublica;
    private Exercicio exercicio;
    private List<TipoContaFinanceira> tiposContasFinanceira;
    private List<TipoRecursoSubConta> tipoRecursoSubContas;
    private Boolean buscarSomenteAtivas;

    public List<TipoRecursoSubConta> getTipoRecursoSubContas() {
        return tipoRecursoSubContas;
    }

    public void setTipoRecursoSubContas(List<TipoRecursoSubConta> tipoRecursoSubContas) {
        this.tipoRecursoSubContas = tipoRecursoSubContas;
    }

    public List<TipoContaFinanceira> getTiposContasFinanceira() {
        return tiposContasFinanceira;
    }

    public void setTiposContasFinanceira(List<TipoContaFinanceira> tiposContasFinanceira) {
        this.tiposContasFinanceira = tiposContasFinanceira;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public void novo(ContaBancariaEntidade conta, UnidadeOrganizacional uo, ContaDeDestinacao contaDeDestinacao, List<TipoContaFinanceira> tipos, List<TipoRecursoSubConta> tipoRecursos, Exercicio exercicio, DividaPublica dividaPublica, Boolean buscarSomenteAtivas) {
        this.contaBancariaEntidade = conta;
        this.unidadeOrganizacional = uo;
        this.contaDeDestinacao = contaDeDestinacao;
        this.tiposContasFinanceira = tipos;
        this.tipoRecursoSubContas = tipoRecursos;
        this.dividaPublica = dividaPublica;
        this.buscarSomenteAtivas = buscarSomenteAtivas;
        getExercicio(exercicio);
    }

    private void getExercicio(Exercicio exercicio) {
        if (exercicio == null) {
            this.exercicio = getSistemaControlador().getExercicioCorrente();
        } else {
            this.exercicio = exercicio;
        }
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<SubConta> completaSubContas(String parte) {
        try {
            if (dividaPublica == null) {
                return subContaFacade.buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(parte.trim(), contaBancariaEntidade, unidadeOrganizacional, getSistemaControlador().getExercicioCorrente(), contaDeDestinacao, tiposContasFinanceira, tipoRecursoSubContas, buscarSomenteAtivas);
            } else {
                return subContaFacade.buscarContasFinanceirasPorDividaPublica(parte.trim(), contaBancariaEntidade, unidadeOrganizacional, getSistemaControlador().getExercicioCorrente(), contaDeDestinacao, dividaPublica, buscarSomenteAtivas);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), ex.getMessage());
        }
        return new ArrayList<>();
    }

    public BigDecimal recuperarSaldoSubConta(SubConta subContaSelecionada) {
        BigDecimal valorSubConta;
        if (subContaSelecionada != null && contaDeDestinacao != null && unidadeOrganizacional != null) {
            SaldoSubConta saldoSubConta = subContaFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subContaSelecionada, contaDeDestinacao, getSistemaControlador().getDataOperacao());
            valorSubConta = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
        } else if (subContaSelecionada != null && unidadeOrganizacional != null) {
            SaldoSubConta saldoSubConta = subContaFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subContaSelecionada, new ContaDeDestinacao(), getSistemaControlador().getDataOperacao());
            valorSubConta = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
        } else {
            valorSubConta = BigDecimal.ZERO;
        }
        return valorSubConta;
    }

    public String montaTitleContaFinanceira() {
        String retorno = "Informe uma Conta Financeira filtrando por ";
        if (contaBancariaEntidade != null) {
            retorno += " Conta Bancária " + contaBancariaEntidade + ",";
        }
        if (unidadeOrganizacional != null) {
            retorno += " Unidade " + unidadeOrganizacional.getDescricao() + ",";
        }
        if (contaDeDestinacao != null) {
            retorno += " Conta de Destinação de Recursos " + contaDeDestinacao + ",";
        }
        if (tiposContasFinanceira != null) {
            retorno += " Tipos " + subContaFacade.getTiposContaFinanceiraAsString(tiposContasFinanceira) + ",";
        }
        if (tipoRecursoSubContas != null) {
            retorno += " Tipos de Recursos " + subContaFacade.getTiposRecursosAsString(tipoRecursoSubContas) + ",";
        }
        retorno = retorno.substring(0, retorno.length() - 1) + ".";
        return retorno;
    }
}
