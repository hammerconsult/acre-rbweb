/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.RBTransGeradorDeTaxas;
import br.com.webpublico.interfaces.RBTransProcesso;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Entity

@Table(name = "PROCCALCSEGVIACREDENCIAL")
@Etiqueta(value = "Processo de Cálculo de Segunda Via de Credencial")
public class ProcessoCalculoSegundaViaCredencial extends ProcessoCalculoRBTrans implements RBTransProcesso {

    @ManyToOne
    private SegundaViaCredencial segundaViaCredencial;

    public SegundaViaCredencial getSegundaViaCredencial() {
        return segundaViaCredencial;
    }

    public void setSegundaViaCredencial(SegundaViaCredencial segundaViaCredencial) {
        this.segundaViaCredencial = segundaViaCredencial;
    }

    @Override
    public PermissaoTransporte obterPermissaoTransporte() {
        return getSegundaViaCredencial().getPermissaoTransporte();
    }

    @Override
    public long obterIdDoGeradorDeTaxas() {
        return segundaViaCredencial.getId();
    }

    @Override
    public void atribuirGeradorDeTaxas(RBTransGeradorDeTaxas entidade) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RBTransGeradorDeTaxas obterGeradorDeTaxas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
