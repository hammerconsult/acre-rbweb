/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Evento do relatório do iptu")
public class EventoRelatorioIPTU implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private EventoCalculo eventoCalculo;
    @ManyToOne
    private ConfiguracaoRelatorioIPTU configuracaoRelatorioIPTU;
    @Transient
    private Long criadoEm;

    public EventoRelatorioIPTU(){
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public ConfiguracaoRelatorioIPTU getConfiguracaoRelatorioIPTU() {
        return configuracaoRelatorioIPTU;
    }

    public void setConfiguracaoRelatorioIPTU(ConfiguracaoRelatorioIPTU configuracaoRelatorioIPTU) {
        this.configuracaoRelatorioIPTU = configuracaoRelatorioIPTU;
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

    @Override
    public String toString() {
        return id == null ? "Abastecimento ainda não gravado" : "Abastecimento código " + id;
    }

}
