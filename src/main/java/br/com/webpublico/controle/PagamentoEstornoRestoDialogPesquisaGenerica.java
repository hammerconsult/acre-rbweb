package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/11/13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PagamentoEstornoRestoDialogPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return " inner join obj.liquidacao l"
                + " inner join l.empenho e "
                + " inner join e.fornecedor f"
                + " where obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and obj.saldo > 0 "
                + " and obj.status = '" + StatusPagamento.DEFERIDO + "'"
                + " and obj.unidadeOrganizacional = '" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + "' "
                + " and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
    }
}
