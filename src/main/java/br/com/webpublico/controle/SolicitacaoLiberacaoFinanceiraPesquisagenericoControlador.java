package br.com.webpublico.controle;

import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 14/11/13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SolicitacaoLiberacaoFinanceiraPesquisagenericoControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId()
            + "  and (obj.status = '" + StatusSolicitacaoCotaFinanceira.A_LIBERAR.name() + "' OR obj.status = '" + StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE.name() + "')";
        return condicao;
    }
}
