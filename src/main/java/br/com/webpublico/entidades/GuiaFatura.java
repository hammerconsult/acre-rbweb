package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by RenatoRomanini on 11/05/2015.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class GuiaFatura extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código de Barras")
    private String codigoBarra;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Vencimento")
    private Date dataVencimento;
    @Obrigatorio
    @Etiqueta("Valor Nominal")
    private BigDecimal valorNominal;
    @Obrigatorio
    @Etiqueta("Valor Desconto")
    private BigDecimal valorDescontos;
    @Obrigatorio
    @Etiqueta("Valor do Juros")
    private BigDecimal valorJuros;

    public GuiaFatura() {
        valorNominal = BigDecimal.ZERO;
        valorDescontos = BigDecimal.ZERO;
        valorJuros = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public BigDecimal getValorDescontos() {
        return valorDescontos;
    }

    public void setValorDescontos(BigDecimal valorDescontos) {
        this.valorDescontos = valorDescontos;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataVencimento) + Util.formataValor(valorNominal);
    }
}
