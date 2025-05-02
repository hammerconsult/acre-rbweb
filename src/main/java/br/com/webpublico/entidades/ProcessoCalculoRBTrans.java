/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta(value = "Processo de Cálculo RBTrans")
public  class ProcessoCalculoRBTrans extends ProcessoCalculo implements Serializable{

    @OneToMany(mappedBy = "processoCalculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoRBTrans> calculos;
    private Boolean ehLicenciamento;
    @OneToOne
    private ParametrosTransitoRBTrans parametroTransito;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ProcessoCalculoRBTrans() {
        calculos = new ArrayList<>();
    }


    public List<CalculoRBTrans> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoRBTrans> calculos) {
        this.calculos = calculos;
    }

    public Boolean getEhLicenciamento() {
        return ehLicenciamento;
    }

    public void setEhLicenciamento(Boolean ehLicenciamento) {
        this.ehLicenciamento = ehLicenciamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParametrosTransitoRBTrans getParametroTransito() {
        return parametroTransito;
    }

    public void setParametroTransito(ParametrosTransitoRBTrans parametroTransito) {
        this.parametroTransito = parametroTransito;
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
        return this.getClass().getSimpleName() + ".id = " + this.getId();
    }


}
