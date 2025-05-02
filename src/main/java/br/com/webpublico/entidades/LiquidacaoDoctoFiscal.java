package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.reinf.NaturezaRendimentoReinf;
import br.com.webpublico.enums.contabil.reinf.TipoServicoReinf;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Desenvolvimento on 22/10/2015.
 */
@Entity
@Audited
public class LiquidacaoDoctoFiscal extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Liquidação")
    @ManyToOne
    private Liquidacao liquidacao;
    @Etiqueta("Documento Fiscal")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;
    @Etiqueta("Valor Liquidado")
    @Monetario
    private BigDecimal valorLiquidado;
    @Etiqueta("Saldo")
    @Monetario
    private BigDecimal saldo;
    @Enumerated(EnumType.STRING)
    private TipoServicoReinf tipoServicoReinf;
    @Etiqueta("Possui retenções previdenciárias?")
    private Boolean retencaoPrevidenciaria;
    @Etiqueta("Valor base de cálculo INSS")
    @Monetario
    private BigDecimal valorBaseCalculo;
    @ManyToOne
    private NotaFiscal notaFiscal;
    @ManyToOne
    private NaturezaRendimentoReinf naturezaRendimentoReinf;
    @Etiqueta("Optante Simples Nacional/Imune/Isento?")
    private Boolean optanteSimplesNacional;
    @Etiqueta("Matrícula da Obra (CEI/CNO)")
    private String matriculaObra;
    @Etiqueta("Porcentagem Retenção INSS")
    private BigDecimal porcentagemRetencaoMaxima;
    @Etiqueta("Conta Extraorçamentária INSS")
    @ManyToOne
    private Conta contaExtra;
    @Etiqueta("Valor Retido INSS")
    @Monetario
    private BigDecimal valorRetido;
    @Etiqueta("Valor Base de Cálculo IRRF")
    @Monetario
    private BigDecimal valorBaseCalculoIrrf;
    @Etiqueta("Porcentagem Retenção IRRF")
    private BigDecimal porcentagemRetencaoMaximaIrrf;
    @Etiqueta("Conta Extraorçamentária IRRF")
    @ManyToOne
    private Conta contaExtraIrrf;
    @Etiqueta("Valor Retido IRRF")
    @Monetario
    private BigDecimal valorRetidoIrrf;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "liquidacaoDoctoFiscal")
    private List<LiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens;

    public LiquidacaoDoctoFiscal() {
        valorLiquidado = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        valorBaseCalculo = BigDecimal.ZERO;
        retencaoPrevidenciaria = false;
        transferenciasBens = Lists.newArrayList();
    }

    public BigDecimal getValorTotalDasTransferencias() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (LiquidacaoDoctoFiscalTransferenciaBens transferencia : transferenciasBens) {
            retorno = retorno.add(transferencia.getValor());
        }
        return retorno;
    }

    public List<LiquidacaoDoctoFiscalTransferenciaBens> getTransferenciasBens() {
        return transferenciasBens;
    }

    public void setTransferenciasBens(List<LiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens) {
        this.transferenciasBens = transferenciasBens;
    }

    public BigDecimal getSaldo() {
        if (saldo == null) {
            return BigDecimal.ZERO;
        }
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public TipoServicoReinf getTipoServicoReinf() {
        return tipoServicoReinf;
    }

    public void setTipoServicoReinf(TipoServicoReinf tipoServicoReinf) {
        this.tipoServicoReinf = tipoServicoReinf;
    }

    public Boolean getRetencaoPrevidenciaria() {
        return retencaoPrevidenciaria != null ? retencaoPrevidenciaria : Boolean.FALSE;
    }

    public void setRetencaoPrevidenciaria(Boolean retencaoPrevidenciaria) {
        this.retencaoPrevidenciaria = retencaoPrevidenciaria;
    }

    public BigDecimal getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(BigDecimal valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public NaturezaRendimentoReinf getNaturezaRendimentoReinf() {
        return naturezaRendimentoReinf;
    }

    public void setNaturezaRendimentoReinf(NaturezaRendimentoReinf naturezaRendimentoReinf) {
        this.naturezaRendimentoReinf = naturezaRendimentoReinf;
    }

    public Boolean getOptanteSimplesNacional() {
        return optanteSimplesNacional != null ? optanteSimplesNacional : Boolean.FALSE;
    }

    public void setOptanteSimplesNacional(Boolean optanteSimplesNacional) {
        this.optanteSimplesNacional = optanteSimplesNacional;
    }

    public String getMatriculaObra() {
        return matriculaObra;
    }

    public void setMatriculaObra(String matriculaObra) {
        this.matriculaObra = matriculaObra;
    }

    public BigDecimal getPorcentagemRetencaoMaxima() {
        return porcentagemRetencaoMaxima;
    }

    public void setPorcentagemRetencaoMaxima(BigDecimal porcentagemRetencaoMaxima) {
        this.porcentagemRetencaoMaxima = porcentagemRetencaoMaxima;
    }

    public Conta getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(Conta contaExtra) {
        this.contaExtra = contaExtra;
    }

    public BigDecimal getValorRetido() {
        if (valorRetido == null || valorRetido.compareTo(BigDecimal.ZERO) == 0) {
            calcularValorRetidoInss();
        }
        return valorRetido;
    }

    public void setValorRetido(BigDecimal valorRetido) {
        this.valorRetido = valorRetido;
    }

    public BigDecimal getValorCalculoIrInssOuSaldoAtual() {
        if (valorLiquidado != null && saldo.compareTo(valorLiquidado) == 0) {
            return valorLiquidado.subtract(getValorRetido()).subtract(getValorRetidoIrrf());
        }
        return saldo;
    }

    public BigDecimal getValorBaseCalculoIrrf() {
        return valorBaseCalculoIrrf;
    }

    public void setValorBaseCalculoIrrf(BigDecimal valorBaseCalculoIrrf) {
        this.valorBaseCalculoIrrf = valorBaseCalculoIrrf;
    }

    public BigDecimal getPorcentagemRetencaoMaximaIrrf() {
        return porcentagemRetencaoMaximaIrrf;
    }

    public void setPorcentagemRetencaoMaximaIrrf(BigDecimal porcentagemRetencaoMaximaIrrf) {
        this.porcentagemRetencaoMaximaIrrf = porcentagemRetencaoMaximaIrrf;
    }

    public Conta getContaExtraIrrf() {
        return contaExtraIrrf;
    }

    public void setContaExtraIrrf(Conta contaExtraIrrf) {
        this.contaExtraIrrf = contaExtraIrrf;
    }

    public BigDecimal getValorRetidoIrrf() {
        if (valorRetidoIrrf == null || valorRetidoIrrf.compareTo(BigDecimal.ZERO) == 0) {
            calcularValorRetidoIrrf();
        }
        return valorRetidoIrrf;
    }

    public void setValorRetidoIrrf(BigDecimal valorRetidoIrrf) {
        this.valorRetidoIrrf = valorRetidoIrrf;
    }

    public void calcularPorcentagemEValorRetidoInss() {
        calcularPorcentagemInss();
        calcularValorRetidoInss();
    }

    public void calcularPorcentagemInss() {
        if (this != null &&
            this.getValorBaseCalculo() != null && this.getValorBaseCalculo().compareTo(BigDecimal.ZERO) != 0 &&
            this.getValorRetido() != null && this.getValorRetido().compareTo(BigDecimal.ZERO) != 0) {
            this.setPorcentagemRetencaoMaxima((this.getValorRetido().multiply(BigDecimal.valueOf(100))).divide(this.getValorBaseCalculo(), 2, RoundingMode.UP));
        }
    }

    public void calcularPorcentagemIrrf() {
        if (this != null &&
            this.getValorRetidoIrrf() != null && this.getValorRetidoIrrf().compareTo(BigDecimal.ZERO) != 0 &&
            this.getValorBaseCalculoIrrf() != null && this.getValorBaseCalculoIrrf().compareTo(BigDecimal.ZERO) != 0) {
            this.setPorcentagemRetencaoMaximaIrrf((this.getValorRetidoIrrf().multiply(BigDecimal.valueOf(100))).divide(this.getValorBaseCalculoIrrf(), 2, RoundingMode.UP));
        }
    }

    public void calcularValorRetidoInss() {
        if (this != null &&
            this.getValorBaseCalculo() != null && this.getValorBaseCalculo().compareTo(BigDecimal.ZERO) != 0 &&
            this.getPorcentagemRetencaoMaxima() != null && this.getPorcentagemRetencaoMaxima().compareTo(BigDecimal.ZERO) != 0) {
            if (this.getPorcentagemRetencaoMaxima().compareTo(new BigDecimal("100")) > 0) {
                this.setPorcentagemRetencaoMaxima(new BigDecimal("100"));
            }
            if (this.getPorcentagemRetencaoMaxima().compareTo(BigDecimal.ZERO) < 0) {
                this.setPorcentagemRetencaoMaxima(BigDecimal.ZERO);
            }
            this.setValorRetido((this.getValorBaseCalculo().multiply(this.getPorcentagemRetencaoMaxima())).divide(new BigDecimal("100"), 2, RoundingMode.DOWN));
        } else {
            this.setValorRetido(BigDecimal.ZERO);
        }
    }

    public void calcularValorRetidoIrrf() {
        if (this.getValorBaseCalculoIrrf() != null && this.getValorBaseCalculoIrrf().compareTo(BigDecimal.ZERO) != 0 &&
            this.getPorcentagemRetencaoMaximaIrrf() != null && this.getPorcentagemRetencaoMaximaIrrf().compareTo(BigDecimal.ZERO) != 0) {
            if (this.getPorcentagemRetencaoMaximaIrrf().compareTo(new BigDecimal("100")) > 0) {
                this.setPorcentagemRetencaoMaximaIrrf(new BigDecimal("100"));
            }
            if (this.getPorcentagemRetencaoMaximaIrrf().compareTo(BigDecimal.ZERO) < 0) {
                this.setPorcentagemRetencaoMaximaIrrf(BigDecimal.ZERO);
            }
            this.setValorRetidoIrrf((this.getValorBaseCalculoIrrf().multiply(this.getPorcentagemRetencaoMaximaIrrf())).divide(new BigDecimal("100"), 2, RoundingMode.DOWN));
        } else {
            this.setValorRetidoIrrf(BigDecimal.ZERO);
        }
    }

    public void limparCamposDocumentoFiscalEventosReinf2000(Conta contaExtraInss, boolean hasPagamentoNaoEstornado) {
        if (!hasPagamentoNaoEstornado) {
            this.setTipoServicoReinf(null);
            this.setMatriculaObra(null);
            this.setPorcentagemRetencaoMaxima(null);
            this.setValorRetido(null);
            this.setValorBaseCalculo(null);
            this.setContaExtra(contaExtraInss);
        }
    }

    public void limparCamposDocumentoFiscalEventosReinf4000(Conta contaExtraIrrf, boolean hasPagamentoNaoEstornado) {
        if (!hasPagamentoNaoEstornado) {
            this.setNaturezaRendimentoReinf(null);
            this.setPorcentagemRetencaoMaximaIrrf(null);
            this.setValorRetidoIrrf(null);
            this.setContaExtraIrrf(contaExtraIrrf);
            this.setValorBaseCalculoIrrf(this.getDoctoFiscalLiquidacao().getValor());
            this.calcularValorRetidoIrrf();
        }
    }

    @Override
    public String toString() {
        return doctoFiscalLiquidacao.toString();
    }
}
