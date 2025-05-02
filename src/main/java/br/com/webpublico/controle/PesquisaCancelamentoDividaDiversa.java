package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Fabio
 */
@ManagedBean
@ViewScoped
public class PesquisaCancelamentoDividaDiversa extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("exercicio.ano", "Exercício", Integer.class));
        itens.add(new ItemPesquisaGenerica("numeroLancamento", "Número", Integer.class));
        itens.add(new ItemPesquisaGenerica("dataLancamento", "Data de Lançamento", Date.class));
        itens.add(new ItemPesquisaGenerica("tipoDividaDiversa", "Tipo de Dívida Diversa", TipoDividaDiversa.class, true));
        itens.add(new ItemPesquisaGenerica("dataVencimento", "Data de Vencimento", Date.class));
        itens.add(new ItemPesquisaGenerica("situacao", "Situação", SituacaoCalculoDividaDiversa.class, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("exercicio.ano", "Exercício", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("numeroLancamento", "Número", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataLancamento", "Data de Lançamento", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("tipoDividaDiversa", "Tipo de Dívida Diversa", TipoDividaDiversa.class, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataVencimento", "Data de Vencimento", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("situacao", "Situação", SituacaoCalculoDividaDiversa.class, true));
    }

    @Override
    public String getHqlConsulta() {
        //return "select new CalculoDividaDiversa(obj.id, obj.pessoa, obj.cadastro, obj.exercicio, obj.numeroLancamento, obj.tipoDividaDiversa, obj.situacao) "
        return "select new CalculoDividaDiversa(obj.id, obj.dataLancamento, obj.dataVencimento, pes, cad, "
                + " obj.exercicio, obj.numeroLancamento, obj.tipoDividaDiversa, obj.situacao) "
                + " from CalculoDividaDiversa obj "
                + " left join obj.pessoa pes "
                + " left join obj.cadastro cad ";
    }

    @Override
    public String montaCondicao() {
        return super.montaCondicao()
                + " and (obj.situacao = '" + SituacaoCalculoDividaDiversa.EFETIVADO.name() + "') ";
    }

}
