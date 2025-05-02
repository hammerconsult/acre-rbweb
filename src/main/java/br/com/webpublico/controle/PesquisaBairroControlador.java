package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 26/03/14
 * Time: 18:41
 */
@ManagedBean
@ViewScoped
public class PesquisaBairroControlador extends ComponentePesquisaGenerico implements Serializable{
    @Override
    public void getCampos() {
        super.getCampos();
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        ItemPesquisaGenerica item = new ItemPesquisaGenerica();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(item);
        dataTablePesquisaGenerico.setValuePesquisa(Boolean.TRUE.toString());
        item.setCondicao("obj.ativo");
        item.setLabel("Ativo");
        item.setTipo(Boolean.class);
        item.setLabelBooleanFalse("Não");
        item.setLabelBooleanTrue("Sim");
        super.getItens().add(item);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);

    }
}
