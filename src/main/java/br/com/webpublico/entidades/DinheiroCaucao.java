/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class DinheiroCaucao extends CaucaoContrato {

    @ManyToOne
    @Etiqueta("Conta Corrente Bancária")
    @Tabelavel(ordemApresentacao = 6)
    private ContaCorrenteBancaria contaCorrenteBancaria;
    @Transient
    @Etiqueta("Banco")
    @Tabelavel(ordemApresentacao = 4)
    private Banco banco;
    @Transient
    @Etiqueta("Agência")
    @Tabelavel(ordemApresentacao = 5)
    private Agencia agencia;

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
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

    public Agencia getAgencia() {
        try{
            agencia = getContaCorrenteBancaria().getAgencia();
        } catch (NullPointerException npe){
        }
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }
}
