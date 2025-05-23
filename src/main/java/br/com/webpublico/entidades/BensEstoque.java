/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Bens de Estoque")

public class BensEstoque implements Serializable, EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @ReprocessamentoContabil
    private Date dataBem;
    @Etiqueta("Lançamento")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @Etiqueta("Grupo Material")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private GrupoMaterial grupoMaterial;
    @Etiqueta("Tipo de Estoque")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private TipoOperacaoBensEstoque operacoesBensEstoque;
    @ManyToOne
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Etiqueta("Unidade Organizacional")
    @Pesquisavel
    @ManyToOne
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    @Monetario
    @Obrigatorio
    private BigDecimal valor;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Ingresso")
    private TipoIngresso tipoIngresso;
    @Etiqueta("Tipo de Baixa")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private TipoBaixaBens tipoBaixaBens;
    @Etiqueta("Classe")
    @ManyToOne
    private ClasseCredor classeCredor;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Histórico")
    private String historico;
    @Invisivel
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    public BensEstoque() {
        this.criadoEm = System.nanoTime();
        this.dataBem = new Date();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
    }

    public BensEstoque(String numero, Date dataBem, TipoLancamento tipoLancamento, UnidadeOrganizacional unidadeOrganizacional, TipoEstoque tipoEstoque, TipoIngresso tipoIngresso, TipoBaixaBens tipoBaixaBens, GrupoMaterial grupoMaterial, Pessoa pessoa, ClasseCredor classeCredor, String historico, BigDecimal valor) {
        this.numero = numero;
        this.dataBem = dataBem;
        this.tipoLancamento = tipoLancamento;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoEstoque = tipoEstoque;
        this.tipoIngresso = tipoIngresso;
        this.tipoBaixaBens = tipoBaixaBens;
        this.grupoMaterial = grupoMaterial;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.historico = historico;
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Date getDataBem() {
        return dataBem;
    }

    public void setDataBem(Date dataBem) {
        this.dataBem = dataBem;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
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

    public TipoOperacaoBensEstoque getOperacoesBensEstoque() {
        return operacoesBensEstoque;
    }

    public void setOperacoesBensEstoque(TipoOperacaoBensEstoque operacoesBensEstoque) {
        this.operacoesBensEstoque = operacoesBensEstoque;
    }

    public TipoBaixaBens getTipoBaixaBens() {
        return tipoBaixaBens;
    }

    public void setTipoBaixaBens(TipoBaixaBens tipoBaixaBens) {
        this.tipoBaixaBens = tipoBaixaBens;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(TipoIngresso tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataBem()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoEstoque() != null) {
            historicoNota += "Tipo de Bem: " + this.getTipoEstoque().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoMaterial() != null) {
            historicoNota += "Grupo Material: " + this.getGrupoMaterial() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacoesBensEstoque() != null) {
            historicoNota += "Operação: " + this.getOperacoesBensEstoque().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
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
        if (this.getEventoContabil() != null && this.getEventoContabil().getClpHistoricoContabil() != null) {
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
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return numero;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + grupoMaterial.toString();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(), contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }
}
