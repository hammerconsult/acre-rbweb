package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity

@Audited
@Etiqueta("Momória de Cáculo de I.P.T.U.")
@GrupoDiagrama(nome = "Licitacao")
public class MemoriaCaluloIPTU implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoCalculo evento;
    @ManyToOne
    private CalculoIPTU calculoIPTU;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private Construcao construcao;

    public MemoriaCaluloIPTU() {
        this.criadoEm = System.nanoTime();
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoCalculo getEvento() {
        return evento;
    }

    public void setEvento(EventoCalculo evento) {
        this.evento = evento;
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

    public CalculoIPTU getCalculoIPTU() {
        return calculoIPTU;
    }

    public void setCalculoIPTU(CalculoIPTU calculoIPTU) {
        this.calculoIPTU = calculoIPTU;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(obj, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return evento.getDescricao() + ": " + valor;
    }
}
