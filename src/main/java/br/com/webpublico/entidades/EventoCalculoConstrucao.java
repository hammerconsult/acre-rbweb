package br.com.webpublico.entidades;

import br.com.webpublico.enums.QualidadeDaConstrucao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Cacheable
@Audited
public class EventoCalculoConstrucao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoConfiguradoBCI eventoCalculo;
    @ManyToOne
    private Construcao construcao;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public EventoCalculoConstrucao() {
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

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
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
        String retorno = valor.toString();
        switch (eventoCalculo.getIdentificacao()) {
            case EVENTO_COMPLEMENTAR:
                return eventoCalculo.formataNumero(valor) + getComplementoDoValor();
            case FATOR_CORRECAO:
                return String.valueOf(valor.intValue()) + getComplementoDoValor();
            case VALOR_VENAL:
                return eventoCalculo.formataNumero(valor) + getComplementoDoValor();
        }
        return retorno ;

    }

    public String getComplementoDoValor() {
        switch (eventoCalculo.getIdentificacao()) {
            case EVENTO_COMPLEMENTAR:
                return "";
            case FATOR_CORRECAO:
                return "-" +QualidadeDaConstrucao.getQualidade(valor.intValue());
            case VALOR_VENAL:
                return "";
            default:
                return "";
        }
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
