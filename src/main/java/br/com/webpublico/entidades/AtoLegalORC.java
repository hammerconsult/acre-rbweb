/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Orcamentario")
@Audited

@Entity
public class AtoLegalORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Crédito Suplementar")
    private BigDecimal creditoSuplementar;
    @Etiqueta("Crédito Especial")
    private BigDecimal creditoEspecial;
    @Etiqueta("Crédito Extraordinario")
    private BigDecimal creditoExtraordinario;
    @Etiqueta("Excesso Arrecadacao")
    private BigDecimal excessoArecadacao;
    @Etiqueta("Superavit")
    private BigDecimal superAvit;
    @Etiqueta("Anulacao")
    private BigDecimal anulacao;
    @Etiqueta("Operacao de Crédito")
    private BigDecimal operacaoDeCredito;
    @Etiqueta("Reserva de Contingência")
    private BigDecimal reservaDeContingencia;
    @Etiqueta("Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public AtoLegalORC() {
        this.creditoSuplementar = new BigDecimal(0);
        this.creditoEspecial = new BigDecimal(0);
        this.creditoExtraordinario = new BigDecimal(0);
        this.excessoArecadacao = new BigDecimal(0);
        this.superAvit = new BigDecimal(0);
        this.anulacao = new BigDecimal(0);
        this.operacaoDeCredito = new BigDecimal(0);
        this.reservaDeContingencia= new BigDecimal(0);
        dataRegistro = new Date();
    }

    public AtoLegalORC(BigDecimal creditoSuplementar, BigDecimal creditoEspecial, BigDecimal creditoExtraordinario, BigDecimal excessoArecadacao, BigDecimal superAvit, BigDecimal anulacao, BigDecimal operacaoDeCredito, AtoLegal atoLegal) {
        this.creditoSuplementar = creditoSuplementar;
        this.creditoEspecial = creditoEspecial;
        this.creditoExtraordinario = creditoExtraordinario;
        this.excessoArecadacao = excessoArecadacao;
        this.superAvit = superAvit;
        this.anulacao = anulacao;
        this.operacaoDeCredito = operacaoDeCredito;
        this.atoLegal = atoLegal;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAnulacao() {
        return anulacao;
    }

    public void setAnulacao(BigDecimal anulacao) {
        this.anulacao = anulacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public BigDecimal getCreditoEspecial() {
        return creditoEspecial;
    }

    public void setCreditoEspecial(BigDecimal creditoEspecial) {
        this.creditoEspecial = creditoEspecial;
    }

    public BigDecimal getCreditoExtraordinario() {
        return creditoExtraordinario;
    }

    public void setCreditoExtraordinario(BigDecimal creditoExtraordinario) {
        this.creditoExtraordinario = creditoExtraordinario;
    }

    public BigDecimal getCreditoSuplementar() {
        return creditoSuplementar;
    }

    public void setCreditoSuplementar(BigDecimal creditoSuplementar) {
        this.creditoSuplementar = creditoSuplementar;
    }

    public BigDecimal getExcessoArecadacao() {
        return excessoArecadacao;
    }

    public void setExcessoArecadacao(BigDecimal excessoArecadacao) {
        this.excessoArecadacao = excessoArecadacao;
    }

    public BigDecimal getOperacaoDeCredito() {
        return operacaoDeCredito;
    }

    public void setOperacaoDeCredito(BigDecimal operacaoDeCredito) {
        this.operacaoDeCredito = operacaoDeCredito;
    }

    public BigDecimal getSuperAvit() {
        return superAvit;
    }

    public void setSuperAvit(BigDecimal superAvit) {
        this.superAvit = superAvit;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public BigDecimal getReservaDeContingencia() {
        return reservaDeContingencia;
    }

    public void setReservaDeContingencia(BigDecimal reservaDeContingencia) {
        this.reservaDeContingencia = reservaDeContingencia;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AtoLegalORC other = (AtoLegalORC) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.dataRegistro != null ? this.dataRegistro.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return id + "";
    }
}
