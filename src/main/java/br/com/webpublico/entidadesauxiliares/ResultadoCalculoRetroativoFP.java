/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * @author peixe
 */
public class ResultadoCalculoRetroativoFP {

    Date dataInicialCalculoRetroativo;
    Date hoje;

    public Date getDataInicialCalculoRetroativo() {
        return dataInicialCalculoRetroativo;
    }

    public ResultadoCalculoRetroativoFP(Date hoje) {
        this.hoje = hoje;
    }

    public void setDataInicialCalculoRetroativo(Date dataInicialCalculoRetroativo) {
        this.dataInicialCalculoRetroativo = dataInicialCalculoRetroativo;
    }

    public boolean temCalculoRetroativo() {
        return hoje != null && dataInicialCalculoRetroativo != null && dataInicialCalculoRetroativo.before(hoje);
    }
}
