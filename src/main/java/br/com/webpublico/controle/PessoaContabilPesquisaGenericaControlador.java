package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoClasseCredor;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 26/07/14
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PessoaContabilPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public String getHqlConsulta() {
        return " select obj from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getHqlContador() {
        return " select count(distinct obj.id) from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " left join obj.classeCredorPessoas classes where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        lista = new ArrayList<>(new HashSet(lista));
    }

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica codigo = new ItemPesquisaGenerica();
        codigo.setCondicao("classes.classeCredor.codigo");
        codigo.setLabel("Código da Classe Credor");
        codigo.setTipo(String.class);
        codigo.seteEnum(false);
        codigo.setPertenceOutraClasse(true);
        super.getItens().add(codigo);
        super.getItensOrdenacao().add(codigo);

        ItemPesquisaGenerica descricao = new ItemPesquisaGenerica();
        descricao.setCondicao("classes.classeCredor.descricao");
        descricao.setLabel("Descrição da Classe Credor");
        descricao.setTipo(String.class);
        descricao.seteEnum(false);
        descricao.setPertenceOutraClasse(true);
        super.getItens().add(descricao);
        super.getItensOrdenacao().add(descricao);

        ItemPesquisaGenerica tipo = new ItemPesquisaGenerica();
        tipo.setCondicao("classes.classeCredor.tipoClasseCredor");
        tipo.setLabel("Tipo de Classe Credor");
        tipo.setTipo(TipoClasseCredor.class);
        tipo.seteEnum(true);
        tipo.setPertenceOutraClasse(true);
        super.getItens().add(tipo);
        super.getItensOrdenacao().add(tipo);
    }


}
