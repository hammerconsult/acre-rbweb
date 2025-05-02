/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Andre
 */
@ManagedBean
@ViewScoped
public class PessoaPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    public static final String aliasPessoa = "pessoa";
    public static final String aliasPF = "pf";
    public static final String aliasPJ = "pj";
    private Boolean somenteAtivas = false;

    public void setSomenteAtivas(Boolean somenteAtivas) {
        this.somenteAtivas = somenteAtivas;
    }

    @Override
    public String getHqlConsulta() {
        return "select " + aliasPessoa + " from Pessoa " + aliasPessoa + " ";
    }

    @Override
    public String getHqlContador() {
        return "select count(" + aliasPessoa + ".id)  from " + classe.getSimpleName() + " " + aliasPessoa + " ";
    }

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("nome", "Nome", String.class));
        itens.add(new ItemPesquisaGenerica("razaoSocial", "Razão Social", String.class));
        itens.add(new ItemPesquisaGenerica("cpf", "CPF", String.class));
        itens.add(new ItemPesquisaGenerica("cnpj", "CNPJ", String.class));
        if (!somenteAtivas) {
            itens.add(new ItemPesquisaGenerica("situacaoCadastralPessoa", "Situação", SituacaoCadastralPessoa.class, true, true));
        }
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
    }

    @Override
    public String getComplementoQuery() {
        if (!somenteAtivas) {
            return " where " + montaCondicao() + montaOrdenacao();
        } else {
            return " where " + montaCondicao() + " and " + aliasPessoa + ".situacaoCadastralPessoa = '" + SituacaoCadastralPessoa.ATIVO.name() + "' " + montaOrdenacao();

        }
    }

    @Override
    public String montaCondicao() {
        String condicao = "";
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
            if (validaItem(dataTablePesquisaGenerico)) {
                try {
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("nome")) {
                        condicao += " (" + aliasPessoa + " in (select " + aliasPF + " from PessoaFisica " + aliasPF + " where lower(" + aliasPF + ".nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%')) ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("razaoSocial")) {
                        condicao += " (" + aliasPessoa + " in (select " + aliasPJ + " from PessoaJuridica " + aliasPJ + " where lower(" + aliasPJ + ".razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%')))";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("cpf")) {
                        condicao += " " + aliasPessoa + " in (select " + aliasPF + " from PessoaFisica " + aliasPF + " where (replace(replace(replace(" + aliasPF + ".cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("cnpj")) {
                        condicao += " " + aliasPessoa + " in (select " + aliasPJ + " from PessoaJuridica " + aliasPJ + " where (replace(replace(replace(" + aliasPJ + ".cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))";
                    }
                    if (!somenteAtivas && dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("situacaoCadastralPessoa")) {
                        condicao += aliasPessoa + ".situacaoCadastralPessoa = '" + dataTablePesquisaGenerico.getValuePesquisa() + "'";
                    }
                    condicao += " and ";
                } catch (NullPointerException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
                }

            }
        }
        condicao += " 1=1 ";
        return condicao;
    }

    @Override
    protected String montaOrdenacao() {
        return " order by pessoa.nome ";
    }

    @Override
    public String visualizar() {
        return super.visualizar();
    }

    @Override
    public void adicionarCampoOrdenacao() {
        getItemOrdenacao().setCondicao(getItemOrdenacao().getCondicao() + " " + getOrdenacao());
        if (podeAdicionarCampoOrdenacao(getItemOrdenacao())) {
            getCamposOrdenacao().add(getItemOrdenacao());
            setItemOrdenacao(new ItemPesquisaGenerica());
            setItemOrdenacaoSelecionado(null);
        } else {
            FacesContext.getCurrentInstance().addMessage(getIdComponente() + "btnAddOrdenacao", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar!", " Campo selecionado já foi adicionado na lista."));
        }
    }

    @Override
    public void prepararConfiguracaoRelatorio() {
        if (classe != Pessoa.class) {
            novoRelatorioGenericoTabela();
            novoRelatorioGenericoUnicoRegistro();
        }
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        try {
            Object[] retorno = facade.filtarComContadorDeRegistros(sql, sqlCount, null, inicio, maximoRegistrosTabela);

            lista = (ArrayList) retorno[0];
            super.setTotalDeRegistrosExistentes(((Long) retorno[1]).intValue());

            if (lista.size() > 0) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
        }
    }
}
