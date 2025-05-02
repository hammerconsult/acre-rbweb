/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Entity
@Audited
@Etiqueta("Estorno de Liquidação")
@GrupoDiagrama(nome = "Contabil")
public class LiquidacaoEstorno extends SuperEntidade implements EntidadeContabilComValor {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;

    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;

    @ErroReprocessamentoContabil
    @Etiqueta("Liquidação")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Liquidacao liquidacao;

    @Tabelavel
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Histórico Contábil")
    @ManyToOne
    private HistoricoContabil historicoContabil;

    @Etiqueta("Histórico")
    @Obrigatorio
    private String complementoHistorico;

    @Invisivel
    @ManyToOne
    private MovimentoDespesaORC movimentoDespesaORC;

    @Enumerated(EnumType.STRING)
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;

    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @OneToMany(mappedBy = "liquidacaoEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoLiquidacaoEstorno> desdobramentos;

    @OneToMany(mappedBy = "liquidacaoEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiquidacaoEstornoDoctoFiscal> documentosFiscais;

    private String historicoNota;
    private String historicoRazao;

    public LiquidacaoEstorno() {
        valor = new BigDecimal(BigInteger.ZERO);
        desdobramentos = new ArrayList<>();
        documentosFiscais = new ArrayList<>();
    }

    public LiquidacaoEstorno(Date dataEstorno, BigDecimal valor, String numero, HistoricoContabil historicoContabil, String complementoHistorico, Liquidacao liquidacao, MovimentoDespesaORC movimentoDespesaORC, CategoriaOrcamentaria categoriaOrcamentaria) {
        this.dataEstorno = dataEstorno;
        this.valor = valor;
        this.numero = numero;
        this.historicoContabil = historicoContabil;
        this.complementoHistorico = complementoHistorico;
        this.liquidacao = liquidacao;
        this.movimentoDespesaORC = movimentoDespesaORC;
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public LiquidacaoEstorno(Long id, String numero, Date dataEstorno, Long liquidacao, BigDecimal valor) {
        this.id = id;
        this.dataEstorno = dataEstorno;
        this.valor = valor;
        this.numero = numero;
        this.liquidacao = new Liquidacao();
        this.liquidacao.setId(liquidacao);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public List<DesdobramentoLiquidacaoEstorno> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoLiquidacaoEstorno> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public List<LiquidacaoEstornoDoctoFiscal> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<LiquidacaoEstornoDoctoFiscal> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LiquidacaoEstorno)) {
            return false;
        }
        LiquidacaoEstorno other = (LiquidacaoEstorno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Nº " + numero + " - Liquidação: " + liquidacao;
    }

    private void gerarHistoricoNota() {
        String historico = "";
        if (this.getNumero() != null) {
            historico += "Nº: " + this.getNumero() + "/" + this.getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiquidacao() != null) {
            historico += "Liquidação: " + this.getLiquidacao().getNumero() + "/" + this.getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiquidacao().getEmpenho() != null) {
            historico += this.getLiquidacao().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Dotação: " + this.getLiquidacao().getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Conta de Despesa: " + this.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + "." + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDesdobramentos() != null) {
            for (DesdobramentoLiquidacaoEstorno desdobramento : this.getDesdobramentos()) {
                historico += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getLiquidacao().getEmpenho().getFonteDespesaORC() != null) {
            historico += " Fonte de Recursos: " + ((ContaDeDestinacao) this.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiquidacao().getEmpenho() != null) {
            historico += " Pessoa: " + this.getLiquidacao().getEmpenho().getFornecedor().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Classe: " + this.getLiquidacao().getEmpenho().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiquidacao().getDoctoFiscais() != null) {
            for (LiquidacaoDoctoFiscal doc : this.getLiquidacao().getDoctoFiscais()) {
                historico += " Tipo: " + doc.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal() + " -  Nº Documento Comprobatório: " + doc.getDoctoFiscalLiquidacao().getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getComplementoHistorico() != null) {
            historico += " " + this.getComplementoHistorico();
        }
        this.historicoNota = historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    private void gerarHistoricoRazao() {
        historicoRazao = " ";
        for (DesdobramentoLiquidacaoEstorno desd : this.getDesdobramentos()) {
            if (desd.getEventoContabil() != null) {
                if (desd.getEventoContabil().getClpHistoricoContabil() != null) {
                    historicoRazao += desd.getEventoContabil().getClpHistoricoContabil().toString() + " ";
                }
            }
        }
        historicoRazao += this.historicoNota;
        historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + ((EntidadeContabil) liquidacao).getReferenciaArquivoPrestacaoDeContas();
    }

    public boolean isLiquidacaoEstornoCategoriaRestoPagar() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.RESTO.equals(this.categoriaOrcamentaria);
    }

    public boolean isLiquidacaoEstornoCategoriaNormal() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.NORMAL.equals(this.categoriaOrcamentaria);
    }

    public BigDecimal getValorTotalDetalhamentos() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoLiquidacaoEstorno desd : getDesdobramentos()) {
            valor = valor.add(desd.getValor());
        }
        return valor;
    }

    public BigDecimal getValorTotalDocumentosFiscais() {
        BigDecimal valor = BigDecimal.ZERO;
        for (LiquidacaoEstornoDoctoFiscal doctoFiscal : getDocumentosFiscais()) {
            valor = valor.add(doctoFiscal.getValor());
        }
        return valor;
    }

    public void validarMesmoDetalhamentoEmLista(DesdobramentoLiquidacaoEstorno desdSelecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoLiquidacaoEstorno desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    public void validarMesmoDetalhamentoEmListaPoObrigacaoPagar(DesdobramentoLiquidacaoEstorno desdSelecionado, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoLiquidacaoEstorno desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)
                && desdEmLista.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    private void isMesmoDetalhamentoDespesa(Conta conta, ValidacaoException ve, DesdobramentoLiquidacaoEstorno desdEmLista) {
        if (desdEmLista.getConta().equals(conta)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta " + conta + " já foi adicionada na lista.");
        }
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}
