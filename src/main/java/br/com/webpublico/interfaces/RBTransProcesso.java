/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.PermissaoTransporte;

/**
 * @author cheles
 */
public interface RBTransProcesso {

    public void atribuirGeradorDeTaxas(RBTransGeradorDeTaxas entidade);

    public RBTransGeradorDeTaxas obterGeradorDeTaxas();

    public abstract PermissaoTransporte obterPermissaoTransporte();

    public abstract long obterIdDoGeradorDeTaxas();
}
