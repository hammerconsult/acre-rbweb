/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoReceitaExtra;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Adriano Jandre
 */
@ManagedBean
@ViewScoped
public class TransporteReceitaExtraPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));

        criarItemSituacao();
        criarItemRetencao();
        criarItemSaldo();

        super.getCampos();
    }

    @Override
    public String montaCondicao() {
        String s = super.montaCondicao();
        return s.replace("obj.saldo = true", "obj.saldo > 0 ")
                .replace("obj.saldo = false", "obj.saldo = 0 ")
                .replace("obj.retencaoPgto = true", "obj.retencaoPgto is not null")
                .replace("obj.retencaoPgto = false", "obj.retencaoPgto is null");
    }

    private void criarItemSaldo() {
        ItemPesquisaGenerica saldo = new ItemPesquisaGenerica("obj.saldo", "Possui Saldo?", Boolean.class, false, false, "Não", "Sim");
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(saldo);
        dataTablePesquisaGenerico.setValuePesquisa("true");
        super.getItens().add(saldo);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);
    }

    private void criarItemRetencao() {
        ItemPesquisaGenerica retencao = new ItemPesquisaGenerica("obj.retencaoPgto", "Possui Pagamento?", Boolean.class, false, false, "Não", "Sim");
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(retencao);
        dataTablePesquisaGenerico.setValuePesquisa("true");
        super.getItens().add(retencao);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);
    }

    private void criarItemSituacao() {
        ItemPesquisaGenerica situacao = new ItemPesquisaGenerica("obj.situacaoReceitaExtra", "Situação", SituacaoReceitaExtra.class, true);
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(situacao);
        dataTablePesquisaGenerico.setValuePesquisa(SituacaoReceitaExtra.ABERTO.name());
        super.getItens().add(situacao);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);
    }

    @Override
    public String getComplementoQuery() {
        return " where to_char (obj.dataReceita, 'yyyy') = " + super.getSistemaControlador().getExercicioCorrente().getAno().toString() +
                " and obj.unidadeOrganizacional.id = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + " " +
                " and " + montaCondicao() + montaOrdenacao();
    }
}
