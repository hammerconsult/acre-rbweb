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
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class FiancaBancariaCaucao extends CaucaoContrato {

    @ManyToOne
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Agência")
    private Agencia agencia;
    @Tabelavel(ordemApresentacao = 6)
    @Etiqueta("Número da Fiança")
    private Integer numeroFianca;
    @Transient
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Banco")
    private Banco banco;

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public Integer getNumeroFianca() {
        return numeroFianca;
    }

    public void setNumeroFianca(Integer numeroFianca) {
        this.numeroFianca = numeroFianca;
    }

    @Override
    public Banco getBanco() {
        try{
            banco = getAgencia().getBanco();
        } catch (NullPointerException npe){
        }
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }
}
