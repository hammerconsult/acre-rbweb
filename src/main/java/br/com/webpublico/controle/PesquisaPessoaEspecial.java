/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package br.com.webpublico.controle;

import br.com.webpublico.entidades.UnidadeExterna;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.PerfilEnum;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PesquisaPessoaEspecial extends ComponentePesquisaGenerico {

    @Override
    public void getCampos() {
        itens = Lists.newArrayList();
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.nome", "Nome", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.cpf", "CPF", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.email", "E-mail", String.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.unidadeExterna.pessoaJuridica.razaoSocial", "Unidade Externa", UnidadeExterna.class, false, true));

        itensOrdenacao = Lists.newArrayList();
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.nome", "Nome", String.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.cpf", "CPF", String.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.email", "E-mail", String.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.unidadeExterna.pessoaJuridica.razaoSocial", "Unidade Externa", UnidadeExterna.class, false, true));
    }

    @Override
    public String getHqlConsulta() {
        return "select new PessoaFisica(obj.id, obj.nome, obj.cpf) from PessoaFisica obj ";
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += " and '" + PerfilEnum.PERFIL_ESPECIAL.name() + "' in elements(obj.perfis)";
        return condicao;
    }
}
