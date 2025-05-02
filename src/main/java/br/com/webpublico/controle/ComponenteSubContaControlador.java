/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoRecursoSubConta;
import br.com.webpublico.negocios.SaldoSubContaFacade;
import br.com.webpublico.negocios.SubContaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Fabio
 *         <p>
 *         Classe para controlar as ações dos componentes de Conta Financeira.
 */
@SessionScoped
@ManagedBean
public class ComponenteSubContaControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ComponenteSubContaControlador.class);
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    private ConverterAutoComplete converterSubConta;
    private transient HtmlOutputText valor;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private SubConta subContaSelecionada;
    private TipoRecursoSubConta tipoRecursoSubConta;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaDeDestinacao contaDeDestinacao;
    private BigDecimal valorSubConta;
    private Exercicio exercicio;

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterSubConta;
    }

    public void setConverterSubConta(ConverterAutoComplete converterSubConta) {
        this.converterSubConta = converterSubConta;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public void setSubContaFacade(SubContaFacade subContaFacade) {
        this.subContaFacade = subContaFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public void setSaldoSubContaFacade(SaldoSubContaFacade saldoSubContaFacade) {
        this.saldoSubContaFacade = saldoSubContaFacade;
    }

    public SubConta getSubContaSelecionada() {
        return subContaSelecionada;
    }

    public void setSubContaSelecionada(SubConta subContaSelecionada) {
        this.subContaSelecionada = subContaSelecionada;
    }

    public SaldoSubConta recuperarSaldo(SubConta subConta) {
        return saldoSubContaFacade.recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subConta, contaDeDestinacao, sistemaControlador.getDataOperacao());
    }

    public List<SubConta> completaSubConta(String parte) {
        if (unidadeOrganizacional != null && tipoRecursoSubConta == null && contaDeDestinacao == null) {
            return subContaFacade.listaPorUnidadeOrganizacional(parte.trim(), unidadeOrganizacional, sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao());
        }
        if (unidadeOrganizacional != null && tipoRecursoSubConta == null && contaDeDestinacao != null) {
            return subContaFacade.buscarContasFinanceiraPorUnidadeTipoContaDeDestinacao(parte.trim(), unidadeOrganizacional, null, contaDeDestinacao);
        }
        if (unidadeOrganizacional == null && tipoRecursoSubConta != null && exercicio != null) {
            return subContaFacade.listaFiltrandoSubContasPorTipoRecursoExercicio(parte.trim(), tipoRecursoSubConta, exercicio);
        }
        if (unidadeOrganizacional == null && tipoRecursoSubConta != null) {
            return subContaFacade.listaFiltrandoSubContasPorTipoRecurso(parte.trim(), tipoRecursoSubConta);
        }
        if (unidadeOrganizacional != null && tipoRecursoSubConta != null) {
            return subContaFacade.listaPorUnidadeOrganizacionalTipoRecurso(parte.trim(), unidadeOrganizacional, tipoRecursoSubConta);
        }
        return subContaFacade.listaFiltrandoSubConta(parte.trim());

    }

    public HtmlOutputText getValor() {
        return valor;
    }

    public void setValor(HtmlOutputText valor) {
        this.valor = valor;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setaSubContaSelecionada(SubConta subContaSelecionada) {
        if (subContaSelecionada != null) {
            this.subContaSelecionada = subContaSelecionada;
        }
    }

    public void setaUnidadeOrganizacional(UnidadeOrganizacional unidade) {
        if (unidade != null) {
            this.unidadeOrganizacional = unidade;
        }
    }

    public void atualizarContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        if (contaDeDestinacao != null) {
            this.contaDeDestinacao = contaDeDestinacao;
        }
    }

    public void setaExercicio(Exercicio exercicio) {
        if (exercicio != null) {
            this.exercicio = exercicio;
        }
    }

    public void setaTipoRecursoSubConta(String tipo) {
        try {
            if (tipo.equals(TipoRecursoSubConta.CONVENIO_CONGENERE.name())) {
                tipoRecursoSubConta = TipoRecursoSubConta.CONVENIO_CONGENERE;
            } else if (tipo.equals(TipoRecursoSubConta.OPERACAO_CREDITO.name())) {
                tipoRecursoSubConta = TipoRecursoSubConta.OPERACAO_CREDITO;
            } else if (tipo.equals(TipoRecursoSubConta.RECURSO_TESOURO.name())) {
                tipoRecursoSubConta = TipoRecursoSubConta.RECURSO_TESOURO;
            } else if (tipo.equals(TipoRecursoSubConta.RECURSO_UNIDADE.name())) {
                tipoRecursoSubConta = TipoRecursoSubConta.RECURSO_UNIDADE;
            } else if (tipo.equals(TipoRecursoSubConta.RECURSO_VINCULADO.name())) {
                tipoRecursoSubConta = TipoRecursoSubConta.RECURSO_VINCULADO;
            }
        } catch (Exception e) {
            tipoRecursoSubConta = null;
        }

    }

    public void actionListener(SelectEvent evt) {
        this.subContaSelecionada = ((SubConta) evt.getObject());
    }

    public boolean rederDetalhes() {
        if (this.subContaSelecionada == null) {
            return false;
        }
        return this.subContaSelecionada.getId() != null;
    }

    public void naoFazNada() {
    }

    public BigDecimal getValorSubConta() {
        if (subContaSelecionada != null && contaDeDestinacao != null && unidadeOrganizacional != null) {
            SaldoSubConta saldoSubConta = recuperarSaldo(subContaSelecionada);
            valorSubConta = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
        } else if (subContaSelecionada != null && unidadeOrganizacional != null) {
            SaldoSubConta saldoSubConta = saldoSubContaFacade.recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subContaSelecionada, new ContaDeDestinacao(), sistemaControlador.getDataOperacao());
            valorSubConta = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
        } else {
            valorSubConta = BigDecimal.ZERO;
        }
        return valorSubConta;
    }

    public void setValorSubConta(BigDecimal valorSubConta) {
        this.valorSubConta = valorSubConta;
    }

    public String getTitleAutoCompleteSubConta() {
        try {
            String retorno = "Selecione uma Conta Financeira filtrando ";
            if (unidadeOrganizacional != null) {
                retorno += " pela Unidade Orçamentária: " + unidadeOrganizacional.getDescricao() + ";";
            }
            if (contaDeDestinacao != null) {
                retorno += " pela Conta de Destinação de Recursos: " + contaDeDestinacao + ";";
            }
            if (tipoRecursoSubConta != null) {
                retorno += " pelo Tipo de Recurso da Conta Financeira: " + tipoRecursoSubConta.getDescricao() + ";";
            }
            if (exercicio != null) {
                retorno += " pelo exercício: " + exercicio.getAno() + ";";
            }
            return retorno;
        } catch (Exception e) {
            return "Selecione uma Conta Financeira";
        }
    }
}
