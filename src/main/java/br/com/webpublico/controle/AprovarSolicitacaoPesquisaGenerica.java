/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.SituacaoSolicitacao;
import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
public class AprovarSolicitacaoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

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
        return  " where obj.unidadeOrganizacional.id in (" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ") "
                + " and (obj.status = '" + StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE.name() + "'"
                + " or obj.status = '"   + StatusSolicitacaoCotaFinanceira.A_APROVAR.name() + "'"
                + " or obj.status = '"   + StatusSolicitacaoCotaFinanceira.A_LIBERAR.name() + "')"
                + " and obj.exercicio.id = "   + getSistemaControlador().getExercicioCorrente().getId()
                + " and " + montaCondicao() + montaOrdenacao();
    }
}
