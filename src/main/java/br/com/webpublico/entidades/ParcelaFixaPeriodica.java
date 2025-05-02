/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * @author daniel
 */
@Entity

@Audited
public class ParcelaFixaPeriodica extends Parcela {

    private static final long serialVersionUID = 1L;
    private Integer diaVencimento;

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    @Override
    public String getLabelVencimento() {
        return "Dia " + diaVencimento;
    }
}
