package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 10/10/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

@Audited
@Etiqueta("Faixa de Valores para Parcelamento")
@Entity
public class FaixaValorParcelamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Valor Inicial")
    private BigDecimal valorInicial;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Valor Final")
    private BigDecimal valorFinal;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade de Parcelas")
    private Integer qtdParcela;
    @ManyToOne
    private ParametrosITBI parametrosITBI;
    @Invisivel
    @Transient
    private Long criadoEm;

    public FaixaValorParcelamento() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Integer getQtdParcela() {
        return qtdParcela;
    }

    public void setQtdParcela(Integer qtdParcela) {
        this.qtdParcela = qtdParcela;
    }

    public ParametrosITBI getParametrosITBI() {
        return parametrosITBI;
    }

    public void setParametrosITBI(ParametrosITBI parametrosITBI) {
        this.parametrosITBI = parametrosITBI;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public boolean faixaEstaContida(FaixaValorParcelamento faixa) {
        if ((faixa.getValorInicial().compareTo(getValorInicial()) >= 0) && (faixa.getValorInicial().compareTo(getValorFinal()) <= 0)) {
            return true;
        }

        if ((faixa.getValorFinal().compareTo(getValorInicial()) >= 0) && (faixa.getValorFinal().compareTo(getValorFinal()) <= 0)) {
            return true;
        }

        return false;
    }

    public boolean valorEstaContido(BigDecimal valor) {
        return valor.compareTo(valorInicial) >= 0 && valor.compareTo(valorFinal) <= 0;
    }
}
