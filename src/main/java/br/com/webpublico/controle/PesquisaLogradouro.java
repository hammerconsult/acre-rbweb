/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoLogradouro;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class PesquisaLogradouro extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return "select new Logradouro(obj.id, st, obj.codigo, obj.tipoLogradouro, obj.nome, obj.nomeUsual, obj.nomeAntigo, obj.situacao) " +
                " from " + classe.getSimpleName() + " obj " +
                " left join obj.setor st " ;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
    }

    @Override
    public void getCampos() {
        super.getCampos();
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        ItemPesquisaGenerica item = new ItemPesquisaGenerica();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(item);
        dataTablePesquisaGenerico.setValuePesquisa(SituacaoLogradouro.ATIVO.name());
        item.setCondicao("obj.situacao");
        item.setLabel("Situação");
        item.setTipo(SituacaoLogradouro.class);
        item.seteEnum(true);
        super.getItens().add(item);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);

    }


}
