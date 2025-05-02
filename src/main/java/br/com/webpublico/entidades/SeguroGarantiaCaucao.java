/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class SeguroGarantiaCaucao extends CaucaoContrato {

    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Nome do Segurado")
    private String nomeSegurado;
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Nome da Seguradora")
    private String nomeSeguradora;
    @Etiqueta("Número do Seguro")
    @Tabelavel(ordemApresentacao = 6)
    private Integer numeroSeguro;

    public String getNomeSegurado() {
        return nomeSegurado;
    }

    public void setNomeSegurado(String nomeSegurado) {
        this.nomeSegurado = nomeSegurado;
    }

    public Integer getNumeroSeguro() {
        return numeroSeguro;
    }

    public void setNumeroSeguro(Integer numeroSeguro) {
        this.numeroSeguro = numeroSeguro;
    }

    public String getNomeSeguradora() {
        return nomeSeguradora;
    }

    public void setNomeSeguradora(String nomeSeguradora) {
        this.nomeSeguradora = nomeSeguradora;
    }
}
