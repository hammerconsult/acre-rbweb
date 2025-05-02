/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Fabio
 */
@ManagedBean
@ViewScoped
public class PesquisaUsuarioSistema extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.login", "Login", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.pessoaFisica.nome", "Nome", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.pessoaFisica.cpf", "CPF", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("uni.descricao", "Unidade Organizacional", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("grupo.nome", "Grupo de Usuário", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Protocolo", null, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Materiais", null, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Licitação", null, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Patrimônio", null, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Financeiro", null, false, true));
        itens.add(new ItemPesquisaGenerica("", "Gestor de Orçamento", null, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.login", "Login", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("pessoaFisica.nome", "Nome", String.class, false, true));
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct new UsuarioSistema(" +
            " obj.id," +
            " obj.login," +
            " obj.pessoaFisica.nome," +
            " obj.expira," +
            " obj.dataCadastro) " +
            " from UsuarioSistema obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) from UsuarioSistema obj ";
    }

    @Override
    public String getComplementoQuery() {
        Boolean isFiltroPorGestor = Boolean.FALSE;
        String nossoComplemento = "";
        for (DataTablePesquisaGenerico campo : camposPesquisa) {
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Protocolo")) {
                nossoComplemento += " and usuUni.gestorProtocolo = java.lang.Boolean.TRUE ";
                isFiltroPorGestor = Boolean.TRUE;
            }
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Materiais")) {
                nossoComplemento += " and usuUni.gestorMateriais = java.lang.Boolean.TRUE ";
                isFiltroPorGestor = Boolean.TRUE;
            }
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Licitação")) {
                nossoComplemento += " and usuUni.gestorLicitacao = java.lang.Boolean.TRUE ";
                isFiltroPorGestor = Boolean.TRUE;
            }
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Patrimônio")) {
                nossoComplemento += " and usuUni.gestorPatrimonio = java.lang.Boolean.TRUE ";
                isFiltroPorGestor = Boolean.TRUE;
            }
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Financeiro")) {
                nossoComplemento += " and usuUni.gestorFinanceiro = java.lang.Boolean.TRUE ";
                isFiltroPorGestor = Boolean.TRUE;
            }
            if (campo.getItemPesquisaGenerica().getLabel().equals("Gestor de Orçamento")) {
                nossoComplemento += " and usuUni.gestorOrcamento = java.lang.Boolean.TRUE";
                isFiltroPorGestor = Boolean.TRUE;
            }
        }

        String condicao = montaCondicao() + nossoComplemento;
        String ordenar = montaOrdenacao();
        String hql = "";

        if (condicao.contains("grupo.nome")) {
            hql += " join obj.grupos grupo ";
        }
        if (condicao.contains("uni.descricao") ||  isFiltroPorGestor) {
            hql += " join obj.usuarioUnidadeOrganizacional usuUni " +
                " join usuUni.unidadeOrganizacional uni ";
        }
        hql += " where " + condicao + ordenar;
        return hql;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
    }

    @Override
    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor,
                                    boolean isDate, boolean stringExata) {
        if (nomeCampo.contains("uni.descricao")) {
            return "(" + nomeCampo + " like '%" + intervalo.toLowerCase().trim() + "%')";
        } else {
            return super.processaIntervalo(nomeCampo, intervalo, isString, isValor, isDate, stringExata);
        }
    }
}
