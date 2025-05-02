/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Parâmetros de Trânsito: Configuração")
@Table(name = "PARAMETROSTRANSITOCONFIG")
@Inheritance(strategy = InheritanceType.JOINED)
public class ParametrosTransitoConfiguracao extends ParametrosTransitoRBTrans {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametrosTransitoConfiguracao")
    @Etiqueta("Taxas de Trânsito")
    private List<TaxaTransito> taxasTransito;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametrosTransitoConfiguracao")
    private List<ParametrosTransitoTermos> parametrosTermos;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametrosTransitoConfiguracao")
    private List<ParametrosValoresTransferencia> parametrosValoresTransferencias;

    public List<TaxaTransito> getTaxasTransito() {
        if (taxasTransito != null) {
            Collections.sort(taxasTransito);
        }
        return taxasTransito;
    }

    public void setTaxasTransito(List<TaxaTransito> taxasTransito) {
        this.taxasTransito = taxasTransito;
    }

    public List<ParametrosTransitoTermos> getParametrosTermos() {
        if (parametrosTermos != null) {
            Collections.sort(parametrosTermos);
        }
        return parametrosTermos;
    }

    public void setParametrosTermos(List<ParametrosTransitoTermos> parametrosTermos) {
        this.parametrosTermos = parametrosTermos;
    }

    public List<ParametrosValoresTransferencia> getParametrosValoresTransferencias() {
        if (parametrosValoresTransferencias != null) {
            Collections.sort(parametrosValoresTransferencias);
        }
        return parametrosValoresTransferencias;
    }

    public void setParametrosValoresTransferencias(List<ParametrosValoresTransferencia> parametrosValoresTransferencias) {
        this.parametrosValoresTransferencias = parametrosValoresTransferencias;
    }
}
