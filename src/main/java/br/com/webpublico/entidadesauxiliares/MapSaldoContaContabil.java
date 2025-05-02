/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.IdentidadeDaEntidade;

import javax.persistence.Transient;
import java.util.Date;

/**
 * @author JaimeDias
 */
public class MapSaldoContaContabil {

    private Date datasaldo;
    private Conta conta;
    private UnidadeOrganizacional unidade;
    @Transient
    private Long criadoEm;

    public MapSaldoContaContabil() {
        criadoEm = System.nanoTime();
    }

    public MapSaldoContaContabil(Date dataSaldo, Conta conta, UnidadeOrganizacional unidade) {
        this.datasaldo = datasaldo;
        this.conta = conta;
        this.unidade = unidade;
        criadoEm = System.nanoTime();
    }

    public Date getDatasaldo() {
        return datasaldo;
    }

    public void setDatasaldo(Date datasaldo) {
        this.datasaldo = datasaldo;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return datasaldo.toString() + conta + unidade;
    }
}
