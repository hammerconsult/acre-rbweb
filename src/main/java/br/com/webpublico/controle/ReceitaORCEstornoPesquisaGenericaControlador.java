package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 12/03/14
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ReceitaORCEstornoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return " where obj.saldo > 0 AND obj.receitaDeIntegracao = false AND obj.integracaoTribCont = null "
                + " and obj.unidadeOrganizacional = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId()
                + " and obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and " + montaCondicao() + montaOrdenacao();
    }

}
