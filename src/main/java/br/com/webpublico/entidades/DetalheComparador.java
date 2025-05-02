package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 11/06/2015
 * Time: 15:01
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class DetalheComparador implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemComparadorFolha itemComparadorFolha;
    @ManyToOne
    private EventoFP eventoFP;
    private BigDecimal valorBaseWeb;
    private BigDecimal valorBaseTurmalina;
    private BigDecimal valorWeb;
    private BigDecimal valorTurmalina;
    @Transient
    private Long criadoEm = System.nanoTime();

    public DetalheComparador() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemComparadorFolha getItemComparadorFolha() {
        return itemComparadorFolha;
    }

    public void setItemComparadorFolha(ItemComparadorFolha itemComparadorFolha) {
        this.itemComparadorFolha = itemComparadorFolha;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public BigDecimal getValorBaseWeb() {
        return valorBaseWeb;
    }

    public void setValorBaseWeb(BigDecimal valorBaseWeb) {
        this.valorBaseWeb = valorBaseWeb;
    }

    public BigDecimal getValorBaseTurmalina() {
        return valorBaseTurmalina;
    }

    public void setValorBaseTurmalina(BigDecimal valorBaseTurmalina) {
        this.valorBaseTurmalina = valorBaseTurmalina;
    }

    public BigDecimal getValorWeb() {
        return valorWeb;
    }

    public void setValorWeb(BigDecimal valorWeb) {
        this.valorWeb = valorWeb;
    }

    public BigDecimal getValorTurmalina() {
        return valorTurmalina;
    }

    public void setValorTurmalina(BigDecimal valorTurmalina) {
        this.valorTurmalina = valorTurmalina;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
