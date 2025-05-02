/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.SituacoesAcaoFiscal;

import java.util.Comparator;

/**
 * @author fabio
 */
public class OrdenaSituacaoAcaoFiscalPorData implements Comparator<SituacoesAcaoFiscal> {

    @Override
    public int compare(SituacoesAcaoFiscal o1, SituacoesAcaoFiscal o2) {
        return o1.getData().compareTo(o2.getData());
    }

}
