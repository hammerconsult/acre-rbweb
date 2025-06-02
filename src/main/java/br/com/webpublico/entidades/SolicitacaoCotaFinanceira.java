/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Solicitação Financeira")

public class SolicitacaoCotaFinanceira extends SuperEntidadeContabilGerarContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Solicitação")
    private Date dtSolicitacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private String numero;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário Solicitante")
    private UsuarioSistema usuarioSolicitante;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Solicitado (R$)")
    private BigDecimal valorSolicitado;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Aprovado (R$)")
    private BigDecimal valorAprovado;

    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta("Valor Liberado")
    private BigDecimal valorLiberado;

    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Dependência da Execução Orçamentária")
    private ResultanteIndependente resultanteIndependente;

    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Conta Financeira")
    private SubConta contaFinanceira;

    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;

    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta("Histórico")
    private String historicoSolicitacao;

    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta("Histórico da Liberação")
    private String historicoLiberacao;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Pesquisavel
    @Etiqueta("Saldo à Liberar (R$)")
    private BigDecimal saldo;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA, campoSelecionado = false)
    @Monetario
    @Pesquisavel
    @Etiqueta("Saldo à Aprovar (R$)")
    private BigDecimal saldoAprovar;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private StatusSolicitacaoCotaFinanceira status;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta("Unidade Organizacional Administrativa")
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Cancelamento")
    private Date dataCancelamento;

    @Etiqueta("Motivo do Cancelamento")
    @Length(maximo = 3000)
    private String motivoCancelamento;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "solicitacao", orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<SolicitacaoFinanceiraContaDespesa> elementosDespesa;

    @Version
    private Long versao;

    public SolicitacaoCotaFinanceira() {
        valorSolicitado = new BigDecimal(BigInteger.ZERO);
        valorLiberado = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        saldoAprovar = new BigDecimal(BigInteger.ZERO);
        valorAprovado = new BigDecimal(BigInteger.ZERO);
        status = StatusSolicitacaoCotaFinanceira.ABERTA;
        elementosDespesa = new ArrayList<>();
    }

    public SolicitacaoCotaFinanceira(Date dtSolicitacao, BigDecimal valorSolicitado, BigDecimal valorLiberado, UsuarioSistema usuarioSolicitante, StatusSolicitacaoCotaFinanceira status, String numero, ResultanteIndependente resultanteIndependente, UnidadeOrganizacional unidadeOrganizacional, SubConta contaFinanceira, FonteDeRecursos fonteDeRecursos, String historicoSolicitacao, String historicoLiberacao, BigDecimal saldo) {
        this.dtSolicitacao = dtSolicitacao;
        this.valorSolicitado = valorSolicitado;
        this.valorLiberado = valorLiberado;
        this.usuarioSolicitante = usuarioSolicitante;
        this.status = status;
        this.numero = numero;
        this.resultanteIndependente = resultanteIndependente;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaFinanceira = contaFinanceira;
        this.fonteDeRecursos = fonteDeRecursos;
        this.historicoSolicitacao = historicoSolicitacao;
        this.historicoLiberacao = historicoLiberacao;
        this.saldo = saldo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public StatusSolicitacaoCotaFinanceira getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoCotaFinanceira status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDtSolicitacao() {
        return dtSolicitacao;
    }

    public void setDtSolicitacao(Date dtSolicitacao) {
        this.dtSolicitacao = dtSolicitacao;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(BigDecimal valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    public UsuarioSistema getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(UsuarioSistema usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getHistoricoLiberacao() {
        return historicoLiberacao;
    }

    public void setHistoricoLiberacao(String historicoLiberacao) {
        this.historicoLiberacao = historicoLiberacao;
    }

    public String getHistoricoSolicitacao() {
        return historicoSolicitacao;
    }

    public void setHistoricoSolicitacao(String historicoSolicitacao) {
        this.historicoSolicitacao = historicoSolicitacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(BigDecimal valorLiberado) {
        this.valorLiberado = valorLiberado;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public BigDecimal getValorAprovado() {
        return valorAprovado;
    }

    public void setValorAprovado(BigDecimal valorAprovado) {
        this.valorAprovado = valorAprovado;
    }

    public BigDecimal getSaldoAprovar() {
        return saldoAprovar;
    }

    public void setSaldoAprovar(BigDecimal saldoAprovar) {
        this.saldoAprovar = saldoAprovar;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public List<SolicitacaoFinanceiraContaDespesa> getElementosDespesa() {
        return elementosDespesa;
    }

    public void setElementosDespesa(List<SolicitacaoFinanceiraContaDespesa> elementosDespesa) {
        this.elementosDespesa = elementosDespesa;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (numero != null) {
            retorno += "Nº " + numero;
        }
        if (usuarioSolicitante != null) {
            retorno += " - " + usuarioSolicitante;
        }

        retorno = getValorPorStatus(retorno);

        if (dtSolicitacao != null) {
            retorno += " - (" + DataUtil.getDataFormatada(dtSolicitacao) + ") ";
        }
        return retorno;
    }

    private String getValorPorStatus(String retorno) {
        if (status.equals(StatusSolicitacaoCotaFinanceira.ABERTA) || status.equals(StatusSolicitacaoCotaFinanceira.A_APROVAR)) {
            if (valorSolicitado != null) {
                retorno += " - Valor " + Util.formataValor(valorSolicitado);
            }
        } else if (status.equals(StatusSolicitacaoCotaFinanceira.A_APROVAR) || status.equals(StatusSolicitacaoCotaFinanceira.A_LIBERAR)) {
            if (valorAprovado != null) {
                retorno += " - Valor " + Util.formataValor(valorAprovado);
            }
        } else if (status.equals(StatusSolicitacaoCotaFinanceira.CONCLUIDO) || status.equals(StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE)) {
            if (valorLiberado != null) {
                retorno += " - Valor " + Util.formataValor(valorLiberado);
            }
        } else if (status.equals(StatusSolicitacaoCotaFinanceira.CANCELADO)) {
            if (valorSolicitado != null) {
                retorno += " - Valor " + Util.formataValor(valorSolicitado);
            }
        }
        return retorno;
    }

    public boolean isSolicitacaoAberta() {
        return this.getStatus() != null && StatusSolicitacaoCotaFinanceira.ABERTA.equals(this.status);
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(),
            getContaDeDestinacao(), getExercicio());
    }
}
