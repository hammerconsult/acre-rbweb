/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;

/**
 * @author cheles
 */
public interface RBTransGeradorDeTaxas {

    public TaxaTransito obterTaxaEquivalente(ParametrosTransitoRBTrans parametrosTransitoRBTrans);

    public RBTransProcesso obterNovoProcesso();

    public boolean temProcessoDeCalculo();

    public void novaListaDeProcessos();

    public void adicionarNaListaDeProcessos(ProcessoCalculo processo);

    public CalculoRBTrans obterCalculoDaLista();

    public PermissaoTransporte obterPermissaoTransporte();

    public void executaQuandoPagarGuia();
}
