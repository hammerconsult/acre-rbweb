/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Table(name = "CONFHORARIOEXPEDIENTE")
@Etiqueta("Configuração Horário Expediente")
public class ConfiguracaoHorarioExpediente extends ConfiguracaoModulo implements Serializable {

    @Tabelavel
    @Etiqueta("Início Expediente")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaInicialManha;
    @Tabelavel
    @Etiqueta("Início Intervalo")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaFinalManha;
    @Tabelavel
    @Etiqueta("Término Intervalo")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaInicialTarde;
    @Tabelavel
    @Etiqueta("Término Expediente")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaFinalTarde;

    public ConfiguracaoHorarioExpediente() {
    }

    public Date getHoraFinalManha() {
        return horaFinalManha;
    }

    public void setHoraFinalManha(Date horaFinalManha) {
        this.horaFinalManha = horaFinalManha;
    }

    public Date getHoraFinalTarde() {
        return horaFinalTarde;
    }

    public void setHoraFinalTarde(Date horaFinalTarde) {
        this.horaFinalTarde = horaFinalTarde;
    }

    public Date getHoraInicialManha() {
        return horaInicialManha;
    }

    public void setHoraInicialManha(Date horaInicialManha) {
        this.horaInicialManha = horaInicialManha;
    }

    public Date getHoraInicialTarde() {
        return horaInicialTarde;
    }

    public void setHoraInicialTarde(Date horaInicialTarde) {
        this.horaInicialTarde = horaInicialTarde;
    }
}
