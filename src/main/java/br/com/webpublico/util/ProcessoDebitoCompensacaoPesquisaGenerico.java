/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.entidades.ProcessoDebito;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.negocios.ProcessoDebitoFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
public class ProcessoDebitoCompensacaoPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;

    private String tipoProcesso;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.codigo", "Código", Long.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.lancamento", "Data de lançamento", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício", Integer.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.situacao", "Situação", SituacaoProcessoDebito.class, true, false));
        itensOrdenacao.addAll(itens);
        itens.add(new ItemPesquisaGenerica("cad.cmc", "C.M.C.", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cad.bci", "Inscrição Imobiliária", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.razaoSocial", "Razão Social do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.nome", "Nome do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.cpf", "CPF do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("pessoa.cnpj", "CNPJ do Contribuinte", String.class, false, true));
    }

    @Override
    public String getHqlConsulta() {
        return " select distinct new ProcessoDebito(obj.id,"
            + " obj.codigo,"
            + " obj.tipo,"
            + " obj.lancamento,"
            + " coalesce(obj.numeroProtocolo, ''),"
            + " obj.situacao, "
            + " obj.exercicio.ano) from ProcessoDebito obj";
    }

    @Override
    public String getComplementoQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" left  join c.cadastro cad ");
        sb.append(" inner  join c.pessoas pc ");
        sb.append(" inner  join pc.pessoa pessoa ");
        sb.append(" where ");
        sb.append(montaCondicao());
        sb.append(montaOrdenacao());

        return sb.toString();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        for (ProcessoDebito obj : (List<ProcessoDebito>) lista) {
            List<String> cadastros = processoDebitoFacade.recuperarCadastrosDoProcesso(obj);
            if (cadastros.size() > 10) {
                obj.setCadastros(cadastros.subList(0, 9));
                obj.getCadastros().add("...");
            } else {
                obj.setCadastros(cadastros);
            }
        }
    }

    @Override
    protected String montaOrdenacao() {
        if (camposOrdenacao != null) {
            if (!camposOrdenacao.isEmpty()) {
                String ordenar = " order by ";
                for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
                    if (itemPesquisaGenerica.getCondicao() != null) {
                        if (!itemPesquisaGenerica.getCondicao().trim().isEmpty()) {
                            String alias = "";
                            ordenar += alias + primeiraLetraMinuscula(itemPesquisaGenerica.getCondicao()) + " " + itemPesquisaGenerica.getTipoOrdenacao() + ",";
                        }
                    }
                }

                if (ordenar.equals(" order by ")) {
                    return ordenacaoPadrao();
                } else {
                    return ordenar.substring(0, ordenar.length() - 1);
                }
            }
        }
        return ordenacaoPadrao();
    }

    @Override
    public String getHqlContador() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(distinct obj.id) from ProcessoDebito obj ");
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" left  join c.cadastro cad ");
        sb.append(" inner join c.pessoas pc ");
        sb.append(" inner join pc.pessoa pessoa ");
        return sb.toString();
    }

    @Override
    public String montaCondicao() {
        String valor;
        String condicao = super.montaCondicao();
        condicao += " and tipo = '" + TipoProcessoDebito.COMPENSACAO.name() + "'";
        for (DataTablePesquisaGenerico obj : camposPesquisa) {
            if (obj.getItemPesquisaGenerica().getCondicao().equals("cad.bci")) {
                valor = obj.getValuePesquisa().toLowerCase();
                if (valor != null && valor != "") {
                    condicao = condicao.replace("lower(to_char(cad.bci)) LIKE '%" + valor + "%' and ", condicaoInscricaoImobiliaria(valor));
                } else {
                    condicao = condicao.replace("cad.bci is null and", condicaoInscricaoImobiliaria(valor));
                }
                break;
            } else if (obj.getItemPesquisaGenerica().getCondicao().equals("cad.cmc")) {
                valor = obj.getValuePesquisa().toLowerCase();
                if (valor != null && valor != "") {
                    condicao = condicao.replace("lower(to_char(cad.cmc)) LIKE '%" + valor + "%' and ", condicaoInscricaoCadastral(valor));
                } else {
                    condicao = condicao.replace("cad.cmc is null and", condicaoInscricaoCadastral(valor));
                }
                break;
            }
        }
        return condicao;
    }

    public String condicaoInscricaoImobiliaria(String valor) {
        StringBuilder sb = new StringBuilder();
        sb.append(" cad.id in (");
        sb.append("select id from CadastroImobiliario where inscricaoCadastral = '" + valor + "'");
        sb.append(") and ");
        return sb.toString();
    }

    public String condicaoInscricaoCadastral(String valor) {
        StringBuilder sb = new StringBuilder();
        sb.append(" cad.id in (");
        sb.append("select id from CadastroEconomico where inscricaoCadastral = '" + valor + "'");
        sb.append(") and ");
        return sb.toString();
    }

    public void novoTipoParaPesquisa(String tipo) {
        this.tipoProcesso = tipo;
    }

    public String getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(String tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }
}
