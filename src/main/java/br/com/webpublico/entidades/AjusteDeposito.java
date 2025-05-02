/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.SituacaoAjusteDeposito;
import br.com.webpublico.enums.TipoAjuste;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@Entity

@Audited
@Etiqueta("Ajuste em Depósito")
public class AjusteDeposito extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data de Referência")
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date dataAjuste;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    @ErroReprocessamentoContabil
    private String numero;

    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Ajuste")
    @ErroReprocessamentoContabil
    private TipoAjuste tipoAjuste;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Conta Extraorçamentária")
    private ContaExtraorcamentaria contaExtraorcamentaria;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    private Pessoa pessoa;

    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;

    @ErroReprocessamentoContabil
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor (R$)")
    @Monetario
    @Obrigatorio
    @ErroReprocessamentoContabil
    private BigDecimal valor;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Saldo (R$)")
    @Monetario
    @Obrigatorio
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoAjusteDeposito situacao;

    @ManyToOne
    @ErroReprocessamentoContabil
    @Etiqueta("Fonte de Recurso")
    private FonteDeRecursos fonteDeRecurso;

    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;

    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    private String historicoNota;
    private String historicoRazao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ajusteDeposito", orphanRemoval = true)
    private List<AjusteDepositoReceitaExtra> receitasExtras;

    public AjusteDeposito() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        situacao = SituacaoAjusteDeposito.ABERTO;
        receitasExtras = Lists.newArrayList();
    }

    public AjusteDeposito(String numero, Date dataAjuste, UnidadeOrganizacional unidadeOrganizacional, TipoAjuste tipoAjuste, ContaExtraorcamentaria contaExtraorcamentaria, Pessoa pessoa, ClasseCredor classeCredor, String historico, BigDecimal valor, FonteDeRecursos fonteDeRecurso, EventoContabil eventoContabil) {
        this.numero = numero;
        this.dataAjuste = dataAjuste;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoAjuste = tipoAjuste;
        this.contaExtraorcamentaria = contaExtraorcamentaria;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.historico = historico;
        this.valor = valor;
        this.fonteDeRecurso = fonteDeRecurso;
        this.eventoContabil = eventoContabil;
    }

    public SituacaoAjusteDeposito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAjusteDeposito situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<AjusteDepositoReceitaExtra> getReceitasExtras() {
        if (receitasExtras != null) {
            Collections.sort(receitasExtras, new Comparator<AjusteDepositoReceitaExtra>() {
                @Override
                public int compare(AjusteDepositoReceitaExtra o1, AjusteDepositoReceitaExtra o2) {
                    int i = o1.getReceitaExtra().getDataReceita().compareTo(o2.getReceitaExtra().getDataReceita());
                    if (i == 0) {
                        i = o1.getReceitaExtra().getNumero().compareTo(o2.getReceitaExtra().getNumero());
                    }
                    return i;
                }
            });
        }
        return receitasExtras;
    }

    public void setReceitasExtras(List<AjusteDepositoReceitaExtra> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public Date getDataAjuste() {
        return dataAjuste;
    }

    public void setDataAjuste(Date dataAjuste) {
        this.dataAjuste = dataAjuste;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoAjuste getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjuste tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FonteDeRecursos getFonteDeRecurso() {
        return fonteDeRecurso;
    }

    public void setFonteDeRecurso(FonteDeRecursos fonteDeRecurso) {
        this.fonteDeRecurso = fonteDeRecurso;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataAjuste()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecurso() != null) {
            historicoNota += "Fonte de Recurso: " + this.getFonteDeRecurso() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaExtraorcamentaria() != null) {
            historicoNota += "Conta Extraorçamentária: " + this.getContaExtraorcamentaria() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += "Pessoa: " + this.getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredor() != null) {
            historicoNota += "Classe: " + this.getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataAjuste) + " - " + tipoAjuste.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    public boolean isAjusteAumentativo() {
        return tipoAjuste != null && TipoAjuste.AUMENTATIVO.equals(tipoAjuste);
    }

    public boolean isAjusteDiminutivo() {
        return tipoAjuste != null && TipoAjuste.DIMINUTIVO.equals(tipoAjuste);
    }

    public BigDecimal getValorTotalReceitaExtra() {
        BigDecimal total = BigDecimal.ZERO;
        for (AjusteDepositoReceitaExtra receita : this.getReceitasExtras()) {
            total = total.add(receita.getReceitaExtra().getValor());
        }
        return total;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), null, getContaDeDestinacao(), null, null, null, null, fonteDeRecurso.getExercicio(), null, null, 0, null, null);
    }
}
