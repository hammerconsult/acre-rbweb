package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCobrancaParcelamento;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity

@Audited
public class ParamParcelamentoTributo extends SuperEntidade  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParamParcelamento paramParcelamento;
    private BigDecimal percDescontoImposto;
    private BigDecimal percDescontoTaxa;
    private BigDecimal percentualMulta;
    private BigDecimal percentualJuros;
    private BigDecimal percentualCorrecaoMonetaria;
    private BigDecimal percentualHonorarios;
    private Integer numeroParcelaInicial;
    private Integer numeroParcelaFinal;
    @Temporal(TemporalType.DATE)
    private Date vencimentoFinalParcela;


    public ParamParcelamentoTributo() {
        super();
        percDescontoImposto = BigDecimal.ZERO;
        percDescontoTaxa = BigDecimal.ZERO;
        percentualJuros = BigDecimal.ZERO;
        percentualMulta = BigDecimal.ZERO;
        percentualCorrecaoMonetaria = BigDecimal.ZERO;
        percentualHonorarios = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getPercDescontoImposto() {
        return percDescontoImposto != null ? percDescontoImposto : BigDecimal.ZERO;
    }

    public void setPercDescontoImposto(BigDecimal percDescontoImposto) {
        this.percDescontoImposto = percDescontoImposto;
    }

    public BigDecimal getPercDescontoTaxa() {
        return percDescontoTaxa != null ? percDescontoTaxa : BigDecimal.ZERO;
    }

    public void setPercDescontoTaxa(BigDecimal percDescontoTaxa) {
        this.percDescontoTaxa = percDescontoTaxa;
    }

    public BigDecimal getPercentualCorrecaoMonetaria() {
        return percentualCorrecaoMonetaria != null ? percentualCorrecaoMonetaria : BigDecimal.ZERO;
    }

    public void setPercentualCorrecaoMonetaria(BigDecimal percentualCorrecaoMonetaria) {
        this.percentualCorrecaoMonetaria = percentualCorrecaoMonetaria;
    }

    public BigDecimal getPercentualJuros() {
        return percentualJuros != null ? percentualJuros : BigDecimal.ZERO;
    }

    public void setPercentualJuros(BigDecimal percentualJuros) {
        this.percentualJuros = percentualJuros;
    }

    public BigDecimal getPercentualMulta() {
        return percentualMulta != null ? percentualMulta : BigDecimal.ZERO;
    }

    public void setPercentualMulta(BigDecimal percentualMulta) {
        this.percentualMulta = percentualMulta;
    }

    public ParamParcelamento getParamParcelamento() {
        return paramParcelamento;
    }

    public void setParamParcelamento(ParamParcelamento paramParcelamento) {
        this.paramParcelamento = paramParcelamento;
    }

    public Integer getNumeroParcelaInicial() {
        return numeroParcelaInicial;
    }

    public void setNumeroParcelaInicial(Integer numeroParcelaInicial) {
        this.numeroParcelaInicial = numeroParcelaInicial;
    }

    public Integer getNumeroParcelaFinal() {
        return numeroParcelaFinal;
    }

    public void setNumeroParcelaFinal(Integer numeroParcelaFinal) {
        this.numeroParcelaFinal = numeroParcelaFinal;
    }

    public Date getVencimentoFinalParcela() {
        return vencimentoFinalParcela;
    }

    public void setVencimentoFinalParcela(Date vencimentoFinalParcela) {
        this.vencimentoFinalParcela = vencimentoFinalParcela;
    }

    public BigDecimal getPercentualHonorarios() {
        return percentualHonorarios != null ? percentualHonorarios : BigDecimal.ZERO;
    }

    public void setPercentualHonorarios(BigDecimal percentualHonorarios) {
        this.percentualHonorarios = percentualHonorarios;
    }


    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParamParcelamentoTributo[ id=" + id + " ]";
    }
}
