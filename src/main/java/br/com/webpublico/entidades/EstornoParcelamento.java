/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class EstornoParcelamento extends OperacaoParcelaValorDivida {

    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
