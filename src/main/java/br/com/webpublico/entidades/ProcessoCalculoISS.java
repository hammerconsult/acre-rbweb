/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
public class ProcessoCalculoISS extends ProcessoCalculo {
    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculoISS", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoISS> calculos;
    private Integer mesReferencia;
    private String observacoes;

    public List<CalculoISS> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoISS> calculos) {
        this.calculos = calculos;
    }

    public Integer getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Integer mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
