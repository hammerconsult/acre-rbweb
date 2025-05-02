package br.com.webpublico.controle;

import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.util.Date;

/**
 * @author Julio Cesar
 */
@ManagedBean
@ViewScoped
public class PesquisaNFSEspecial extends ComponentePesquisaGenerico {
    @Override
    public void getCampos() {

        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.emissao", "Data de Emissão da Nota", Date.class));
        itens.add(new ItemPesquisaGenerica("ex.ano", "Exercício", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.numero", "Número da Nota", Long.class));
        itens.add(new ItemPesquisaGenerica("obj.prestador", "Prestador de Serviço", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.tomador", "Tomador de Serviço", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.situacao", "Situação", NFSAvulsa.Situacao.class, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.emissao", "Data de Emissão da Nota", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("ex.ano", "Exercício", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numero", "Número da Nota", Long.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.prestador", "Prestador de Serviço", Pessoa.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.tomador", "Tomador de Serviço", Pessoa.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.situacao", "Situação", NFSAvulsa.Situacao.class, true));
    }

    @Override
    public String getHqlConsulta() {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct obj from NFSAvulsa obj");
        hql.append(" inner join obj.exercicio ex");
        hql.append(" inner join obj.prestador prestador");
        hql.append(" inner join obj.tomador tomador");
        hql.append(" inner join obj.itens iten");
        return hql.toString();
    }

    @Override
    public String getHqlContador() {

        StringBuilder hql = new StringBuilder();
        hql.append("select distinct count(obj.id) from NFSAvulsa obj");
        hql.append(" inner join obj.exercicio ex");
        hql.append(" inner join obj.prestador prestador");
        hql.append(" inner join obj.tomador tomador");
        hql.append(" inner join obj.itens iten");

        return hql.toString();
    }
}

