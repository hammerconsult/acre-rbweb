/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoItemBordero;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
public class ItemBorderoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return "select distinct obj  from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id)  from " + classe.getSimpleName() + " obj ";
    }


    @Override
    public String getComplementoQuery() {
        return " left join obj.listaPagamentos lp "
            + " left join obj.listaPagamentosExtra lpe "
            + " left join obj.listaTransferenciaFinanceira ltf "
            + " left join obj.listaTransferenciaMesmaUnidade ltfm "
            + " left join obj.listaLiberacaoCotaFinanceira llf "
            + " where ((lp.situacaoItemBordero  = '" + SituacaoItemBordero.BORDERO + "' or lp.situacaoItemBordero = '" + SituacaoItemBordero.DEFERIDO + "' or lp.situacaoItemBordero = '" + SituacaoItemBordero.INDEFIRIDO + "')"
            + "     or (lpe.situacaoItemBordero = '" + SituacaoItemBordero.BORDERO + "' or lpe.situacaoItemBordero = '" + SituacaoItemBordero.DEFERIDO + "' or lpe.situacaoItemBordero = ' " + SituacaoItemBordero.INDEFIRIDO + "')"
            + "     or (ltf.situacaoItemBordero = '" + SituacaoItemBordero.BORDERO + "' or ltf.situacaoItemBordero = '" + SituacaoItemBordero.DEFERIDO + "' or ltf.situacaoItemBordero = ' " + SituacaoItemBordero.INDEFIRIDO + "')"
            + "     or (ltfm.situacaoItemBordero = '" + SituacaoItemBordero.BORDERO + "' or ltfm.situacaoItemBordero = '" + SituacaoItemBordero.DEFERIDO + "' or ltfm.situacaoItemBordero = ' " + SituacaoItemBordero.INDEFIRIDO + "')"
            + "     or (llf.situacaoItemBordero = '" + SituacaoItemBordero.BORDERO + "' or llf.situacaoItemBordero = '" + SituacaoItemBordero.DEFERIDO + "' or llf.situacaoItemBordero = ' " + SituacaoItemBordero.INDEFIRIDO + "'))"
            + " AND obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
            + " and " + montaCondicao() + montaOrdenacao();
    }
}
