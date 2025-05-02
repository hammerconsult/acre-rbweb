/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Etiqueta("Configuração para o Relatório de IPTU")
public class ConfiguracaoRelatorioIPTU implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @OneToMany(mappedBy = "configuracaoRelatorioIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoRelatorioIPTU> eventos;
    @Transient
    private Long criadoEm;

    public ConfiguracaoRelatorioIPTU() {
        this.eventos = Lists.newArrayList();
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EventoRelatorioIPTU> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoRelatorioIPTU> eventos) {
        this.eventos = eventos;
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
