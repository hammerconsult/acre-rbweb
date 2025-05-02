package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Entity
@Cacheable
@Audited
public class EventoCalculoBCI implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoConfiguradoBCI eventoCalculo;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public EventoCalculoBCI() {
        criadoEm = System.nanoTime();
        valor = BigDecimal.ZERO;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoConfiguradoBCI getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoConfiguradoBCI eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getValorFormatado() {
        DecimalFormat df = new DecimalFormat(eventoCalculo.getPattern());
        return df.format(valor.setScale(4, RoundingMode.HALF_UP));
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

}
