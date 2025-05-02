/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemAposentadoria implements ItemValorPrevidencia, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    private BigDecimal valor;
    @ManyToOne
    private Aposentadoria aposentadoria;
    @ManyToOne
    private EventoFP eventoFP;
    @ManyToOne
    private ReajusteMediaAposentadoria reajusteRecebido;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private Long criadoEm;

    public ItemAposentadoria() {
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public ReajusteMediaAposentadoria getReajusteRecebido() {
        return reajusteRecebido;
    }

    public void setReajusteRecebido(ReajusteMediaAposentadoria reajusteRecebido) {
        this.reajusteRecebido = reajusteRecebido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public VinculoFP getVinculoFP() {
        return aposentadoria;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public void setVinculoFP(VinculoFP vinculoFP) {
        if (vinculoFP instanceof Aposentadoria) {
            setAposentadoria((Aposentadoria) vinculoFP);
        }
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
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

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
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
        return "Início: " + Util.dateToString(inicioVigencia) + " - R$: " + valor;
    }

    public boolean temReajusteRecebido() {
        return reajusteRecebido != null;
    }
}
