/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.webpublico.enums.TipoAcaoCobranca;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Julio Cesar
 */
@ManagedBean
@ViewScoped
public class PesquisaBaixaNotificacaoControlador extends ComponentePesquisaGenericoRemovendoFields {

    @Override
    public void getCampos() {

        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.programacaoCobranca.numero", "N° da programação", String.class,false,true));
        itens.add(new ItemPesquisaGenerica("obj.programacaoCobranca.descricao", "Descrição da programação", String.class,false,true));
        itens.add(new ItemPesquisaGenerica("tipoAcaoCobranca", "Tipo de Ação de Cobrança", TipoAcaoCobranca.class,true));
        itens.add(new ItemPesquisaGenerica("emitirGuia", "Cobrança com Guia", Boolean.class,false,false,"Não","Sim"));
        itens.add(new ItemPesquisaGenerica("damAgrupado", "DAM Agrupado", Boolean.class,false,false,"Não","Sim"));
        itens.add(new ItemPesquisaGenerica("vencimentoDam", "Vencimento do DAM", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.programacaoCobranca.ultimaSituacao", "Situação", TipoSituacaoProgramacaoCobranca.class,true,true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.programacaoCobranca.numero", "N° da programação", String.class,false,true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.programacaoCobranca.descricao", "Descrição da programação", String.class,false,true));
        itensOrdenacao.add(new ItemPesquisaGenerica("tipoAcaoCobranca", "Tipo de Ação de Cobrança", TipoAcaoCobranca.class,true));
        itensOrdenacao.add(new ItemPesquisaGenerica("emitirGuia", "Cobrança com Guia", Boolean.class,false,false,"Não","Sim"));
        itensOrdenacao.add(new ItemPesquisaGenerica("damAgrupado", "DAM Agrupado", Boolean.class,false,false,"Não","Sim"));
        itensOrdenacao.add(new ItemPesquisaGenerica("vencimentoDam", "Vencimento do DAM", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.programacaoCobranca.ultimaSituacao", "Situação", TipoSituacaoProgramacaoCobranca.class,true,true));
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct new NotificacaoCobrancaAdmin(obj.id, obj.programacaoCobranca.ultimaSituacao ,obj) " +
                "from " + classe.getSimpleName() +
                " obj join obj.itemNotificacaoLista item " +
                " join item.aceite aceite ";
    }


    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        return condicao ;
    }

    @Override
    public List<String> getNomesDosFieldsParaRemover() {
        List<String> nomes = new ArrayList<>();
        nomes.add("aceite");
        return nomes;
    }
}
