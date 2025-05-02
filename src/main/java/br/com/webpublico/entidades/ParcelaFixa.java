/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class ParcelaFixa extends Parcela {

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public ParcelaFixa() {
        setDataRegistro(new Date());
    }

    @Override
    public String getLabelVencimento() {
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        return ft.format(vencimento);
    }

    @Override
    public Date getVencimento() {
        return this.vencimento;
    }
}
