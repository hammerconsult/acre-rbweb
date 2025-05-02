package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity

@Audited
public class ParamParcelamentoFaixa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParamParcelamento paramParcelamento;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Integer quantidadeMaximaParcelas;

    @Transient
    private Long criadoEm;

    public ParamParcelamentoFaixa() {
        criadoEm = System.nanoTime();
        valorFinal = BigDecimal.ZERO;
        valorInicial = BigDecimal.ZERO;
        quantidadeMaximaParcelas = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParamParcelamento getParamParcelamento() {
        return paramParcelamento;
    }

    public void setParamParcelamento(ParamParcelamento paramParcelamento) {
        this.paramParcelamento = paramParcelamento;
    }

    public Integer getQuantidadeMaximaParcelas() {
        return quantidadeMaximaParcelas;
    }

    public void setQuantidadeMaximaParcelas(Integer quantidadeMaximaParcelas) {
        this.quantidadeMaximaParcelas = quantidadeMaximaParcelas;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParamParcelamentoFaixa[ id=" + id + " ]";
    }
}
