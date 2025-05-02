package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cacheable
@Audited
public class EventoConfiguradoIPTU implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoCalculo eventoCalculo;
    @ManyToOne
    private ConfiguracaoEventoIPTU configuracaoEventoIPTU;
    private Long criadoEm;

    public EventoConfiguradoIPTU() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public ConfiguracaoEventoIPTU getConfiguracaoEventoIPTU() {
        return configuracaoEventoIPTU;
    }

    public void setConfiguracaoEventoIPTU(ConfiguracaoEventoIPTU configuracaoEventoIPTU) {
        this.configuracaoEventoIPTU = configuracaoEventoIPTU;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum TipoLancamento {
        TRIBUTO, MEMORIA;
    }
}
