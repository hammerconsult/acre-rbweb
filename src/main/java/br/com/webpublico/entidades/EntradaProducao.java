/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author cheles
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais por Produção")
public class EntradaProducao extends EntradaMaterial implements Serializable {

    @OneToOne
    private Producao producao;

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
    }
}
