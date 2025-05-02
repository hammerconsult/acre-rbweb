/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrazoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Calendar;
import java.util.Date;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class ParcelaPeriodica extends Parcela {

    private Integer quantidade;
    @Enumerated(EnumType.STRING)
    private TipoPrazoParcela tipoPrazoParcela;

    public ParcelaPeriodica() {

    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TipoPrazoParcela getTipoPrazoParcela() {
        return tipoPrazoParcela;
    }

    public void setTipoPrazoParcela(TipoPrazoParcela tipoPrazoParcela) {
        this.tipoPrazoParcela = tipoPrazoParcela;
    }

    @Override
    public Date getVencimento() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (tipoPrazoParcela == TipoPrazoParcela.DIARIO) {
            calendar.add(Calendar.DATE, quantidade);
        } else if (tipoPrazoParcela == TipoPrazoParcela.MENSAL) {
            calendar.add(Calendar.MONTH, quantidade);
        }
        return calendar.getTime();
    }

    @Override
    public String getLabelVencimento() {
        if (tipoPrazoParcela == TipoPrazoParcela.DIARIO) {
            return "Com intervalo de " + quantidade + " dias";
        } else {
            return "Com intervalo de " + quantidade + " meses";
        }

    }
}
