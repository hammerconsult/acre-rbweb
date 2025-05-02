package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.SituacaoConta;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Criado por Mateus
 * Data: 10/01/2017.
 */
@ManagedBean
@ViewScoped
public class ContaCorrenteBancariaPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itensOrdenacao.clear();
        itens.clear();
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("b.numeroBanco", "Número do Banco", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("ag.numeroAgencia", "Número da Agência", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.modalidadeConta", "Tipo da Conta", ModalidadeConta.class, true, false));
        itens.add(new ItemPesquisaGenerica("obj.numeroConta", "Número da Conta", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.situacao", "Situação", SituacaoConta.class, true, false));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return "select new ContaCorrenteBancaria(obj) from " + classe.getSimpleName() + " obj "
            + " INNER JOIN obj.agencia ag "
            + " INNER JOIN ag.banco b ";
    }

    @Override
    public String getHqlContador() {
        return super.getHqlContador()
            + " INNER JOIN obj.agencia ag "
            + " INNER JOIN ag.banco b";
    }
}
