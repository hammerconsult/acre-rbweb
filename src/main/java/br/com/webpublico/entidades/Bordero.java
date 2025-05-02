/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoBordero;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fabio
 */
@Etiqueta(value = "Ordem Bancária")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class Bordero implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String sequenciaArquivo;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Geração")
    private Date dataGeracao;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Arquivo")
    private Date dataGeracaoArquivo;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Débito")
    private Date dataDebito;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "bordero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderoPagamento> listaPagamentos;
    @OneToMany(mappedBy = "bordero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderoPagamentoExtra> listaPagamentosExtra;
    @OneToMany(mappedBy = "bordero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderoTransferenciaFinanceira> listaTransferenciaFinanceira;
    @OneToMany(mappedBy = "bordero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidade;
    @OneToMany(mappedBy = "bordero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorderoLiberacaoFinanceira> listaLiberacaoCotaFinanceira;
    private String observacao;
    @ManyToOne
    @Etiqueta("Banco")
    private Banco banco;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoBordero situacao;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    private Long qntdPagamentos;
    @Transient
    private Long criadoEm;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public Bordero() {
        criadoEm = System.nanoTime();
        listaPagamentos = new ArrayList<>();
        listaPagamentosExtra = new ArrayList<>();
        listaTransferenciaFinanceira = new ArrayList<>();
        listaTransferenciaMesmaUnidade = new ArrayList<>();
        listaLiberacaoCotaFinanceira = new ArrayList<>();
        valor = BigDecimal.ZERO;
        qntdPagamentos = 0l;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<BorderoLiberacaoFinanceira> getListaLiberacaoCotaFinanceira() {
        return listaLiberacaoCotaFinanceira;
    }

    public void setListaLiberacaoCotaFinanceira(List<BorderoLiberacaoFinanceira> listaLiberacaoCotaFinanceira) {
        this.listaLiberacaoCotaFinanceira = listaLiberacaoCotaFinanceira;
    }

    public List<BorderoTransferenciaMesmaUnidade> getListaTransferenciaMesmaUnidade() {
        return listaTransferenciaMesmaUnidade;
    }

    public void setListaTransferenciaMesmaUnidade(List<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidade) {
        this.listaTransferenciaMesmaUnidade = listaTransferenciaMesmaUnidade;
    }

    public List<BorderoTransferenciaFinanceira> getListaTransferenciaFinanceira() {
        return listaTransferenciaFinanceira;
    }

    public void setListaTransferenciaFinanceira(List<BorderoTransferenciaFinanceira> listaTransferenciaFinanceira) {
        this.listaTransferenciaFinanceira = listaTransferenciaFinanceira;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public List<BorderoPagamento> getListaPagamentos() {
        return listaPagamentos;
    }

    public void setListaPagamentos(List<BorderoPagamento> listaPagamentos) {
        this.listaPagamentos = listaPagamentos;
    }

    public List<BorderoPagamentoExtra> getListaPagamentosExtra() {
        return listaPagamentosExtra;
    }

    public void setListaPagamentosExtra(List<BorderoPagamentoExtra> listaPagamentosExtra) {
        this.listaPagamentosExtra = listaPagamentosExtra;
    }

    public SituacaoBordero getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBordero situacao) {
        this.situacao = situacao;
    }

    public Date getDataDebito() {
        return dataDebito;
    }

    public void setDataDebito(Date dataDebito) {
        this.dataDebito = dataDebito;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Date getDataGeracaoArquivo() {
        return dataGeracaoArquivo;
    }

    public void setDataGeracaoArquivo(Date dataGeracaoArquivo) {
        this.dataGeracaoArquivo = dataGeracaoArquivo;
    }

    public String getSequenciaArquivo() {
        return sequenciaArquivo;
    }

    public void setSequenciaArquivo(String sequenciaArquivo) {
        this.sequenciaArquivo = sequenciaArquivo;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getQntdPagamentos() {
        return qntdPagamentos;
    }

    public void setQntdPagamentos(Long qntdPagamentos) {
        this.qntdPagamentos = qntdPagamentos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return sequenciaArquivo.toString();
    }

    public String toStringAutoComplete() {
        if (dataGeracao != null) {
            return sequenciaArquivo + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataGeracao);
        } else {
            return sequenciaArquivo + " - " + Util.formataValor(valor);
        }
    }

    public boolean isOrdemBancariaAberta() {
        return this.situacao != null && SituacaoBordero.ABERTO.equals(this.situacao);
    }

    public boolean isOrdemBancariaDeferida() {
        return this.situacao != null && SituacaoBordero.DEFERIDO.equals(this.situacao);
    }

    public boolean isOrdemBancariaPaga() {
        return this.situacao != null && SituacaoBordero.PAGO.equals(this.situacao);
    }

    public boolean isOrdemBancariaIndeferida() {
        return this.situacao != null && SituacaoBordero.INDEFERIDO.equals(this.situacao);
    }

    public boolean isOrdemBancariaAguardandoBaixa() {
        return this.situacao != null && SituacaoBordero.INDEFERIDO.equals(this.situacao);
    }


}
