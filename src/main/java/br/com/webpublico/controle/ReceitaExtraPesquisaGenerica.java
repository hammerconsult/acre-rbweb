/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Adriano Jandre
 */
@ManagedBean
@ViewScoped
public class ReceitaExtraPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void limpaCamposDaView() {
        super.limpaCampos();
        super.limpaCamposDaView();
        getCampos();
    }

    @Override
    public void getCampos() {
        itens = Lists.newArrayList();
        itensOrdenacao = Lists.newArrayList();

        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));

        criarItemSituacao();
        criarItemRetencao();
        criarItemSaldo();

        super.getCampos();

        ItemPesquisaGenerica exercicio = new ItemPesquisaGenerica("to_char(obj.dataReceita, 'yyyy')", "Exercício", Exercicio.class, false, false);
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(exercicio);
        dataTablePesquisaGenerico.setValuePesquisa(getSistemaControlador().getExercicioCorrente().getAno().toString());
        dataTablePesquisaGenerico.setPodeRemover(false);
        itens.add(exercicio);
        camposPesquisa.add(dataTablePesquisaGenerico);
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
        return " where obj.unidadeOrganizacional.id = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + " " +
                " and " + montaCondicao() + montaOrdenacao();
    }
}
