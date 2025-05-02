/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity

@Audited
public class LancamentoDoctoFiscal implements Serializable, Comparable<LancamentoDoctoFiscal> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    private Long numeroBloco;
    private Long numeroNotaFiscal;
    @ManyToOne
    private NumeroSerie serie;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEmissao;
    private BigDecimal valorNotaFiscal;
    private BigDecimal baseCalculoISS;
    private BigDecimal porcentagemISS;
    private BigDecimal valorIndice;
    private BigDecimal valorISS;
    private BigDecimal valorApurado;
    private Boolean notaCancelada;
    private Boolean notaExtraviada;
    private Boolean naoTributada;
    @Enumerated(EnumType.STRING)
    private TipoNaoTributacao tipoNaoTributacao;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private CadastroAidf aidf;
    private String observacao;
    private String nomeTomador;
    @Enumerated(EnumType.STRING)
    private TipoPessoa tipoPessoaTomador;
    private String cpfCnpjTomador;
    @Transient
    private BigDecimal valorCorrigido;

    public LancamentoDoctoFiscal() {
        criadoEm = System.nanoTime();
        valorNotaFiscal = BigDecimal.ZERO;
        baseCalculoISS = BigDecimal.ZERO;
        porcentagemISS = BigDecimal.ZERO;
        valorIndice = BigDecimal.ZERO;
        valorApurado = BigDecimal.ZERO;
        tipoPessoaTomador = TipoPessoa.FISICA;
    }

    public CadastroAidf getAidf() {
        return aidf;
    }

    public void setAidf(CadastroAidf aidf) {
        this.aidf = aidf;
        String nome;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Long getNumeroBloco() {
        return numeroBloco;
    }

    public void setNumeroBloco(Long numeroBloco) {
        this.numeroBloco = numeroBloco;
    }

    public Long getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(Long numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public BigDecimal getPorcentagemISS() {
        return porcentagemISS;
    }

    public void setPorcentagemISS(BigDecimal porcentagemISS) {
        this.porcentagemISS = porcentagemISS;
    }

    public NumeroSerie getSerie() {
        return serie;
    }

    public void setSerie(NumeroSerie serie) {
        this.serie = serie;
    }

    public BigDecimal getValorApurado() {
        return valorApurado != null ? valorApurado : BigDecimal.ZERO;
    }

    public void setValorApurado(BigDecimal valorApurado) {
        this.valorApurado = valorApurado;
    }

    public BigDecimal getValorIndice() {
        return valorIndice != null ? valorIndice : BigDecimal.ZERO;
    }

    public void setValorIndice(BigDecimal valorIndice) {
        this.valorIndice = valorIndice;
    }

    public BigDecimal getValorNotaFiscal() {
        return valorNotaFiscal != null ? valorNotaFiscal : BigDecimal.ZERO;
    }

    public void setValorNotaFiscal(BigDecimal valorNotaFiscal) {
        this.valorNotaFiscal = valorNotaFiscal;
    }

    public Boolean getNotaCancelada() {
        return notaCancelada != null ? notaCancelada : false;
    }

    public void setNotaCancelada(Boolean notaCancelada) {
        this.notaCancelada = notaCancelada;
    }

    public Boolean getNotaExtraviada() {
        return notaExtraviada != null ? notaExtraviada : false;
    }

    public void setNotaExtraviada(Boolean notaExtraviada) {
        this.notaExtraviada = notaExtraviada;
    }

    public BigDecimal getBaseCalculoISS() {
        return baseCalculoISS != null ? baseCalculoISS : BigDecimal.ZERO;
    }

    public void setBaseCalculoISS(BigDecimal baseCalculoISS) {
        this.baseCalculoISS = baseCalculoISS;
    }

    public Boolean getNaoTributada() {
        return naoTributada != null ? naoTributada : false;
    }

    public void setNaoTributada(Boolean naoTributada) {
        this.naoTributada = naoTributada;
    }

    public TipoNaoTributacao getTipoNaoTributacao() {
        return tipoNaoTributacao;
    }

    public void setTipoNaoTributacao(TipoNaoTributacao tipoNaoTributacao) {
        this.tipoNaoTributacao = tipoNaoTributacao;
    }

    public BigDecimal getValorISS() {
        return valorISS;
    }

    public void setValorISS(BigDecimal valorISS) {
        this.valorISS = valorISS;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNomeCpfCnpjTomador() {
        if (cpfCnpjTomador != null && !"".equals(cpfCnpjTomador)) {
            return nomeTomador + " - " + cpfCnpjTomador;
        }
        return nomeTomador;
    }

    public String getNomeTomador() {
        return nomeTomador;
    }

    public void setNomeTomador(String nomeTomador) {
        this.nomeTomador = nomeTomador;
    }

    public TipoPessoa getTipoPessoaTomador() {
        return tipoPessoaTomador;
    }

    public void setTipoPessoaTomador(TipoPessoa tipoPessoaTomador) {
        this.tipoPessoaTomador = tipoPessoaTomador;
    }

    public String getCpfCnpjTomador() {
        return cpfCnpjTomador;
    }

    public void setCpfCnpjTomador(String cpfCnpjTomador) {
        this.cpfCnpjTomador = cpfCnpjTomador;
    }

    public int getMes() {
        if (dataEmissao != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(dataEmissao);
            return c.get(Calendar.MONTH) + 1;
        }
        return 0;
    }

    public int getAno() {
        if (dataEmissao != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(dataEmissao);
            return c.get(Calendar.YEAR);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.LancamentoDoctoFiscal[ id=" + id + " ]";
    }

    @Override
    public int compareTo(LancamentoDoctoFiscal o) {
        return numeroNotaFiscal.compareTo(o.getNumeroNotaFiscal());
    }

    public BigDecimal getValorCorrigido() {
        return valorCorrigido;
    }

    public void setValorCorrigido(BigDecimal valorCorrigido) {
        this.valorCorrigido = valorCorrigido;
    }

    public static enum TipoNaoTributacao {

        ISENTO_LEI("Isento Por Lei"),
        FORA_DOMICILIO("Fora de Domicílio Fiscal"),
        SIMPLES_NACIONAL("Optante do Simples Nacional"),
        SUBSTITUICAO_TRIBUTARIA("Substituição Tributária"),
        RETIDO_NA_FONTE("Retido na Fonte");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private TipoNaoTributacao(String descricao) {
            this.descricao = descricao;
        }
    }
}
