/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PagamentoRestoPagarLiquidacaoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return  " inner join obj.exercicio e "
                + " where e.ano = " + getSistemaControlador().getExercicioCorrente().getAno()
                + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and obj.saldo > 0 "
                + " and obj.unidadeOrganizacional = '" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + "' "
                + " and " + montaCondicao() + montaOrdenacao();
    }
}
