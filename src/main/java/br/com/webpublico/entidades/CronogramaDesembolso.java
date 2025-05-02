/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Cronograma de Desembolso")
public class CronogramaDesembolso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data do Cronograma")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataCronograma;
    @Etiqueta("Valor do Convênio do Cronograma")
    @Tabelavel
    private BigDecimal valorConvenioCronograma;
    @Etiqueta("Valor do Convênio da Contra-Partida")
    @Tabelavel
    private BigDecimal valorConvenioContapartida;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @Transient
    private Long criadoEm;

    public CronogramaDesembolso() {
        valorConvenioContapartida = new BigDecimal(BigInteger.ZERO);
        valorConvenioCronograma = new BigDecimal(BigInteger.ZERO);
        criadoEm = System.nanoTime();
    }

    public CronogramaDesembolso(Date dataCronograma, BigDecimal valorConvenioCronograma, BigDecimal valorConvenioContapartida, ConvenioDespesa convenioDespesa) {
        this.dataCronograma = dataCronograma;
        this.valorConvenioCronograma = valorConvenioCronograma;
        this.valorConvenioContapartida = valorConvenioContapartida;
        this.convenioDespesa = convenioDespesa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataCronograma() {
        return dataCronograma;
    }

    public void setDataCronograma(Date dataCronograma) {
        this.dataCronograma = dataCronograma;
    }

    public BigDecimal getValorConvenioContapartida() {
        return valorConvenioContapartida;
    }

    public void setValorConvenioContapartida(BigDecimal valorConvenioContapartida) {
        this.valorConvenioContapartida = valorConvenioContapartida;
    }

    public BigDecimal getValorConvenioCronograma() {
        return valorConvenioCronograma;
    }

    public void setValorConvenioCronograma(BigDecimal valorConvenioCronograma) {
        this.valorConvenioCronograma = valorConvenioCronograma;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
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

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CronogramaDesembolso[ id=" + id + " ]";
    }
}
